package dev.enzo.blackjack.game.domain.model;

public class GameStatistics implements GameStatisticsView {
    private int wins;
    private int losses;
    private int pushes;

    public GameStatistics() {
        this.wins = 0;
        this.losses = 0;
        this.pushes = 0;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public int getPushes() {
        return pushes;
    }

    public void recordResult(RoundResult result) {
        if (result == null) {
            throw new IllegalArgumentException("Round result cannot be null");
        }

        switch (result) {
            case PLAYER_WIN -> this.wins++;
            case DEALER_WIN -> this.losses++;
            case PUSH -> this.pushes++;
        }
    }
}
