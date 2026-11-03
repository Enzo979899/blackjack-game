package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.in.StartRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.DeckFactory;
import dev.enzo.blackjack.game.application.port.out.RoundEventPublisher;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.Deck;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.game.domain.model.RoundStatus;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;

import java.util.Objects;
import java.util.UUID;

public class StartRoundService implements StartRoundUseCase {
    private final PlayerRepository playerRepository;
    private final RoundRepository roundRepository;
    private final RoundEventPublisher eventPublisher;
    private final DeckFactory deckFactory;


    public StartRoundService(PlayerRepository playerRepository, RoundRepository roundRepository,
                             RoundEventPublisher eventPublisher, DeckFactory deckFactory) {
        this.playerRepository = Objects.requireNonNull(playerRepository);
        this.roundRepository = Objects.requireNonNull(roundRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.deckFactory = Objects.requireNonNull(deckFactory);
    }


    @Override
    public UUID startRound(UUID playerId) {
        Objects.requireNonNull(playerId, "Player ID cannot be null");

        if (playerRepository.findById(playerId).isEmpty()) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }

        UUID roundId = UUID.randomUUID();

        Round round = new Round(roundId, playerId, deckFactory.createDeck());
        round.start();

        roundRepository.save(round);

        if (round.getStatus() == RoundStatus.FINISHED) {
            eventPublisher.publish(new RoundFinishedEvent(round.getRoundId(), round.getPlayerId(), round.getResult()));
        }

        return roundId;
    }

}
