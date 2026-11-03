package dev.enzo.blackjack.player.application.port.in;

import dev.enzo.blackjack.player.application.dto.CreatePlayerResult;

public interface CreatePlayerUseCase {

    CreatePlayerResult createPlayer(String name);

}
