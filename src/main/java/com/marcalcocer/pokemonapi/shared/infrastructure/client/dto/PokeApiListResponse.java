package com.marcalcocer.pokemonapi.shared.infrastructure.client.dto;

import java.util.List;

public record PokeApiListResponse(List<PokemonSummary> results, String next) {
  public record PokemonSummary(String name, String url) {}
}
