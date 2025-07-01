package com.marcalcocer.pokemonapi.application.service;

import com.marcalcocer.pokemonapi.domain.model.Pokemon;
import com.marcalcocer.pokemonapi.domain.port.PokemonCachePort;
import com.marcalcocer.pokemonapi.infrastructure.pokeapi.PokeApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetHeaviestPokemonsService {
    private final PokeApiClient pokeApiClient;
    private final PokemonCachePort cache;

    public List<Pokemon> execute() {
        List<String> names = pokeApiClient.getAllPokemonNames();

        return names.stream()
            .map(name -> cache.get(name).orElseGet(() -> {
                Pokemon pokemon = pokeApiClient.getPokemonDetail(name);
                cache.save(pokemon);
                return pokemon;
            }))
            .sorted(Comparator.comparingInt(Pokemon::getWeight).reversed())
            .limit(5)
            .collect(Collectors.toList());
    }
}
