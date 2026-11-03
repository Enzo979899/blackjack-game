
package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.in.HitRoundUseCase;
import dev.enzo.blackjack.game.application.port.out.RoundEventPublisher;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.game.domain.model.RoundStatus;

import java.util.Objects;
import java.util.UUID;

public class HitRoundService implements HitRoundUseCase {

    private final RoundRepository roundRepository;
    private final RoundEventPublisher eventPublisher;

    public HitRoundService(RoundRepository roundRepository, RoundEventPublisher eventPublisher) {
        this.roundRepository = Objects.requireNonNull(roundRepository);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    @Override
    public void hit(UUID roundId) {
        Objects.requireNonNull(roundId, "Round ID cannot be null");

        Round round = roundRepository.findById(roundId).orElseThrow(() ->
                new IllegalArgumentException("Round not found: " + roundId));

        round.hit();

        roundRepository.save(round);

        if (round.getStatus() == RoundStatus.FINISHED) {
            eventPublisher.publish(new RoundFinishedEvent(round.getRoundId(), round.getPlayerId(), round.getResult()));
        }
    }
}


