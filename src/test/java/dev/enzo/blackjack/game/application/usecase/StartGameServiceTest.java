package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.dto.StartGameResult;
import dev.enzo.blackjack.game.application.port.out.GameRepository;
import dev.enzo.blackjack.game.domain.model.Game;
import dev.enzo.blackjack.game.domain.model.GameStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StartGameServiceTest {

    @Test
    void shouldStartAndSaveNewGame() {
        GameRepository repository = mock(GameRepository.class);
        StartGameService service = new StartGameService(repository);

        StartGameResult result = service.startGame();

        assertNotNull(result.gameId());
        assertEquals(GameStatus.IN_PROGRESS, result.status());

        ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);

        verify(repository).save(eq(result.gameId()), gameCaptor.capture());

        Game savedGame = gameCaptor.getValue();

        assertEquals(GameStatus.IN_PROGRESS, savedGame.getStatus());
    }

    @Test
    void shouldGenerateDifferentIdsForEachGame() {
        GameRepository repository = mock(GameRepository.class);
        StartGameService service = new StartGameService(repository);

        StartGameResult firstGame = service.startGame();
        StartGameResult secondGame = service.startGame();

        assertNotEquals(firstGame.gameId(), secondGame.gameId());
        assertEquals(GameStatus.IN_PROGRESS, firstGame.status());
        assertEquals(GameStatus.IN_PROGRESS, secondGame.status());

        verify(repository).save(eq(firstGame.gameId()), any(Game.class));
        verify(repository).save(eq(secondGame.gameId()), any(Game.class));
    }

}
