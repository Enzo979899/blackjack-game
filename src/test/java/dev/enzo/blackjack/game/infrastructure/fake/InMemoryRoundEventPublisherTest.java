package dev.enzo.blackjack.game.infrastructure.fake;

import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.RoundResult;
import dev.enzo.blackjack.player.application.port.in.RecordRoundResultUseCase;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InMemoryRoundEventPublisherTest {

    @Test
    void shouldForwardRoundFinishedEvent() {
        RecordRoundResultUseCase useCase = mock(RecordRoundResultUseCase.class);
        InMemoryRoundEventPublisher publisher = new InMemoryRoundEventPublisher(useCase);
        RoundFinishedEvent event = new RoundFinishedEvent(UUID.randomUUID(), UUID.randomUUID(), RoundResult.PLAYER_WIN);

        publisher.publish(event);

        verify(useCase, times(1)).recordResult(event);
    }

    @Test
    void shouldForwardDifferentEvents() {
        RecordRoundResultUseCase useCase = mock(RecordRoundResultUseCase.class);
        InMemoryRoundEventPublisher publisher = new InMemoryRoundEventPublisher(useCase);
        UUID playerId = UUID.randomUUID();
        RoundFinishedEvent firstEvent = new RoundFinishedEvent(UUID.randomUUID(), playerId, RoundResult.PLAYER_WIN);
        RoundFinishedEvent secondEvent = new RoundFinishedEvent(UUID.randomUUID(), playerId, RoundResult.DEALER_WIN);

        publisher.publish(firstEvent);
        publisher.publish(secondEvent);

        var inOrder = inOrder(useCase);

        inOrder.verify(useCase).recordResult(firstEvent);
        inOrder.verify(useCase).recordResult(secondEvent);
    }

    @Test
    void shouldRejectNullUseCase() {
        assertThrows(NullPointerException.class, () -> new InMemoryRoundEventPublisher(null));

    }

    @Test
    void shouldPropagateUseCaseFailure() {
        RecordRoundResultUseCase useCase = mock(RecordRoundResultUseCase.class);
        InMemoryRoundEventPublisher publisher = new InMemoryRoundEventPublisher(useCase);
        RoundFinishedEvent event = new RoundFinishedEvent(UUID.randomUUID(), UUID.randomUUID(), RoundResult.PUSH);

        doThrow(new IllegalStateException("Result processing failed")).when(useCase).recordResult(event);

        assertThrows(IllegalStateException.class, () -> publisher.publish(event));

    }

}
