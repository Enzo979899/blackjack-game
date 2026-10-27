package dev.enzo.blackjack.game.domain.model;

public record GameStatisticsSnapshot(int wins, int losses, int pushes) implements GameStatisticsView {

    @Override
    public int getWins() {
        return wins;
    }

    @Override
    public int getLosses() {
        return losses;
    }

    @Override
    public int getPushes() {
        return pushes;
    }

}
