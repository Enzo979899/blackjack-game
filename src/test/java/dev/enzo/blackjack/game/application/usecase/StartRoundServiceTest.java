package dev.enzo.blackjack.game.application.usecase;

import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Round;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StartRoundServiceTest {

    @Test
    void shouldStartAndSaveRoundForExistingPlayer() {
        PlayerRepository playerRepository = mock(PlayerRepository.class);
        RoundRepository roundRepository = mock(RoundRepository.class);
        StartRoundService service = new StartRoundService(playerRepository, roundRepository);
        UUID playerId = UUID.randomUUID();
        Player player = new Player(playerId, "Enzo");

        when(playerRepository.findById(playerId)).thenReturn(java.util.Optional.of(player));

        UUID roundId = service.startRound(playerId);

        assertNotNull(roundId);

        ArgumentCaptor<Round> roundCaptor = ArgumentCaptor.forClass(Round.class);

        verify(roundRepository).save(roundCaptor.capture());

        Round savedRound = roundCaptor.getValue();

        assertEquals(roundId, savedRound.getRoundId());
        assertEquals(playerId, savedRound.getPlayerId());
        verify(playerRepository).findById(playerId);
    }

    @Test
    void shouldRejectNonExistingPlayer() {
        PlayerRepository playerRepository = mock(PlayerRepository.class);
        RoundRepository roundRepository = mock(RoundRepository.class);
        StartRoundService service = new StartRoundService(playerRepository, roundRepository);
        UUID playerId = UUID.randomUUID();

        when(playerRepository.findById(playerId)).thenReturn(java.util.Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.startRound(playerId));
        verify(roundRepository, never()).save(any(Round.class));
    }

    @Test
    void shouldRejectNullPlayerId() {
        PlayerRepository playerRepository = mock(PlayerRepository.class);
        RoundRepository roundRepository = mock(RoundRepository.class);

        StartRoundService service = new StartRoundService(playerRepository, roundRepository);

        assertThrows(NullPointerException.class, () -> service.startRound(null));
        verifyNoInteractions(playerRepository, roundRepository);
    }

    @Test
    void shouldGenerateDifferentIdsForDifferentRounds() {
        PlayerRepository playerRepository = mock(PlayerRepository.class);
        RoundRepository roundRepository = mock(RoundRepository.class);
        StartRoundService service = new StartRoundService(playerRepository, roundRepository);
        UUID playerId = UUID.randomUUID();
        Player player = new Player(playerId, "Enzo");

        when(playerRepository.findById(playerId)).thenReturn(java.util.Optional.of(player));

        UUID firstRoundId = service.startRound(playerId);
        UUID secondRoundId = service.startRound(playerId);

        assertNotEquals(firstRoundId, secondRoundId);
        verify(roundRepository, times(2)).save(any(Round.class));
    }

    @Test
    void shouldRejectNullRepositories() {
        PlayerRepository playerRepository = mock(PlayerRepository.class);
        RoundRepository roundRepository = mock(RoundRepository.class);

        assertThrows(NullPointerException.class, () -> new StartRoundService(null, roundRepository));
        assertThrows(NullPointerException.class, () -> new StartRoundService(playerRepository, null));
    }

}

