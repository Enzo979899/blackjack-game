
package dev.enzo.blackjack.ranking.application.usecase;

import dev.enzo.blackjack.ranking.application.dto.RankingEntry;
import dev.enzo.blackjack.ranking.application.port.out.RankingRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetRankingServiceTest {
    private RankingRepository rankingRepository;
    private GetRankingService service;

    @BeforeEach
    void setUp() {
        rankingRepository = mock(RankingRepository.class);
        service = new GetRankingService(rankingRepository);
    }

    private RankingEntry entry(String name, int wins, int losses, int draws) {
        return new RankingEntry(UUID.randomUUID(), name, wins, losses, draws);
    }

    @Test
    void shouldReturnEmptyRankingWhenNoPlayersExist() {
        when(rankingRepository.findAll()).thenReturn(List.of());

        List<RankingEntry> ranking = service.getRanking();

        assertTrue(ranking.isEmpty());
        verify(rankingRepository).findAll();
    }

    @Test
    void shouldSortPlayersByWinsDescending() {
        RankingEntry enzo = entry("Enzo", 5, 2, 1);
        RankingEntry carla = entry("Carla", 15, 3, 0);
        RankingEntry denise = entry("Denise", 10, 1, 2);

        when(rankingRepository.findAll()).thenReturn(List.of(enzo, carla, denise));

        List<RankingEntry> ranking = service.getRanking();

        assertEquals(List.of(carla, denise, enzo), ranking);
    }

    @Test
    void shouldSortPlayersByLossesAscendingWhenWinsAreEqual() {
        RankingEntry enzo = entry("Enzo", 10, 5, 0);
        RankingEntry carla = entry("Carla", 10, 2, 1);
        RankingEntry denise = entry("Denise", 10, 8, 0);

        when(rankingRepository.findAll()).thenReturn(List.of(enzo, denise, carla));

        List<RankingEntry> ranking = service.getRanking();

        assertEquals(List.of(carla, enzo, denise), ranking);
    }

    @Test
    void shouldSortAlphabeticallyWhenWinsAndLossesAreEqual() {
        RankingEntry enzo = entry("Enzo", 10, 3, 2);
        RankingEntry carla = entry("Carla", 10, 3, 0);
        RankingEntry denise = entry("Denise", 10, 3, 1);

        when(rankingRepository.findAll()).thenReturn(List.of(enzo, denise, carla));

        List<RankingEntry> ranking = service.getRanking();

        assertEquals(List.of(carla, denise, enzo), ranking);
    }

    @Test
    void shouldSortNamesIgnoringCase() {
        RankingEntry enzo = entry("Enzo", 5, 2, 0);
        RankingEntry carla = entry("Carla", 5, 2, 0);
        RankingEntry denise = entry("Denise", 5, 2, 0);

        when(rankingRepository.findAll()).thenReturn(List.of(enzo, denise, carla));

        List<RankingEntry> ranking = service.getRanking();

        assertEquals(List.of(carla, denise, enzo), ranking);
    }

    @Test
    void shouldNotModifyOriginalRepositoryList() {
        RankingEntry enzo = entry("Enzo", 5, 2, 0);
        RankingEntry carla = entry("Carla", 10, 1, 0);

        List<RankingEntry> original = new ArrayList<>(List.of(enzo, carla));

        when(rankingRepository.findAll()).thenReturn(original);

        List<RankingEntry> ranking = service.getRanking();

        assertEquals(List.of(enzo, carla), original);
        assertEquals(List.of(carla, enzo), ranking);
    }

    @Test
    void shouldRejectNullRepository() {
        assertThrows(NullPointerException.class, () -> new GetRankingService(null));
    }

    @Test
    void shouldCallRepositoryOnlyOnce() {
        when(rankingRepository.findAll()).thenReturn(List.of());

        service.getRanking();

        verify(rankingRepository, times(1)).findAll();
        verifyNoMoreInteractions(rankingRepository);
    }
}
