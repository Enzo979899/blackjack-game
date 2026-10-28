package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.dto.StartGameResult;
import dev.enzo.blackjack.game.application.port.in.StartGameUseCase;
import dev.enzo.blackjack.game.application.port.out.GameRepository;
import dev.enzo.blackjack.game.domain.model.Game;

import java.util.Objects;
import java.util.UUID;

public class StartGameService implements StartGameUseCase {
    private final GameRepository gameRepository;

    public StartGameService(GameRepository gameRepository) {
        this.gameRepository = Objects.requireNonNull(gameRepository);
    }

    @Override
    public StartGameResult startGame() {
        Game game = new Game();
        game.startGame();

        UUID gameId = UUID.randomUUID();

        gameRepository.save(gameId, game);

        return new StartGameResult(gameId, game.getStatus());
    }
}
