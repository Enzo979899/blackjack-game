package dev.enzo.blackjack.game.domain.model;

import org.junit.jupiter.api.Test;
import org.mockito.configuration.IMockitoConfiguration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void shoulStartRound() {
        Round round = new Round(new Deck());

        round.start();

        assertEquals(RoundStatus.PLAYER_TURN, round.getStatus());
    }

    @Test
    void shoulNotStartRoundTwice() {
        Round round = new Round(new Deck());

        round.start();

        assertThrows(IllegalStateException.class, round::start);
    }

    @Test
    void shouldDelaTwoCardsToPlayerWhenRoundStarts() {
        Round round = new Round(new Deck());

        round.start();

        assertEquals(2, round.getPlayerHand().getCardCount());
    }

    @Test
    void shouldDealTwoCardsToDealerWhenRoundStarts() {
        Round round = new Round(new Deck());

        round.start();

        assertEquals(2, round.getDealerHand().getCardCount());
    }

    @Test
    void shouldRemoveFourCardsFromDeckAfterRoundStarts() {
        Deck deck = new Deck();
        Round round = new Round(deck);

        round.start();

        assertEquals(48, deck.getRemainingCards());
    }

    @Test
    void shouldNotDrawMoreCardsWhenStartingRoundTwice() {
        Deck deck = new Deck();
        Round round = new Round(deck);

        round.start();

        assertThrows(IllegalStateException.class, round::start);
        assertEquals(48, deck.getRemainingCards());
    }

    @Test
    void shouldAddCardToPlayerHandWhenHit() {
        Round round = new Round(new Deck());

        round.start();

        round.hit();

        assertEquals(3, round.getPlayerHand().getCardCount());
    }

    @Test
    void shouldNotHitBeforeRoundStarts() {
        Round round = new Round(new Deck());

        assertThrows(IllegalStateException.class, round::hit);
    }

    @Test
    void shouldFinishRoundWhenPlayerBusts() {
        Round round = new Round(new Deck());

        round.start();

        while (!round.getPlayerHand().isBusted()) {
            round.hit();
        }

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertEquals(RoundResult.DEALER_WIN, round.getResult());
    }

    @Test
    void shouldNotAllowHitAfterPlayerBusts() {
        Round round = new Round(new Deck());

        round.start();

        while (!round.getPlayerHand().isBusted()) {
            round.hit();
        }

        assertThrows(IllegalStateException.class, round::hit);
    }

    @Test
    void shouldChangeToDealerTurnWhenPlayerStands() {
        Round round = new Round(new Deck());

        round.start();

        round.stand();

        assertEquals(RoundStatus.DEALER_TURN, round.getStatus());
    }

    @Test
    void shouldNotAllowHitAfterPlayerStands() {
        Round round = new Round(new Deck());

        round.start();

        round.stand();

        assertThrows(IllegalStateException.class, round::hit);
    }

    @Test
    void shouldNotAllowStandTwice() {
        Round round = new Round(new Deck());

        round.start();

        round.stand();

        assertThrows(IllegalStateException.class, round::stand);
    }

    @Test
    void shouldDealerDrawCardWhenScoreIsBelow17() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.CLUBS, Rank.TEN), new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.SPADES, Rank.SEVEN), new Card(Suit.SPADES, Rank.TEN), new Card(Suit.HEARTS, Rank.FOUR));

        Round round = new Round(deck);

        round.start();

        assertEquals(17, round.getPlayerHand().calculateScore());
        assertEquals(16, round.getDealerHand().calculateScore());

        round.stand();

        round.playDealerTurn();

        assertEquals(3, round.getDealerHand().getCardCount());
        assertEquals(20, round.getDealerHand().calculateScore());
    }

    @Test
    void shouldDealerStandWhenScoreIs17() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.HEARTS, Rank.TEN), new Card(Suit.DIAMONDS, Rank.SEVEN),
                new Card(Suit.DIAMONDS, Rank.EIGHT), new Card(Suit.SPADES, Rank.TEN));

        Round round = new Round(deck);

        round.start();

        round.stand();

        round.playDealerTurn();

        assertEquals(17, round.getDealerHand().calculateScore());
        assertEquals(2, round.getDealerHand().getCardCount());

        verify(deck, times(4)).cardDraw();
    }

    @Test
    void shouldPlayerWinswhenDealerBust() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.CLUBS, Rank.TEN), new Card(Suit.DIAMONDS, Rank.SIX),
                new Card(Suit.SPADES, Rank.EIGHT), new Card(Suit.SPADES, Rank.TEN), new Card(Suit.CLUBS, Rank.KING));

        Round round = new Round(deck);

        round.start();

        round.stand();

        round.playDealerTurn();

        assertTrue(round.getDealerHand().isBusted());
        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
    }

    @Test
    void shouldPlayerWinWhenScoreIsHigherThanDealer() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.SPADES, Rank.TEN), new Card(Suit.SPADES, Rank.EIGHT),
                new Card(Suit.DIAMONDS, Rank.TEN), new Card(Suit.HEARTS, Rank.TEN));

        Round round = new Round(deck);

        round.start();

        round.stand();

        round.playDealerTurn();

        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
        assertEquals(RoundStatus.FINISHED, round.getStatus());
    }

    @Test
    void shouldDealerWinWhenScoreIsHigherThanPlayer() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.DIAMONDS, Rank.TEN), new Card(Suit.SPADES, Rank.TEN),
                new Card(Suit.SPADES, Rank.EIGHT), new Card(Suit.SPADES, Rank.KING));

        Round round = new Round(deck);

        round.start();

        round.stand();

        round.playDealerTurn();

        assertEquals(RoundResult.DEALER_WIN, round.getResult());
        assertEquals(RoundStatus.FINISHED, round.getStatus());

    }

    @Test
    void shouldEndRoundWithPushWhenScoresAreSame() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.SPADES, Rank.TEN), new Card(Suit.DIAMONDS, Rank.TEN),
                new Card(Suit.DIAMONDS, Rank.EIGHT), new Card(Suit.CLUBS, Rank.EIGHT));

        Round round = new Round(deck);

        round.start();

        round.stand();

        round.playDealerTurn();

        assertEquals(RoundResult.PUSH, round.getResult());
        assertEquals(RoundStatus.FINISHED, round.getStatus());
    }

    @Test
    void shouldFinishRoundWhenPlayerHasBlackJackAndDealerDoesNot() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.HEARTS, Rank.AS), new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.SPADES, Rank.KING), new Card(Suit.HEARTS, Rank.SEVEN));

        Round round = new Round(deck);

        round.start();

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertEquals(RoundResult.PLAYER_WIN, round.getResult());
    }

    @Test
    void shouldEndRoundWithPushWhenBothHaveBlackJack() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.HEARTS, Rank.AS), new Card(Suit.CLUBS, Rank.KING),
                new Card(Suit.DIAMONDS, Rank.QUEEN), new Card(Suit.DIAMONDS, Rank.AS));

        Round round = new Round(deck);

        round.start();

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertEquals(RoundResult.PUSH, round.getResult());
    }

    @Test
    void shouldDealerWinWhenOnlyDealerHasBlackJack() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.CLUBS, Rank.TEN), new Card(Suit.HEARTS, Rank.AS),
                new Card(Suit.SPADES, Rank.EIGHT), new Card(Suit.DIAMONDS, Rank.KING));

        Round round = new Round(deck);

        round.start();

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertEquals(RoundResult.DEALER_WIN, round.getResult());
    }

    @Test
    void shouldNotCount21WithThreeCardsAsNaturalBlackJack() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.CLUBS, Rank.TEN), new Card(Suit.DIAMONDS, Rank.NINE),
                new Card(Suit.SPADES, Rank.FIVE), new Card(Suit.HEARTS, Rank.SEVEN), new Card(Suit.CLUBS, Rank.SIX));

        Round round = new Round(deck);

        round.start();

        round.hit();

        assertEquals(21, round.getPlayerHand().calculateScore());
        assertEquals(3, round.getPlayerHand().getCardCount());
        assertFalse(round.getPlayerHand().isBlackJack());
        assertEquals(RoundStatus.PLAYER_TURN, round.getStatus());
        assertNull(round.getResult());
    }

    @Test
    void shouldNotAllowHitWhenPlayerHas21Points() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.CLUBS, Rank.TEN), new Card(Suit.DIAMONDS, Rank.NINE),
                new Card(Suit.CLUBS, Rank.FIVE), new Card(Suit.HEARTS, Rank.SEVEN), new Card(Suit.CLUBS, Rank.SIX));

        Round round = new Round(deck);

        round.start();

        round.hit();

        assertEquals(21, round.getPlayerHand().calculateScore());
        assertThrows(IllegalStateException.class, round::hit);
    }

    @Test
    void shouldNotDrawCardWhenPlayerAlreadyHas21Points() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.CLUBS, Rank.TEN), new Card(Suit.DIAMONDS, Rank.NINE),
                new Card(Suit.SPADES, Rank.FIVE), new Card(Suit.HEARTS, Rank.SEVEN), new Card(Suit.CLUBS, Rank.SIX));

        Round round = new Round(deck);

        round.start();

        round.hit();

        assertThrows(IllegalStateException.class, round::hit);
        assertEquals(3, round.getPlayerHand().getCardCount());
        assertEquals(21, round.getPlayerHand().calculateScore());
        assertEquals(RoundStatus.PLAYER_TURN, round.getStatus());

        verify(deck, times(5)).cardDraw();
    }

    @Test
    void shouldNotAllowDealerTurnWhileItIsPlayerTurn() {

        Round round = new Round(new Deck());

        round.start();

        assertEquals(RoundStatus.PLAYER_TURN, round.getStatus());
        assertThrows(IllegalStateException.class, round::playDealerTurn);
        assertEquals(RoundStatus.PLAYER_TURN, round.getStatus());
        assertNull(round.getResult());
    }

    @Test
    void shouldNotAllowDealerToPlayTwice() {
        Deck deck = mock(Deck.class);

        when(deck.cardDraw()).thenReturn(new Card(Suit.HEARTS, Rank.TEN), new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.SPADES, Rank.EIGHT), new Card(Suit.DIAMONDS, Rank.KING));

        Round round = new Round(deck);

        round.start();

        round.stand();

        round.playDealerTurn();

        assertEquals(RoundStatus.FINISHED, round.getStatus());
        assertEquals(RoundResult.DEALER_WIN, round.getResult());
        assertThrows(IllegalStateException.class, round::playDealerTurn);
        assertEquals(RoundStatus.FINISHED, round.getStatus());

        verify(deck, times(4)).cardDraw();
    }
}
