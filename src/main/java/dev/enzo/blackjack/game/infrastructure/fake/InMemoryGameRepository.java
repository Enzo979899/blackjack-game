package dev.enzo.blackjack.game.infrastructure.fake;

import dev.enzo.blackjack.game.application.port.out.GameRepository;
import dev.enzo.blackjack.game.domain.model.Game;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryGameRepository implements GameRepository {

    private final Map<UUID, Game> games = new ConcurrentHashMap<>();

    @Override
    public void save(UUID gameId, Game game) {
        games.put(gameId, game);
    }

    @Override
    public Optional<Game> findById(UUID gameId) {
        return Optional.ofNullable(games.get(gameId));
    }
}
