package dev.enzo.blackjack.player.application.usecase;

import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.RoundResult;
import dev.enzo.blackjack.player.application.port.out.RoundResultRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordRoundResultServiceTest {
    private RoundResultRepository repository;
    private RecordRoundResultService service;

    @BeforeEach
    void setUp() {
        repository = mock(RoundResultRepository.class);
        service = new RecordRoundResultService(repository);
    }

    @Test
    void shouldRecordNewRoundResult() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        RoundFinishedEvent event = new RoundFinishedEvent(roundId, playerId, RoundResult.PLAYER_WIN);

        when(repository.recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN)).thenReturn(true);

        service.recordResult(event);

        verify(repository).recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN);
    }

    @Test
    void shouldAcceptAlreadyRecordedRound() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        RoundFinishedEvent event = new RoundFinishedEvent(roundId, playerId, RoundResult.PLAYER_WIN);

        when(repository.recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN)).thenReturn(false);

        assertDoesNotThrow(() -> service.recordResult(event));

        verify(repository).recordIfAbsent(roundId, playerId, RoundResult.PLAYER_WIN);
    }

    @Test
    void shouldForwardDrawResult() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        RoundFinishedEvent event = new RoundFinishedEvent(roundId, playerId, RoundResult.PUSH);

        service.recordResult(event);

        verify(repository).recordIfAbsent(roundId, playerId, RoundResult.PUSH);
    }

    @Test
    void shouldRejectNullEvent() {
        assertThrows(NullPointerException.class, () -> service.recordResult(null));

        verifyNoInteractions(repository);
    }

    @Test
    void shouldPropagateRepositoryFailure() {
        UUID roundId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        RoundFinishedEvent event = new RoundFinishedEvent(roundId, playerId, RoundResult.DEALER_WIN);

        doThrow(new IllegalStateException("Database unavailable")).when(repository)
                .recordIfAbsent(roundId, playerId, RoundResult.DEALER_WIN);

        assertThrows(IllegalStateException.class, () -> service.recordResult(event));
    }
}
