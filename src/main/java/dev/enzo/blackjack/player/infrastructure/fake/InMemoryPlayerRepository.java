package dev.enzo.blackjack.player.infrastructure.fake;

import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPlayerRepository implements PlayerRepository {

    private final Map<UUID, Player> players = new ConcurrentHashMap<>();

    @Override
    public void save(Player player) {
        players.put(player.getId(), player);
    }

    @Override
    public Optional<Player> findById(UUID playerId) {
        return Optional.ofNullable(players.get(playerId));
    }

}
