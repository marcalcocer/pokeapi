package com.marcalcocer.pokemonapi.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PokeApiPokemonDetailResponse(
    int id,
    String name,
    int weight,
    int height,
    @JsonProperty("base_experience") int baseExperience) {}
