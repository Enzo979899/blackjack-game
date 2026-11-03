package dev.enzo.blackjack.player.application.usecase;

import dev.enzo.blackjack.player.application.dto.CreatePlayerResult;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CreatePlayerServiceTest {

    @Test
    void shouldCreateAndSavePlayer() {
        PlayerRepository repository = mock(PlayerRepository.class);
        CreatePlayerService service = new CreatePlayerService(repository);

        CreatePlayerResult result = service.createPlayer("Enzo");

        assertNotNull(result.playerId());
        assertEquals("Enzo", result.name());

        ArgumentCaptor<Player> playerCaptor = ArgumentCaptor.forClass(Player.class);

        verify(repository).save(playerCaptor.capture());

        Player savedPlayer = playerCaptor.getValue();

        assertEquals(result.playerId(), savedPlayer.getId());
        assertEquals(result.name(), savedPlayer.getName());
    }

    @Test
    void shouldNotSavePlayerWhenNameIsNull() {
        PlayerRepository repository = mock(PlayerRepository.class);
        CreatePlayerService service = new CreatePlayerService(repository);

        assertThrows(NullPointerException.class, () -> service.createPlayer(null));
        verify(repository, never()).save(any(Player.class));
    }

    @Test
    void shouldNotSavePlayerWhenNameIsBlank() {
        PlayerRepository repository = mock(PlayerRepository.class);
        CreatePlayerService service = new CreatePlayerService(repository);

        assertThrows(IllegalArgumentException.class, () -> service.createPlayer(""));
        assertThrows(IllegalArgumentException.class, () -> service.createPlayer("   "));
        verify(repository, never()).save(any(Player.class));
    }


    @Test
    void shouldGenerateDifferentIdsForDifferentPlayers() {
        PlayerRepository repository = mock(PlayerRepository.class);
        CreatePlayerService service = new CreatePlayerService(repository);

        CreatePlayerResult firstPlayer = service.createPlayer("Enzo");
        CreatePlayerResult secondPlayer = service.createPlayer("Carla");

        assertNotEquals(firstPlayer.playerId(), secondPlayer.playerId());
        assertEquals("Enzo", firstPlayer.name());
        assertEquals("Carla", secondPlayer.name());
        verify(repository, times(2)).save(any(Player.class));
    }


}
