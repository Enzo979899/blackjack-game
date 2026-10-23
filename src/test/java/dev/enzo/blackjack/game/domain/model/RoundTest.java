package dev.enzo.blackjack.game.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoundTest {

    @Test
    void shouldCreateRoundWithEmptyHands() {
        Round round = new Round(new Deck());

        assertEquals(0, round.getDealerHand().getCardCount());
        assertEquals(0, round.getPlayerHand().getCardCount());
    }

    @Test
    void shouldCreateRoundWithoutResult() {
        Round round = new Round(new Deck());

        assertNull(round.getResult());
    }

    @Test
    void shouldRejectNullDeck() {
        assertThrows(NullPointerException.class, () -> new Round(null));
    }

    @Test
    void shouldCreateRoundWithInitialStatus() {
        Round round = new Round(new Deck());

        assertEquals(RoundStatus.WAITING_TO_START, round.getStatus());
    }
}