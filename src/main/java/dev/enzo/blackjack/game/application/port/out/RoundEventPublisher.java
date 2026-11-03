package dev.enzo.blackjack.game.application.port.out;

import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;

public interface RoundEventPublisher {

    void publish(RoundFinishedEvent event);

}
