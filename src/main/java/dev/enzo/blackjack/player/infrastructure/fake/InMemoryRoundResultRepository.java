package dev.enzo.blackjack.player.infrastructure.fake;

import dev.enzo.blackjack.game.domain.model.RoundResult;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.application.port.out.RoundResultRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class InMemoryRoundResultRepository implements RoundResultRepository {
    private final PlayerRepository playerRepository;
    private final Set<UUID> processedRoundIds = new HashSet<>();

    public InMemoryRoundResultRepository(PlayerRepository playerRepository) {
        this.playerRepository = Objects.requireNonNull(playerRepository);
    }

    @Override
    public synchronized boolean recordIfAbsent(UUID roundId, UUID playerId, RoundResult result) {
        Objects.requireNonNull(roundId, "Round ID cannot be null");
        Objects.requireNonNull(playerId, "Player ID cannot be null");
        Objects.requireNonNull(result, "Round result cannot be null");

        if (processedRoundIds.contains(roundId)) {
            return false;
        }

        Player player = playerRepository.findById(playerId).orElseThrow(() ->
                new IllegalArgumentException("Player not found: " + playerId));

        switch (result) {
            case PLAYER_WIN -> player.recordWin();
            case DEALER_WIN -> player.recordLoss();
            case PUSH -> player.recordDraw();
        }

        playerRepository.save(player);
        processedRoundIds.add(roundId);

        return true;
    }

}


