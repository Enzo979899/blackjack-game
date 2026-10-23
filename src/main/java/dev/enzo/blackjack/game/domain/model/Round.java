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

}
