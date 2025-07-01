package com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class PokeApiListResponse {
    private List<PokemonSummary> results;

    @Data
    public static class PokemonSummary {
        private String name;
        private String url;
    }
}
