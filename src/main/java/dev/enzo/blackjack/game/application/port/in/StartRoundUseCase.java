package dev.enzo.blackjack.game.application.port.in;

import java.util.UUID;

public interface StartRoundUseCase {

    UUID startRound(UUID playerId);

}
