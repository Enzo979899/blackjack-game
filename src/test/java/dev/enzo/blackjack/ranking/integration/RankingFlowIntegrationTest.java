package dev.enzo.blackjack.ranking.integration;

import dev.enzo.blackjack.game.domain.event.RoundFinishedEvent;
import dev.enzo.blackjack.game.domain.model.RoundResult;
import dev.enzo.blackjack.player.application.usecase.CreatePlayerService;
import dev.enzo.blackjack.player.application.usecase.RecordRoundResultService;
import dev.enzo.blackjack.player.infrastructure.fake.InMemoryPlayerRepository;
import dev.enzo.blackjack.player.infrastructure.fake.InMemoryRoundResultRepository;
import dev.enzo.blackjack.ranking.application.dto.RankingEntry;
import dev.enzo.blackjack.ranking.application.usecase.GetRankingService;
import dev.enzo.blackjack.ranking.infrastructure.fake.InMemoryRankingRepository;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RankingFlowIntegrationTest {

    @Test
    void shouldUpdateRankingAfterRecordingRoundResults() {
        InMemoryPlayerRepository playerRepository = new InMemoryPlayerRepository();
        InMemoryRoundResultRepository resultRepository = new InMemoryRoundResultRepository(playerRepository);
        InMemoryRankingRepository rankingRepository = new InMemoryRankingRepository(playerRepository);
        CreatePlayerService createPlayerService = new CreatePlayerService(playerRepository);
        RecordRoundResultService recordResultService = new RecordRoundResultService(resultRepository);
        GetRankingService getRankingService = new GetRankingService(rankingRepository);

        UUID enzoId = createPlayerService.createPlayer("Enzo").playerId();
        UUID carlaId = createPlayerService.createPlayer("Carla").playerId();
        UUID deniseId = createPlayerService.createPlayer("Denise").playerId();

        recordResultService.recordResult(new RoundFinishedEvent(UUID.randomUUID(), enzoId, RoundResult.PLAYER_WIN));

        recordResultService.recordResult(new RoundFinishedEvent(UUID.randomUUID(), enzoId, RoundResult.PLAYER_WIN));

        recordResultService.recordResult(new RoundFinishedEvent(UUID.randomUUID(), carlaId, RoundResult.PLAYER_WIN));

        recordResultService.recordResult(new RoundFinishedEvent(UUID.randomUUID(), deniseId, RoundResult.PLAYER_WIN));

        recordResultService.recordResult(new RoundFinishedEvent(UUID.randomUUID(), deniseId, RoundResult.DEALER_WIN));

        List<RankingEntry> ranking = getRankingService.getRanking();

        assertEquals(3, ranking.size());

        assertEquals(enzoId, ranking.get(0).playerId());
        assertEquals(carlaId, ranking.get(1).playerId());
        assertEquals(deniseId, ranking.get(2).playerId());

        assertAll(() -> assertEquals(2, ranking.get(0).wins()),
                () -> assertEquals(0, ranking.get(0).losses()),
                () -> assertEquals(1, ranking.get(1).wins()),
                () -> assertEquals(0, ranking.get(1).losses()),
                () -> assertEquals(1, ranking.get(2).wins()),
                () -> assertEquals(1, ranking.get(2).losses()));
    }

    @Test
    void shouldNotChangeRankingWhenRoundResultIsProcessedTwice() {
        InMemoryPlayerRepository playerRepository = new InMemoryPlayerRepository();
        InMemoryRoundResultRepository resultRepository = new InMemoryRoundResultRepository(playerRepository);
        InMemoryRankingRepository rankingRepository = new InMemoryRankingRepository(playerRepository);
        CreatePlayerService createPlayerService = new CreatePlayerService(playerRepository);
        RecordRoundResultService recordResultService = new RecordRoundResultService(resultRepository);
        GetRankingService getRankingService = new GetRankingService(rankingRepository);

        UUID enzoId = createPlayerService.createPlayer("Enzo").playerId();
        UUID carlaId = createPlayerService.createPlayer("Carla").playerId();

        recordResultService.recordResult(new RoundFinishedEvent(UUID.randomUUID(), carlaId, RoundResult.PLAYER_WIN));

        UUID roundId = UUID.randomUUID();

        RoundFinishedEvent event = new RoundFinishedEvent(roundId, enzoId, RoundResult.PLAYER_WIN);

        recordResultService.recordResult(event);

        List<RankingEntry> rankingBefore = getRankingService.getRanking();

        recordResultService.recordResult(event);

        List<RankingEntry> rankingAfter = getRankingService.getRanking();

        assertEquals(rankingBefore, rankingAfter);
        assertEquals(1, rankingAfter.stream().filter(entry -> entry.playerId().equals(enzoId))
                .findFirst().orElseThrow().wins());

        assertEquals(1, rankingAfter.stream().filter(entry -> entry.playerId().equals(carlaId))
                .findFirst().orElseThrow().wins());
    }

}
