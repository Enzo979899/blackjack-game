package dev.enzo.blackjack.game.application.port.in;

import java.util.UUID;

public interface StandRoundUseCase {

    void stand(UUID roundId);

}
