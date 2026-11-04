package dev.enzo.blackjack.player.application.port.in;

import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;

public interface RecordRoundResultUseCase {

    void recordResult(RoundFinishedEvent event);

}
