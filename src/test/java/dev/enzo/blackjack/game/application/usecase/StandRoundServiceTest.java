
package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Round;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StandRoundServiceTest {

    @Test
    void shouldStandAndSaveExistingRound() {
        RoundRepository repository = mock(RoundRepository.class);
        Round round = mock(Round.class);
        UUID roundId = UUID.randomUUID();

        when(repository.findById(roundId)).thenReturn(Optional.of(round));

        StandRoundService service = new StandRoundService(repository);

        service.stand(roundId);

        var inOrder = inOrder(repository, round);

        inOrder.verify(repository).findById(roundId);
        inOrder.verify(round).stand();
        inOrder.verify(repository).save(round);
    }

    @Test
    void shouldRejectNonExistingRound() {
        RoundRepository repository = mock(RoundRepository.class);
        UUID roundId = UUID.randomUUID();

        when(repository.findById(roundId)).thenReturn(Optional.empty());

        StandRoundService service = new StandRoundService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.stand(roundId));
        verify(repository).findById(roundId);
        verify(repository, never()).save(any(Round.class));
    }

    @Test
    void shouldRejectNullRoundId() {
        RoundRepository repository = mock(RoundRepository.class);
        StandRoundService service = new StandRoundService(repository);

        assertThrows(NullPointerException.class, () -> service.stand(null));
        verifyNoInteractions(repository);
    }

    @Test
    void shouldNotSaveWhenDomainRejectsStand() {
        RoundRepository repository = mock(RoundRepository.class);
        Round round = mock(Round.class);

        UUID roundId = UUID.randomUUID();

        when(repository.findById(roundId)).thenReturn(Optional.of(round));

        doThrow(new IllegalStateException("Round already finished")).when(round).stand();

        StandRoundService service = new StandRoundService(repository);

        assertThrows(IllegalStateException.class, () -> service.stand(roundId));
        verify(round).stand();
        verify(repository, never()).save(any(Round.class));
    }

    @Test
    void shouldRejectNullRepository() {
        assertThrows(NullPointerException.class, () -> new StandRoundService(null));
    }

}

