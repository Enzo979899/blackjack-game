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

        game.startRound();

        assertNotNull(game.getCurrentRound());
        assertEquals(RoundStatus.WAITING_TO_START, game.getCurrentRound().getStatus());
    }

    @Test
    void shouldNotStartRoundWhenGameIsWaiting() {
        Game game = new Game();

        assertEquals(GameStatus.WAITING, game.getStatus());

        assertThrows(IllegalStateException.class, game::startRound);

        assertNull(game.getCurrentRound());
    }

    @Test
    void shouldNotStartNewRoundWhenPreviousRoundIsNotFinished() {
        Game game = new Game();

        game.startGame();
        game.startRound();

        assertThrows(IllegalStateException.class, game::startRound);
    }

    @Test
    void shouldStartNewRoundWhenPreviousRoundIsFinished() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round firstRound = game.getCurrentRound();

        firstRound.start();

        if (firstRound.getStatus() == RoundStatus.PLAYER_TURN) {
            firstRound.stand();
            firstRound.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, firstRound.getStatus());

        game.recordRoundResult();

        game.startRound();

        Round secondRound = game.getCurrentRound();

        assertNotSame(firstRound, secondRound);
        assertEquals(RoundStatus.WAITING_TO_START, secondRound.getStatus());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void shouldNotFinishGameWhenRoundIsActive() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        round.start();

        assertEquals(RoundStatus.PLAYER_TURN, round.getStatus());
        assertThrows( IllegalStateException.class, game::finish);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void shouldFinishGameWhenCurrentRoundIsFinished() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        round.start();

        if (round.getStatus() == RoundStatus.PLAYER_TURN) {

            round.stand();

            round.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, round.getStatus());

        game.recordRoundResult();

        game.finish();

        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    @Test
    void shouldNotStartRoundWhenGameIsFinished() {
        Game game = new Game();

        game.startGame();

        game.finish();

        assertEquals(GameStatus.FINISHED, game.getStatus());
        assertThrows(IllegalStateException.class, game::startRound);
        assertNull(game.getCurrentRound());
        assertEquals(GameStatus.FINISHED, game.getStatus());
    }

    @Test
    void shouldInitializeGameWithEmptyStatistics() {
        Game game = new Game();

        GameStatisticsView statistics = game.getStatistics();

        assertNotNull(statistics);
        assertEquals(0, statistics.getWins());
        assertEquals(0, statistics.getLosses());
        assertEquals(0, statistics.getPushes());
    }

    @Test
    void shouldRecordFinishedRoundResultInGameStatistics() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        round.start();

        if (round.getStatus() == RoundStatus.PLAYER_TURN) {

            round.stand();

            round.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertNotNull(round.getResult());

        game.recordRoundResult();

        int totalResults = game.getStatistics().getWins() + game.getStatistics().getLosses() +
                game.getStatistics().getPushes();

        assertEquals(1, totalResults);
    }

    @Test
    void shouldNotRecordSameRoundTwice() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        round.start();

        if (round.getStatus() == RoundStatus.PLAYER_TURN) {

            round.stand();

            round.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, round.getStatus());

        game.recordRoundResult();

        assertThrows(IllegalStateException.class, game::recordRoundResult);

        int totalResults = game.getStatistics().getWins() + game.getStatistics().getLosses()
                + game.getStatistics().getPushes();

        assertEquals(1, totalResults);
    }

    @Test
    void shouldRecordResultsFromTwoConsecutiveRounds() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round firstRound = game.getCurrentRound();

        firstRound.start();

        if (firstRound.getStatus() == RoundStatus.PLAYER_TURN) {

            firstRound.stand();

            firstRound.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, firstRound.getStatus());

        game.recordRoundResult();

        game.startRound();

        Round secondRound = game.getCurrentRound();

        secondRound.start();

        if (secondRound.getStatus() == RoundStatus.PLAYER_TURN) {

            secondRound.stand();

            secondRound.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, secondRound.getStatus());

        game.recordRoundResult();

        int totalResults = game.getStatistics().getWins() + game.getStatistics().getLosses()
                + game.getStatistics().getPushes();

        assertEquals(2, totalResults);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertNotSame(firstRound, secondRound);
    }

    @Test
    void shouldNotRecordResultWhenRoundIsNotFinished() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        assertEquals(RoundStatus.WAITING_TO_START, round.getStatus());
        assertThrows(IllegalStateException.class, game::recordRoundResult);
        assertEquals(0, game.getStatistics().getWins());
        assertEquals(0, game.getStatistics().getLosses());
        assertEquals(0, game.getStatistics().getPushes());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void shouldNotRecordResultWhenNoRoundExists() {
        Game game = new Game();

        game.startGame();

        assertNull(game.getCurrentRound());
        assertThrows(IllegalStateException.class, game::recordRoundResult);
        assertEquals(0, game.getStatistics().getWins());
        assertEquals(0, game.getStatistics().getLosses());
        assertEquals(0, game.getStatistics().getPushes());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
    }

    @Test
    void shouldNotFinishGameWhenRoundResultIsNotRecorded() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round round = game.getCurrentRound();

        round.start();

        if (round.getStatus() == RoundStatus.PLAYER_TURN) {

            round.stand();

            round.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertThrows(IllegalStateException.class, game::finish);
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());

        int totalResults = game.getStatistics().getWins() + game.getStatistics().getLosses() +
                game.getStatistics().getPushes();

        assertEquals(0, totalResults);
    }

    @Test
    void shouldNotStartNewRoundWhenPreviousResultIsNotRecorded() {
        Game game = new Game();

        game.startGame();

        game.startRound();

        Round firstRound = game.getCurrentRound();

        firstRound.start();

        if (firstRound.getStatus() == RoundStatus.PLAYER_TURN) {

            firstRound.stand();

            firstRound.playDealerTurn();
        }

        assertEquals(RoundStatus.FINISHED, firstRound.getStatus());
        assertThrows(IllegalStateException.class, game::startRound);
        assertSame(firstRound, game.getCurrentRound());
        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());

        int totalResults = game.getStatistics().getWins() + game.getStatistics().getLosses() +
                game.getStatistics().getPushes();

        assertEquals(0, totalResults);
    }

    @Test
    void shouldExposeGameStatisticsAsReadOnlyView() {
        Game game = new Game();

        GameStatisticsView statistics = game.getStatistics();

        assertNotNull(statistics);
        assertEquals(0, statistics.getWins());
        assertEquals(0, statistics.getLosses());
        assertEquals(0, statistics.getPushes());
    }

    @Test
    void shouldNotExposeMutableGameStatistics() {
        Game game = new Game();

        GameStatisticsView statistics = game.getStatistics();

        assertInstanceOf(GameStatisticsSnapshot.class, statistics);
        assertFalse(statistics instanceof GameStatistics);
    }
}
