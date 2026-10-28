package dev.enzo.blackjack.game.application.port.in;

import dev.enzo.blackjack.game.application.dto.StartGameResult;
import dev.enzo.blackjack.game.domain.model.Game;

public interface StartGameUseCase {

    StartGameResult startGame();

}
