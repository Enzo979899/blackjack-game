package dev.enzo.blackjack.game.domain.model;

public class Game {
    private GameStatus status;
    private Round currentRound;
    private final GameStatistics statistics;
    private boolean roundResultRecorded;

    public Game() {
        this.status = GameStatus.WAITING;
        this.currentRound = null;
        this.statistics = new GameStatistics();
        this.roundResultRecorded = false;
    }

    public GameStatus getStatus() {
        return status;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void startGame() {
        if (this.status != GameStatus.WAITING) {
            throw new IllegalStateException("Game is already started!");
        }
        this.status = GameStatus.IN_PROGRESS;
    }

    public void finish() {
        if (this.status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress!");
        }

        if (this.currentRound != null && this.currentRound.getStatus() != RoundStatus.FINISHED) {
            throw new IllegalStateException("Cannot finish game with an active round!");
        }

        if (this.currentRound != null && !this.roundResultRecorded) {
            throw new IllegalStateException("Cannot finish game with an unrecorded round result!");
        }
        this.status = GameStatus.FINISHED;
    }

    public void startRound() {
        if (this.status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress!");
        }

        if (this.currentRound != null && this.currentRound.getStatus() != RoundStatus.FINISHED) {
            throw new IllegalStateException("Previous round is not finished!");
        }

        if (this.currentRound != null && !this.roundResultRecorded) {
            throw new IllegalStateException("Previous round result is not recorded!");
        }
        Deck deck = new Deck();
        deck.shuffle();

        this.currentRound = new Round(deck);
        this.roundResultRecorded = false;
    }

    public GameStatisticsView getStatistics() {
        return new GameStatisticsSnapshot(this.statistics.getWins(), this.statistics.getLosses(),
                this.statistics.getPushes());
    }

    public void recordRoundResult() {
        if (this.currentRound == null) {
            throw new IllegalStateException("There is no round to record!");
        }

        if (this.currentRound.getStatus() != RoundStatus.FINISHED) {
            throw new IllegalStateException("The round is not finished!");
        }

        if (this.roundResultRecorded) {
            throw new IllegalStateException("Round result is already recorded!");
        }
        this.statistics.recordResult(this.currentRound.getResult());
        this.roundResultRecorded = true;
    }

}
