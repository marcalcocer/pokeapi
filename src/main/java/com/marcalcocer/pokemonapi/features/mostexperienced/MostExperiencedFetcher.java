package com.marcalcocer.pokemonapi.features.mostexperienced;

import com.marcalcocer.pokemonapi.shared.domain.model.Pokemon;
import java.util.List;
import reactor.core.publisher.Mono;

public interface MostExperiencedFetcher {
  Mono<List<Pokemon>> getTop5MostExperienced();
}
