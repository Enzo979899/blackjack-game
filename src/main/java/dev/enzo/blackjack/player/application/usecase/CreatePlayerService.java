package dev.enzo.blackjack.player.application.usecase;

import dev.enzo.blackjack.player.application.dto.CreatePlayerResult;
import dev.enzo.blackjack.player.application.port.in.CreatePlayerUseCase;
import dev.enzo.blackjack.player.application.port.out.PlayerRepository;
import dev.enzo.blackjack.player.domain.model.Player;

import java.util.Objects;
import java.util.UUID;

public class CreatePlayerService implements CreatePlayerUseCase {
    private final PlayerRepository playerRepository;

    public CreatePlayerService(PlayerRepository playerRepository) {
        this.playerRepository = Objects.requireNonNull(playerRepository);
    }

    @Override
    public CreatePlayerResult createPlayer(String name) {
        UUID playerId = UUID.randomUUID();

        Player player = new Player(playerId, name);

        playerRepository.save(player);

        return new CreatePlayerResult(player.getId(), player.getName());
    }

}
