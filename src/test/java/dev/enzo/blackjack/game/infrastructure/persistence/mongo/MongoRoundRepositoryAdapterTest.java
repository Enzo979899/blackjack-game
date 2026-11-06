package dev.enzo.blackjack.game.infrastructure.persistence.mongo;

import dev.enzo.blackjack.game.domain.model.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertTrue;

class MongoRoundRepositoryAdapterTest {
    private final SpringDataRoundRepository springRepository = mock(SpringDataRoundRepository.class);
    private final MongoRoundRepositoryAdapter adapter = new MongoRoundRepositoryAdapter(springRepository);

    @Test
    void shouldSaveRoundAsMongoDocument() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        Round round = new Round(roundId, playerId, new Deck());

        adapter.save(round);

        ArgumentCaptor<RoundDocument> captor = ArgumentCaptor.forClass(RoundDocument.class);

        verify(springRepository).save(captor.capture());

        RoundDocument savedDocument = captor.getValue();

        assertEquals(roundId.toString(), savedDocument.getId());
        assertEquals(playerId.toString(), savedDocument.getPlayerId());
    }

    @Test
    void shouldFindRoundById() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        RoundDocument document = new RoundDocument(roundId.toString(), playerId.toString(), List.of(), List.of(),
                List.of(new CardDocument(Suit.HEARTS, Rank.AS), new CardDocument(Suit.SPADES, Rank.KING)),
                RoundStatus.WAITING_TO_START, null);

        when(springRepository.findById(roundId.toString())).thenReturn(Optional.of(document));

        Optional<Round> result = adapter.findById(roundId);

        assertTrue(result.isPresent());

        Round restoredRound = result.orElseThrow();

        assertEquals(roundId, restoredRound.getRoundId());
        assertEquals(playerId, restoredRound.getPlayerId());
        assertEquals(RoundStatus.WAITING_TO_START, restoredRound.getStatus());

        verify(springRepository).findById(roundId.toString());
    }

    @Test
    void shouldReturnEmptyWhenRoundDoesNotExist() {
        UUID roundId = UUID.randomUUID();

        when(springRepository.findById(roundId.toString())).thenReturn(Optional.empty());

        Optional<Round> result = adapter.findById(roundId);

        assertTrue(result.isEmpty());

        verify(springRepository).findById(roundId.toString());
    }
}
