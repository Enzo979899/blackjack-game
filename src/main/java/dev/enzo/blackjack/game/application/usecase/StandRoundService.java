package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.in.StandRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.RoundEventPublisher;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.game.domain.model.RoundStatus;

import java.util.Objects;
import java.util.UUID;

public class StandRoundService implements StandRoundUseCase {
    private final RoundRepository roundRepository;
    private final RoundEventPublisher eventPublisher;

    public StandRoundService(RoundRepository roundRepository, RoundEventPublisher eventPublisher) {
        this.roundRepository = Objects.requireNonNull(roundRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void stand(UUID roundId) {
        Objects.requireNonNull(roundId, "Round ID cannot be null");

        Round round = roundRepository.findById(roundId).orElseThrow(() ->
                new IllegalArgumentException("Round not found: " + roundId));

        round.stand();
        round.playDealerTurn();

        roundRepository.save(round);

        if (round.getStatus() == RoundStatus.FINISHED) {
            eventPublisher.publish(new RoundFinishedEvent(round.getRoundId(), round.getPlayerId(), round.getResult()));
        }
    }

}

