package com.marcalcocer.pokemonapi.features.highest;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import java.util.List;
import reactor.core.publisher.Mono;

public interface HighestFetcher {
  Mono<List<Pokemon>> getTop5Highest();
}
