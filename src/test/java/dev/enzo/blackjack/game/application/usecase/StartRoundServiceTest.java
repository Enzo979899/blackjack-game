
package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.out.DeckFactory;
import dev.enzo.blackjack.game.application.port.out.RoundEventPublisher;
import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.*;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartRoundServiceTest {

    private PlayerRepository playerRepository;
    private RoundRepository roundRepository;
    private RoundEventPublisher eventPublisher;
    private DeckFactory deckFactory;
    private StartRoundService service;

    @BeforeEach
    void setUp() {
        playerRepository = mock(PlayerRepository.class);
        roundRepository = mock(RoundRepository.class);
        eventPublisher = mock(RoundEventPublisher.class);
        deckFactory = mock(DeckFactory.class);

        service = new StartRoundService(playerRepository, roundRepository, eventPublisher, deckFactory);
    }

    private Deck deckWithCards(Card... cards) {
        Deck deck = mock(Deck.class);
        when(deck.cardDraw()).thenReturn(cards[0], Arrays.copyOfRange(cards, 1, cards.length));

        return deck;
    }

    private void givenExistingPlayer(UUID playerId) {
        Player player = new Player(playerId, "Enzo");
        when(playerRepository.findById(playerId)).thenReturn(Optional.of(player));
    }

    private void giveDeckWithoutBlackjack() {
        when(deckFactory.createDeck()).thenAnswer(invocation ->
                deckWithCards(new Card(Suit.HEARTS, Rank.TEN), new Card(Suit.HEARTS, Rank.NINE),
                        new Card(Suit.DIAMONDS, Rank.SEVEN), new Card(Suit.CLUBS, Rank.EIGHT)));
    }

    @Test
    void shouldStartAndSaveRoundForExistingPlayer() {
        UUID playerId = UUID.randomUUID();
        givenExistingPlayer(playerId);
        giveDeckWithoutBlackjack();

        UUID roundId = service.startRound(playerId);

        assertNotNull(roundId);

        ArgumentCaptor<Round> captor = ArgumentCaptor.forClass(Round.class);

        verify(roundRepository).save(captor.capture());

        Round savedRound = captor.getValue();

        assertEquals(roundId, savedRound.getRoundId());
        assertEquals(playerId, savedRound.getPlayerId());
        assertEquals(RoundStatus.PLAYER_TURN, savedRound.getStatus());
        verify(playerRepository).findById(playerId);
        verify(deckFactory).createDeck();
    }

    @Test
    void shouldRejectNonExistingPlayer() {
        UUID playerId = UUID.randomUUID();

        when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.startRound(playerId));

        verify(roundRepository, never()).save(any(Round.class));
        verifyNoInteractions(deckFactory, eventPublisher);
    }

    @Test
    void shouldRejectNullPlayerId() {
        assertThrows(NullPointerException.class, () -> service.startRound(null));

        verifyNoInteractions(playerRepository, roundRepository, eventPublisher, deckFactory);
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentRounds() {

        UUID playerId = UUID.randomUUID();

        givenExistingPlayer(playerId);
        giveDeckWithoutBlackjack();

        UUID firstRoundId = service.startRound(playerId);
        UUID secondRoundId = service.startRound(playerId);

        assertNotEquals(firstRoundId, secondRoundId);
        verify(roundRepository, times(2)).save(any(Round.class));
        verify(deckFactory, times(2)).createDeck();
    }

    @Test
    void shouldRejectNullDependencies() {
        assertThrows(NullPointerException.class, () -> new StartRoundService(null, roundRepository,
                        eventPublisher, deckFactory));

        assertThrows(NullPointerException.class, () -> new StartRoundService(playerRepository, null,
                        eventPublisher, deckFactory));

        assertThrows(NullPointerException.class, () ->
                new StartRoundService(playerRepository, roundRepository, null, deckFactory));

        assertThrows(NullPointerException.class, () -> new StartRoundService(playerRepository, roundRepository,
                eventPublisher, null));
    }

    @Test
    void shouldPublishEventWhenNaturalBlackjackFinishesRound() {
        UUID playerId = UUID.randomUUID();

        givenExistingPlayer(playerId);

        Deck deck = deckWithCards(new Card(Suit.HEARTS, Rank.AS), new Card(Suit.SPADES, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.KING), new Card(Suit.HEARTS, Rank.SEVEN));

        when(deckFactory.createDeck()).thenReturn(deck);

        UUID roundId = service.startRound(playerId);
        ArgumentCaptor<RoundFinishedEvent> captor = ArgumentCaptor.forClass(RoundFinishedEvent.class);

        var inOrder = inOrder(roundRepository, eventPublisher);

        inOrder.verify(roundRepository).save(any(Round.class));
        inOrder.verify(eventPublisher).publish(captor.capture());
        RoundFinishedEvent event = captor.getValue();

        assertEquals(roundId, event.roundId());
        assertEquals(playerId, event.playerId());
        assertEquals(RoundResult.PLAYER_WIN, event.result());
    }

    @Test
    void shouldNotPublishEventWhenRoundIsStillActive() {
        UUID playerId = UUID.randomUUID();

        givenExistingPlayer(playerId);
        giveDeckWithoutBlackjack();

        service.startRound(playerId);

        verify(roundRepository).save(any(Round.class));
        verifyNoInteractions(eventPublisher);
    }
}

