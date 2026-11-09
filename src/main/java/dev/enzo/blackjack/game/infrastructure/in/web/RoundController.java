package dev.enzo.blackjack.game.infrastructure.in.web;

import dev.enzo.blackjack.game.application.port.in.HitRoundUseCase;
import dev.enzo.blackjack.game.application.port.in.StandRoundUseCase;
import dev.enzo.blackjack.game.application.port.in.StartRoundUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/api/rounds")
public class RoundController {
    private final StartRoundUseCase startRoundUseCase;
    private final HitRoundUseCase hitRoundUseCase;
    private final StandRoundUseCase standRoundUseCase;

    public RoundController(StartRoundUseCase startRoundUseCase, HitRoundUseCase hitRoundUseCase, StandRoundUseCase
                           standRoundUseCase) {
        this.startRoundUseCase = startRoundUseCase;
        this.hitRoundUseCase = hitRoundUseCase;
        this.standRoundUseCase = standRoundUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UUID> startRound(@Valid @RequestBody StartRoundRequest request) {

        return startRoundUseCase.startRound(request.playerId());
    }

    @PostMapping("/{roundId}/hit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> hit(@PathVariable UUID roundId) {
        return hitRoundUseCase.hit(roundId);
    }

    @PostMapping("/{roundId}/stand")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> stand(@PathVariable UUID roundId) {
        return standRoundUseCase.stand(roundId);
    }


}
