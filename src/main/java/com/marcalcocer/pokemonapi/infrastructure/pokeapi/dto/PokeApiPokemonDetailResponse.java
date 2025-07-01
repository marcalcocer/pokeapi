package com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto;

import lombok.Data;

@Data
public class PokeApiPokemonDetailResponse {
    private String name;
    private int weight;
    private int height;
    private int base_experience;
}
