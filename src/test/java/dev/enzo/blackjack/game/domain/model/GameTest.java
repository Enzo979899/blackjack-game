package dev.enzo.blackjack.game.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void shouldCreateGameWithWaitingStatus() {
        Game game = new Game();

        assertEquals(GameStatus.WAITING, game.getStatus());
    }

    @Test
    void shouldCreateGameWithoutActiveRound() {
        Game game = new Game();

        assertNull(game.getCurrentRound());
    }

    @Test
    void shouldStartGame() {
        Game game = new Game();

        game.startGame();

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void shouldNotStartGameTwice() {
        Game game = new Game();

        game.startGame();

        assertThrows(IllegalStateException.class, game::startGame);
    }

    @Test
    void shouldFinishGame() {
        Game game = new Game();

        game.startGame();

        game.finish();

        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    @Test
    void shouldNotFinishGameBeforeStarting() {
        Game game = new Game();

        assertThrows(IllegalStateException.class, game::finish);
    }

    @Test
    void shouldNotFinishGameTwice() {
        Game game = new Game();

        game.startGame();

        game.finish();

        assertThrows(IllegalStateException.class, game::finish);
    }

    @Test
    void shouldStartFirstRound() {
        Game game = new Game();

        game.startGame();

        game.start();

        assertNotNull(game.getCurrentRound());
        assertEquals(RoundStatus.WAITING_TO_START, game.getCurrentRound().getStatus());
    }

    @Test
    void shouldNotStartRoundWhenGameIsWaitingOrFinished() {
        Game game = new Game();

        assertThrows(IllegalStateException.class, game::start);
    }

    @Test
    void shouldNotStartRoundWhenGameIsFinishing() {
        Game game = new Game();

        assertThrows(IllegalStateException.class, game::start);
    }

    @Test
    void shouldNotStartNewRoundWhenPreviousRoundIsNotFinished() {
        Game game = new Game();

        game.startGame();
        game.start();

        assertThrows(IllegalStateException.class, game::start);
    }

}
