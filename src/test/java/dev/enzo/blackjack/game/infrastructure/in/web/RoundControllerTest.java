package dev.enzo.blackjack.game.infrastructure.in.web;

import dev.enzo.blackjack.game.application.port.in.StartRoundUseCase;
import dev.enzo.blackjack.player.application.exception.PlayerNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.*;

class RoundControllerTest {

    @Test
    void shouldStartRoundAndReturn201() {
        UUID playerId = UUID.randomUUID();
        UUID roundId = UUID.randomUUID();

        StartRoundUseCase useCase = mock(StartRoundUseCase.class);

        when(useCase.startRound(playerId)).thenReturn(Mono.just(roundId));

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase)).build();

        client.post().uri("/api/rounds").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                          "playerId": "%s"
                        }
                        """.formatted(playerId))
                .exchange().expectStatus().isCreated().expectBody(String.class)
                .isEqualTo("\"" + roundId + "\"");

        verify(useCase).startRound(playerId);
    }

    @Test
    void shouldReturn400WhenPlayerIdIsNull() {
        StartRoundUseCase useCase = mock(StartRoundUseCase.class);

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase))
                .controllerAdvice(new RoundExceptionHandler()).build();

        client.post().uri("/api/rounds").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "playerId": null
                    }
                    """)
                .exchange().expectStatus().isBadRequest().expectBody().jsonPath("$.error")
                .isEqualTo("Player ID is required");

        verifyNoInteractions(useCase);
    }

    @Test
    void shouldReturn400WhenPlayerIdIsInvalid() {
        StartRoundUseCase useCase = mock(StartRoundUseCase.class);

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase))
                .controllerAdvice(new RoundExceptionHandler()).build();

        client.post().uri("/api/rounds").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "playerId": "abc123"
                    }
                    """)
                .exchange().expectStatus().isBadRequest();

        verifyNoInteractions(useCase);
    }

    @Test
    void shouldReturn404WhenPlayerDoesNotExist() {
        UUID playerId = UUID.randomUUID();

        StartRoundUseCase useCase = mock(StartRoundUseCase.class);

        when(useCase.startRound(playerId)).thenReturn(Mono.error(new PlayerNotFoundException(playerId)));

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase))
                .controllerAdvice(new RoundExceptionHandler()).build();

        client.post().uri("/api/rounds").contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                    {
                      "playerId": "%s"
                    }
                    """.formatted(playerId))
                .exchange().expectStatus().isNotFound().expectBody().jsonPath("$.error")
                .isEqualTo("Player not found: " + playerId);

        verify(useCase).startRound(playerId);
    }

}
