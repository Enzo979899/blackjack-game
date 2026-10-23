package dev.enzo.blackjack.game.domain.model;

public class Game {
    private GameStatus status;
    private Round currentRound;

    public Game() {
        this.status = GameStatus.WAITING;
        this.currentRound = null;
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
        this.status = GameStatus.FINISHED;
    }

    public void start() {
        if (this.status != GameStatus.IN_PROGRESS) {
            throw new IllegalStateException("Game is not in progress!");
        }

        if (this.currentRound != null && this.currentRound.getStatus() != RoundStatus.FINISHED) {
            throw new IllegalStateException("Previous round is not finished!");
        }
        this.currentRound = new Round(new Deck());
    }
}
