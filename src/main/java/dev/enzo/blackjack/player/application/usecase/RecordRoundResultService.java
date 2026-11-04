package dev.enzo.blackjack.player.application.usecase;

import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.player.application.port.in.RecordRoundResultUseCase;
import dev.enzo.blackjack.player.application.port.out.RoundResultRepository;

import java.util.Objects;

public class RecordRoundResultService implements RecordRoundResultUseCase {
    private final RoundResultRepository roundResultRepository;

    public RecordRoundResultService(RoundResultRepository roundResultRepository) {
        this.roundResultRepository =
                Objects.requireNonNull(roundResultRepository, "RoundResultRepository cannot be null");
    }

    @Override
    public void recordResult(RoundFinishedEvent event) {
        Objects.requireNonNull(event, "RoundFinishedEvent cannot be null");

        roundResultRepository.recordIfAbsent(event.roundId(), event.playerId(), event.result());
    }

}
