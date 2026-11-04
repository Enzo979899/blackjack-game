package dev.enzo.blackjack.game.infrastructure.fake;

import dev.enzo.blackjack.game.application.port.out.RoundEventPublisher;
import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.player.application.port.in.RecordRoundResultUseCase;

import java.util.Objects;

public class InMemoryRoundEventPublisher implements RoundEventPublisher {

    private final RecordRoundResultUseCase recordRoundResultUseCase;

    public InMemoryRoundEventPublisher(RecordRoundResultUseCase recordRoundResultUseCase) {
        this.recordRoundResultUseCase = Objects.requireNonNull(recordRoundResultUseCase,
                "RecordRoundResultUseCase cannot be null");
    }

    @Override
    public void publish(RoundFinishedEvent event) {
        recordRoundResultUseCase.recordResult(event);
    }
}
