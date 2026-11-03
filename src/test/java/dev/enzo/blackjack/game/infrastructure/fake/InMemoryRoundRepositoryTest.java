package dev.enzo.blackjack.game.infrastructure.fake;

import dev.enzo.blackjack.game.domain.model.Deck;
import dev.enzo.blackjack.game.domain.model.Round;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRoundRepositoryTest {

    @Test
    void shouldSaveAndFindRoundById() {
        InMemoryRoundRepository repository = new InMemoryRoundRepository();
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Round round = new Round(roundId, playerId, new Deck());
        repository.save(round);
        Optional<Round> result = repository.findById(roundId);

        assertTrue(result.isPresent());
        assertSame(round, result.get());
    }

    @Test
    void shouldReturnEmptyWhenRoundDoesNotExist() {
        InMemoryRoundRepository repository = new InMemoryRoundRepository();
        UUID roundId = UUID.randomUUID();

        Optional<Round> result = repository.findById(roundId);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldStoreMultipleRoundsIndependently() {
        InMemoryRoundRepository repository = new InMemoryRoundRepository();
        UUID playerId = UUID.randomUUID();
        UUID firstRoundId = UUID.randomUUID();
        UUID secondRoundId = UUID.randomUUID();
        Round firstRound = new Round(firstRoundId, playerId, new Deck());
        Round secondRound = new Round(secondRoundId, playerId, new Deck());

        repository.save(firstRound);
        repository.save(secondRound);

        assertSame(firstRound, repository.findById(firstRoundId).orElseThrow());
        assertSame(secondRound, repository.findById(secondRoundId).orElseThrow());
    }

    @Test
    void shouldReplaceRoundWhenSavingSameId() {
        InMemoryRoundRepository repository = new InMemoryRoundRepository();
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();
        Round originalRound = new Round(roundId, playerId, new Deck());
        Round updatedRound = new Round(roundId, playerId, new Deck());

        repository.save(originalRound);
        repository.save(updatedRound);

        assertSame(updatedRound, repository.findById(roundId).orElseThrow());
    }

}
