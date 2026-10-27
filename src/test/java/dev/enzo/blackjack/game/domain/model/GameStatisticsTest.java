package dev.enzo.blackjack.game.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStatisticsTest {
    @Test
    void shouldInitializeStatisticsWithZeroValues() {
        GameStatistics statistics = new GameStatistics();

        assertEquals(0, statistics.getWins());
        assertEquals(0, statistics.getLosses());
        assertEquals(0, statistics.getPushes());
    }

    @Test
    void shouldIncreaseWinsWhenPlayerWins() {
        GameStatistics statistics = new GameStatistics();

        statistics.recordResult(RoundResult.PLAYER_WIN);

        assertEquals(1, statistics.getWins());
        assertEquals(0, statistics.getLosses());
        assertEquals(0, statistics.getPushes());
    }

    @Test
    void shouldIncreaseLossesWhenDealerWins() {
        GameStatistics statistics = new GameStatistics();

        statistics.recordResult(RoundResult.DEALER_WIN);

        assertEquals(0, statistics.getWins());
        assertEquals(1, statistics.getLosses());
        assertEquals(0, statistics.getPushes());
    }

    @Test
    void shouldIncreasePushesWhenRoundEndsInPush() {
        GameStatistics statistics = new GameStatistics();

        statistics.recordResult(RoundResult.PUSH);

        assertEquals(0, statistics.getWins());
        assertEquals(0, statistics.getLosses());
        assertEquals(1, statistics.getPushes());
    }

    @Test
    void shouldAccumulateResultsFromMultipleRounds() {
        GameStatistics statistics = new GameStatistics();

        statistics.recordResult(RoundResult.PLAYER_WIN);

        statistics.recordResult(RoundResult.DEALER_WIN);

        statistics.recordResult(RoundResult.PLAYER_WIN);

        statistics.recordResult(RoundResult.PUSH);

        assertEquals(2, statistics.getWins());
        assertEquals(1, statistics.getLosses());
        assertEquals(1, statistics.getPushes());
    }

    @Test
    void shouldRejectNullRoundResult() {
        GameStatistics statistics = new GameStatistics();

        assertThrows(IllegalArgumentException.class, () -> statistics.recordResult(null));

        assertEquals(0, statistics.getWins());
        assertEquals(0, statistics.getLosses());
        assertEquals(0, statistics.getPushes());
    }

}