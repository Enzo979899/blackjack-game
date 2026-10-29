package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.dto.StartRoundResult;
import dev.enzo.blackjack.game.application.port.out.GameRepository;
import dev.enzo.blackjack.game.domain.model.Game;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.game.domain.model.RoundStatus;
import dev.enzo.blackjack.game.infrastructure.fake.InMemoryGameRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartRoundServiceTest {

    @Test
    void shouldStartRoundForExistingGame() {
        GameRepository repository = mock(GameRepository.class);
        StartRoundService service = new StartRoundService(repository);

        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.startGame();

        when(repository.findById(gameId)).thenReturn(Optional.of(game));

        StartRoundResult result = service.startRound(gameId);

        assertEquals(gameId, result.gameId());
        assertNotNull(game.getCurrentRound());
        assertEquals(game.getCurrentRound().getStatus(), result.status());
        assertTrue(result.status() == RoundStatus.PLAYER_TURN || result.status() == RoundStatus.FINISHED);

        verify(repository).findById(gameId);
        verify(repository).save(gameId, game);
    }

    @Test
    void shouldThrowExceptionWhenGameDoesNotExist() {
        GameRepository repository = mock(GameRepository.class);
        StartRoundService service = new StartRoundService(repository);

        UUID gameId = UUID.randomUUID();

        when(repository.findById(gameId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.startRound(gameId));

        assertEquals("Game not found!", exception.getMessage());

        verify(repository).findById(gameId);
        verify(repository, never()).save(any(UUID.class), any(Game.class));
    }

    @Test
    void shouldNotStartNewRoundWhenPreviousRoundIsActive() {
        GameRepository repository = mock(GameRepository.class);
        StartRoundService service = new StartRoundService(repository);

        UUID gameId = UUID.randomUUID();
        Game game = new Game();
        game.startGame();
        game.startRound();

        when(repository.findById(gameId)).thenReturn(Optional.of(game));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.startRound(gameId));

        assertEquals("Previous round is not finished!", exception.getMessage());

        verify(repository).findById(gameId);
        verify(repository, never()).save(any(UUID.class), any(Game.class));
    }

    @Test
    void shouldNotStartRoundWhenGameIsNotStarted() {
        GameRepository repository = mock(GameRepository.class);
        StartRoundService service = new StartRoundService(repository);

        UUID gameId = UUID.randomUUID();
        Game game = new Game();

        when(repository.findById(gameId)).thenReturn(Optional.of(game));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.startRound(gameId));

        assertEquals("Game is not in progress!", exception.getMessage());
        assertNull(game.getCurrentRound());

        verify(repository).findById(gameId);
        verify(repository, never()).save(any(UUID.class), any(Game.class));
    }

    @Test
    void shouldNotStartRoundWhenPreviousResultIsNotRecorded() {
        GameRepository repository = mock(GameRepository.class);
        StartRoundService service = new StartRoundService(repository);

        UUID gameId = UUID.randomUUID();
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        round.start();

        if (round.getStatus() == RoundStatus.PLAYER_TURN) {

            round.stand();

            round.playDealerTurn();
        }

        when(repository.findById(gameId)).thenReturn(Optional.of(game));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> service.startRound(gameId));

        assertEquals("Previous round result is not recorded!", exception.getMessage());

        verify(repository).findById(gameId);
        verify(repository, never()).save(any(UUID.class), any(Game.class));
    }


    @Test
    void shouldStartAndPersistRoundUsingInMemoryRepository() {
        InMemoryGameRepository repository = new InMemoryGameRepository();
        StartRoundService service = new StartRoundService(repository);

        UUID gameId = UUID.randomUUID();

        Game game = new Game();
        game.startGame();
        repository.save(gameId, game);

        StartRoundResult result = service.startRound(gameId);

        Game savedGame = repository.findById(gameId).orElseThrow();

        assertSame(game, savedGame);
        assertNotNull(savedGame.getCurrentRound());
        assertEquals(gameId, result.gameId());
        assertEquals(savedGame.getCurrentRound().getStatus(), result.status());
        assertTrue(result.status() == RoundStatus.PLAYER_TURN || result.status() == RoundStatus.FINISHED);

        if (result.status() == RoundStatus.FINISHED) {
            int totalResults = savedGame.getStatistics().getWins() + savedGame.getStatistics().getLosses()
                    + savedGame.getStatistics().getPushes();

            assertEquals(1, totalResults);
        }
    }

}
