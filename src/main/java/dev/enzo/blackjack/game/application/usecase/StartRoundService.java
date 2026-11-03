package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.in.StartRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Deck;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;

import java.util.Objects;
import java.util.UUID;

public class StartRoundService implements StartRoundUseCase {
    private final PlayerRepository playerRepository;
    private final RoundRepository roundRepository;

    public StartRoundService(PlayerRepository playerRepository, RoundRepository roundRepository) {
        this.playerRepository = Objects.requireNonNull(playerRepository);
        this.roundRepository = Objects.requireNonNull(roundRepository);
    }

    @Override
    public UUID startRound(UUID playerId) {
        Objects.requireNonNull(playerId, "Player ID cannot be null");

        if (playerRepository.findById(playerId).isEmpty()) {
            throw new IllegalArgumentException("Player not found: " + playerId);
        }

        UUID roundId = UUID.randomUUID();

        Round round = new Round(roundId, playerId, new Deck());
        round.start();

        roundRepository.save(round);

        return roundId;
    }

}
