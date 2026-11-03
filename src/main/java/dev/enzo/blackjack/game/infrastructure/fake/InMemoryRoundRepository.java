package dev.enzo.blackjack.game.infrastructure.fake;

import dev.enzo.blackjack.game.application.port.out.RoundRepository;
import dev.enzo.blackjack.game.domain.model.Round;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryRoundRepository implements RoundRepository {

    private final Map<UUID, Round> rounds = new ConcurrentHashMap<>();

    @Override
    public void save(Round round) {
        rounds.put(round.getRoundId(), round);
    }

    @Override
    public Optional<Round> findById(UUID roundId) {
        return Optional.ofNullable(rounds.get(roundId));
    }
}
