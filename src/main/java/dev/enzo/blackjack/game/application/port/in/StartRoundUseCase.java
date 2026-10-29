package dev.enzo.blackjack.game.application.port.in;

import dev.enzo.blackjack.game.application.dto.StartRoundResult;

import java.util.UUID;

public interface StartRoundUseCase {

    StartRoundResult startRound(UUID gameId);

}
