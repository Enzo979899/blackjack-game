
package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Round;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HitRoundServiceTest {

    @Test
    void shouldHitAndSaveExistingRound() {
        RoundRepository repository = mock(RoundRepository.class);
        Round round = mock(Round.class);
        UUID roundId = UUID.randomUUID();

        when(repository.findById(roundId)).thenReturn(Optional.of(round));

        HitRoundService service = new HitRoundService(repository);

        service.hit(roundId);

        var inOrder = inOrder(repository, round);

        inOrder.verify(repository).findById(roundId);
        inOrder.verify(round).hit();
        inOrder.verify(repository).save(round);
    }

    @Test
    void shouldRejectNonExistingRound() {
        RoundRepository repository = mock(RoundRepository.class);
        UUID roundId = UUID.randomUUID();

        when(repository.findById(roundId)).thenReturn(Optional.empty());

        HitRoundService service = new HitRoundService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.hit(roundId));
        verify(repository).findById(roundId);
        verify(repository, never()).save(any(Round.class));
    }

    @Test
    void shouldRejectNullRoundId() {
        RoundRepository repository = mock(RoundRepository.class);
        HitRoundService service = new HitRoundService(repository);

        assertThrows(NullPointerException.class, () -> service.hit(null));
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotSaveWhenDomainRejectsHit() {
        RoundRepository repository = mock(RoundRepository.class);
        Round round = mock(Round.class);
        UUID roundId = UUID.randomUUID();

        when(repository.findById(roundId)).thenReturn(Optional.of(round));
        doThrow(new IllegalStateException("Round already finished")).when(round).hit();

        HitRoundService service = new HitRoundService(repository);

        assertThrows(IllegalStateException.class, () -> service.hit(roundId));
        verify(round).hit();
        verify(repository, never()).save(any(Round.class));
    }

    @Test
    void shouldRejectNullRepository() {
        assertThrows(NullPointerException.class, () -> new HitRoundService(null));
    }

}

