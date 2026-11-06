package dev.enzo.blackjack.game.infrastructure.persistence.mongo;

import dev.enzo.blackjack.game.domain.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

@SpringBootTest(properties = {
        "spring.mongodb.uri=mongodb://localhost:27017/blackjack_test"
})
@ActiveProfiles("mongo")
class MongoRoundRepositoryIntegrationTest {

    @Autowired
    private MongoRoundRepositoryAdapter adapter;

    @Autowired
    private SpringDataRoundRepository springRepository;
    private final List<UUID> createdRoundIds = new ArrayList<>();

    @AfterEach
    void cleanUp() {
        for (UUID roundId : createdRoundIds) {
            springRepository.deleteById(roundId.toString());
        }
        createdRoundIds.clear();
    }

    @Test
    void shouldSaveAndFindRoundInMongoDB() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Round round = new Round(roundId, playerId, new Deck());

        createdRoundIds.add(roundId);
        adapter.save(round);

        Round restoredRound = adapter.findById(roundId).orElseThrow();

        assertEquals(roundId, restoredRound.getRoundId());
        assertEquals(playerId, restoredRound.getPlayerId());
        assertEquals(round.getStatus(), restoredRound.getStatus());
        assertEquals(round.getRemainingCards().size(), restoredRound.getRemainingCards().size());

        for (int i = 0; i < round.getRemainingCards().size(); i++) {
            Card originalCard = round.getRemainingCards().get(i);
            Card restoredCard = restoredRound.getRemainingCards().get(i);

            assertEquals(originalCard.getSuit(), restoredCard.getSuit());
            assertEquals(originalCard.getRank(), restoredCard.getRank());
        }
    }

    @Test
    void shouldPreservePlayerAndDealerHandsInMongoDB() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Round round = Round.restore(roundId, playerId, List.of(new Card(Suit.HEARTS, Rank.AS),
                        new Card(Suit.SPADES, Rank.KING)), List.of(new Card(Suit.CLUBS, Rank.SEVEN),
                        new Card(Suit.DIAMONDS, Rank.NINE)), new Deck(), RoundStatus.WAITING_TO_START, null);

        createdRoundIds.add(roundId);
        adapter.save(round);

        Round restoredRound = adapter.findById(roundId).orElseThrow();

        assertEquals(2, restoredRound.getPlayerHand().getCards().size());
        assertEquals(2, restoredRound.getDealerHand().getCards().size());

        List<Card> playerCards = restoredRound.getPlayerHand().getCards();
        List<Card> dealerCards = restoredRound.getDealerHand().getCards();

        assertEquals(Suit.HEARTS, playerCards.get(0).getSuit());
        assertEquals(Rank.AS, playerCards.get(0).getRank());

        assertEquals(Suit.SPADES, playerCards.get(1).getSuit());
        assertEquals(Rank.KING, playerCards.get(1).getRank());

        assertEquals(Suit.CLUBS, dealerCards.get(0).getSuit());
        assertEquals(Rank.SEVEN, dealerCards.get(0).getRank());

        assertEquals(Suit.DIAMONDS, dealerCards.get(1).getSuit());
        assertEquals(Rank.NINE, dealerCards.get(1).getRank());
    }

    @Test
    void shouldPreserveFinishedRoundResultInMongoDB() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Round round = Round.restore(roundId, playerId, List.of(new Card(Suit.HEARTS, Rank.TEN),
                new Card(Suit.SPADES, Rank.KING)), List.of(new Card(Suit.CLUBS, Rank.NINE),
                new Card(Suit.DIAMONDS, Rank.EIGHT)), new Deck(), RoundStatus.FINISHED, RoundResult.PLAYER_WIN);

        createdRoundIds.add(roundId);
        adapter.save(round);

        Round restoredRound = adapter.findById(roundId).orElseThrow();

        assertEquals(RoundStatus.FINISHED, restoredRound.getStatus());
        assertEquals(RoundResult.PLAYER_WIN, restoredRound.getResult());
    }

    @Test
    void shouldReturnEmptyWhenRoundDoesNotExistInMongoDB() {
        UUID nonExistingRoundId = UUID.randomUUID();

        Optional<Round> result = adapter.findById(nonExistingRoundId);

        assertTrue(result.isEmpty());
    }
}
