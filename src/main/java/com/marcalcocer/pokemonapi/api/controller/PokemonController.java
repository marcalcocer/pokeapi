package com.marcalcocer.pokemonapi.api.controller;

import com.marcalcocer.pokemonapi.application.service.GetHeaviestPokemonsService;
import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pokemons")
@RequiredArgsConstructor
public class PokemonController {

  private final GetHeaviestPokemonsService getHeaviestPokemonsService;

  @GetMapping("/heaviest")
  public ResponseEntity<List<Pokemon>> getHeaviestPokemons() {
    List<Pokemon> result = getHeaviestPokemonsService.execute();
    return ResponseEntity.ok(result);
  }
}
