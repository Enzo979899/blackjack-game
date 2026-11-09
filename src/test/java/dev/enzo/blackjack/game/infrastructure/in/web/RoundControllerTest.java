package dev.enzo.blackjack.game.infrastructure.in.web;

import dev.enzo.blackjack.game.application.exception.RoundNotFoundException;
import dev.enzo.blackjack.game.application.port.in.HitRoundUseCase;
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
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        when(useCase.startRound(playerId)).thenReturn(Mono.just(roundId));

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase, hitUseCase)).build();

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
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase, hitUseCase))
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
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase, hitUseCase))
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
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        when(useCase.startRound(playerId)).thenReturn(Mono.error(new PlayerNotFoundException(playerId)));

        WebTestClient client = WebTestClient.bindToController(new RoundController(useCase, hitUseCase))
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

    @Test
    void shouldHitRoundAndReturn204() {
        UUID roundId = UUID.randomUUID();

        StartRoundUseCase startUseCase = mock(StartRoundUseCase.class);
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        when(hitUseCase.hit(roundId)).thenReturn(Mono.empty());

        WebTestClient client = WebTestClient.bindToController(new RoundController(startUseCase, hitUseCase)).build();

        client.post().uri("/api/rounds/{roundId}/hit", roundId).exchange().expectStatus().isNoContent()
                .expectBody().isEmpty();

        verify(hitUseCase).hit(roundId);
        verifyNoInteractions(startUseCase);
    }

    @Test
    void shouldReturn404WhenHittingNonExistingRound() {
        UUID roundId = UUID.randomUUID();
        StartRoundUseCase startUseCase = mock(StartRoundUseCase.class);
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        when(hitUseCase.hit(roundId)).thenReturn(Mono.error(new RoundNotFoundException(roundId)));

        WebTestClient client = WebTestClient.bindToController(new RoundController(startUseCase, hitUseCase))
                .controllerAdvice(new RoundExceptionHandler()).build();

        client.post().uri("/api/rounds/{roundId}/hit", roundId).exchange().expectStatus().isNotFound().expectBody()
                .jsonPath("$.error").isEqualTo("Round not found: " + roundId);

        verify(hitUseCase).hit(roundId);
        verifyNoInteractions(startUseCase);
    }

    @Test
    void shouldReturn409WhenHittingFinishedRound() {
        UUID roundId = UUID.randomUUID();

        StartRoundUseCase startUseCase = mock(StartRoundUseCase.class);
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        when(hitUseCase.hit(roundId)).thenReturn(Mono.error(new IllegalStateException("It's not player turn!")));

        WebTestClient client = WebTestClient.bindToController(new RoundController(startUseCase, hitUseCase))
                .controllerAdvice(new RoundExceptionHandler()).build();

        client.post().uri("/api/rounds/{roundId}/hit", roundId).exchange().expectStatus().isEqualTo(409)
                .expectBody().jsonPath("$.error").isEqualTo("It's not player turn!");

        verify(hitUseCase).hit(roundId);
        verifyNoInteractions(startUseCase);
    }

    @Test
    void shouldReturn409WhenHittingWith21Points() {
        UUID roundId = UUID.randomUUID();

        StartRoundUseCase startUseCase = mock(StartRoundUseCase.class);
        HitRoundUseCase hitUseCase = mock(HitRoundUseCase.class);

        when(hitUseCase.hit(roundId)).thenReturn(Mono.error(new IllegalStateException("Cannot hit with 21")));

        WebTestClient client = WebTestClient.bindToController(new RoundController(startUseCase, hitUseCase))
                .controllerAdvice(new RoundExceptionHandler()).build();

        client.post().uri("/api/rounds/{roundId}/hit", roundId).exchange().expectStatus().isEqualTo(409)
                .expectBody().jsonPath("$.error").isEqualTo("Cannot hit with 21");

        verify(hitUseCase).hit(roundId);
        verifyNoInteractions(startUseCase);
    }

}
