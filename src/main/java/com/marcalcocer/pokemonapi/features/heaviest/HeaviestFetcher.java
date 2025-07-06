package com.marcalcocer.pokemonapi.features.heaviest;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import java.util.List;
import reactor.core.publisher.Mono;

public interface HeaviestFetcher {
  Mono<List<Pokemon>> getTop5Heaviest();
}
