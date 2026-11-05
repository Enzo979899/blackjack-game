package dev.enzo.blackjack.ranking.infrastructure.fake;

import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;
import dev.enzo.blackjack.ranking.application.dto.RankingEntry;
import dev.enzo.blackjack.ranking.application.port.out.RankingRepository;

import java.util.List;
import java.util.Objects;

public class InMemoryRankingRepository implements RankingRepository {
    private final PlayerRepository playerRepository;

    public InMemoryRankingRepository(PlayerRepository playerRepository) {
        this.playerRepository = Objects.requireNonNull(playerRepository, "PlayerRepository cannot be null");
    }

    @Override
    public List<RankingEntry> findAll() {
        return playerRepository.findAll().stream().map(this::toRankingEntry).toList();
    }

    private RankingEntry toRankingEntry(Player player) {
        return new RankingEntry(player.getId(), player.getName(), player.getWins(), player.getLosses(),
                player.getDraws());
    }
}
