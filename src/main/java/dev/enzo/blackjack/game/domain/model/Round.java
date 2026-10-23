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

    public Hand getPlayerHand() {
        return playerHand;
    }

    public Hand getDealerHand() {
        return dealerHand;
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
        this.status = RoundStatus.PLAYER_TURN;
    }

    public void hit() {
        if (this.status != RoundStatus.PLAYER_TURN) {
            throw new IllegalStateException("It's not player turn!");
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
            throw new IllegalStateException("It's not the dealer turn!");
        }

        while(this.dealerHand.calculateScore() < 17) {
            this.dealerHand.addCard(this.deck.cardDraw());
        }
    }

}
