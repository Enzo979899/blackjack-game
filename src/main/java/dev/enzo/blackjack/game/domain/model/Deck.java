package dev.enzo.blackjack.game.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards;

    public Deck() {
        this.cards = new ArrayList<>();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public int getRemainingCards() {
        return cards.size();
    }

    public Card cardDraw() {
        if (cards.isEmpty()) {
            throw new IllegalStateException("The deck is empty");
        }
        return cards.removeFirst();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }
}
