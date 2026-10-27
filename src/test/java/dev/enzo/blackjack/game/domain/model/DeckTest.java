package dev.enzo.blackjack.game.domain.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void shouldReturn52Cards() {
        Deck deck = new Deck();

        int remainingCards = deck.getRemainingCards();

        assertEquals(52, remainingCards);
    }

    @Test
    void shouldDecreaseRemainingCardWhenDrawingCards() {
        Deck deck = new Deck();

        Card drawCard = deck.cardDraw();

        assertNotNull(drawCard);
        assertEquals(51, deck.getRemainingCards());
    }

    @Test
    void shouldDraw52UniqueCards() {
        Deck deck = new Deck();
        Set<String> drawnCards = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.cardDraw();
            String identifier = card.getSuit() + "-" + card.getRank();

            drawnCards.add(identifier);
        }

        assertEquals(52, drawnCards.size());
        assertEquals(0, deck.getRemainingCards());
    }

    @Test
    void shouldThrowExceptionWhenDeckIsEmpty() {
        Deck deck = new Deck();

        for (int i = 0; i < 52; i++) {
            deck.cardDraw();
        }

        IllegalStateException exception = assertThrows(IllegalStateException.class, deck::cardDraw);

        assertEquals("The deck is empty", exception.getMessage());
    }

    @Test
    void shouldPreserveAllCardsAfterShuffle() {
        Deck deck = new Deck();

        deck.shuffle();

        Set<String> drawnCards = new HashSet<>();

        for (int i = 0; i < 52; i++) {
            Card card = deck.cardDraw();
            drawnCards.add(card.getSuit() + "-" + card.getRank());
        }

        assertEquals(52, drawnCards.size());
        assertEquals(0, deck.getRemainingCards());
    }

}
