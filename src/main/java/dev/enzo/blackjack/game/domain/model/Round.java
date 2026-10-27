package dev.enzo.blackjack.game.domain.model;


public class Round {
    private final Hand playerHand;
    private final Hand dealerHand;
    private final Deck deck;
    private RoundStatus status;
    private RoundResult result;

    public Round(Deck deck) {
        if (deck == null) {
            throw new NullPointerException("The deck cannot be null");
        }

        this.deck = deck;
        this.playerHand = new Hand();
        this.dealerHand = new Hand();
        this.status = RoundStatus.WAITING_TO_START;
        this.result = null;

    }

    public HandView getPlayerHand() {
        return new HandSnapshot(this.playerHand.getCards());
    }

    public HandView getDealerHand() {
        return new HandSnapshot(this.dealerHand.getCards());
    }

    public RoundStatus getStatus() {
        return status;
    }

    public RoundResult getResult() {
        return result;
    }

    public void start() {
        if (this.status != RoundStatus.WAITING_TO_START) {
            throw new IllegalStateException("Round is already started!");
        }
        this.playerHand.addCard(this.deck.cardDraw());
        this.dealerHand.addCard(this.deck.cardDraw());
        this.playerHand.addCard(this.deck.cardDraw());
        this.dealerHand.addCard(this.deck.cardDraw());

        if (this.playerHand.isBlackJack() && this.dealerHand.isBlackJack()) {
            this.result = RoundResult.PUSH;
        } else if (this.playerHand.isBlackJack()) {
            this.result = RoundResult.PLAYER_WIN;
        } else if (this.dealerHand.isBlackJack()) {
            this.result = RoundResult.DEALER_WIN;
        }

        if (this.result != null) {
            this.status = RoundStatus.FINISHED;
            return;
        }
        this.status = RoundStatus.PLAYER_TURN;
    }

    public void hit() {
        if (this.status != RoundStatus.PLAYER_TURN) {
            throw new IllegalStateException("It's not player turn!");
        }

        if (this.playerHand.calculateScore() == 21) {
            throw new IllegalStateException("Cannot hit with 21");
        }
        this.playerHand.addCard(this.deck.cardDraw());

        if  (this.playerHand.isBusted()) {
            this.status = RoundStatus.FINISHED;
            this.result = RoundResult.DEALER_WIN;
        }
    }

    public void stand() {
        if (this.status != RoundStatus.PLAYER_TURN) {
            throw new IllegalStateException("It's not the player turn!");
        }
        this.status = RoundStatus.DEALER_TURN;
    }

    public void playDealerTurn() {
        if (this.status != RoundStatus.DEALER_TURN) {
            throw new IllegalStateException("It's not the dealer's turn!");
        }

        while (this.dealerHand.calculateScore() < 17) {
            this.dealerHand.addCard(this.deck.cardDraw());
        }

        int playerScore = this.playerHand.calculateScore();
        int dealerScore = this.dealerHand.calculateScore();

        if (this.dealerHand.isBusted()) {
            this.result = RoundResult.PLAYER_WIN;
        } else if (playerScore > dealerScore) {
            this.result = RoundResult.PLAYER_WIN;
        } else if (dealerScore > playerScore) {
            this.result = RoundResult.DEALER_WIN;
        } else {
            this.result = RoundResult.PUSH;
        }

        this.status = RoundStatus.FINISHED;
    }

}
