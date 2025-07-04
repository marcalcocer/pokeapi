package com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto;

import java.util.List;
import lombok.Data;

@Data
public class PokeApiListResponse {
  private List<PokemonSummary> results;

  @Data
  public static class PokemonSummary {
    private String name;
    private String url;
  }
}
