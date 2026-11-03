package dev.enzo.blackjack.game.application.port.in;

import java.util.UUID;

public interface HitRoundUseCase {

    void hit(UUID roundId);

}
