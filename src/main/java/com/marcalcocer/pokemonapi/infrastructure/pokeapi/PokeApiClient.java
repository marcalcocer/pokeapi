package com.marcalcocer.pokemonapi.infrastructure.pokeapi;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto.PokeApiListResponse;
import com.marcalcocer.pokemonapi.infrastructure.pokeapi.dto.PokeApiPokemonDetailResponse;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class PokeApiClient {

  private final WebClient webClient;

  public List<String> getAllPokemonNames() {
    return webClient
        .get()
        .uri("/pokemon?limit=1000")
        .retrieve()
        .bodyToMono(PokeApiListResponse.class)
        .map(
            resp ->
                resp.getResults().stream()
                    .map(PokeApiListResponse.PokemonSummary::getName)
                    .collect(Collectors.toList()))
        .block();
  }

  public Pokemon getPokemonDetail(String name) {
    PokeApiPokemonDetailResponse dto =
        webClient
            .get()
            .uri("/pokemon/{name}", name)
            .retrieve()
            .bodyToMono(PokeApiPokemonDetailResponse.class)
            .block();

    if (dto == null) {
      return null;
    }

    return Pokemon.builder()
        .name(dto.getName())
        .height(dto.getHeight())
        .weight(dto.getWeight())
        .baseExperience(dto.getBase_experience())
        .build();
  }
}
