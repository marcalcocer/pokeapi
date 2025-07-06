package com.marcalcocer.pokemonapi.domain.port.in;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import java.util.List;
import reactor.core.publisher.Mono;

public interface HeaviestPokemonFetcher {
  Mono<List<Pokemon>> getTop5Heaviest();
}
