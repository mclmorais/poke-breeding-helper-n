package marcelo.breguenait.breedinghelper;

import java.util.UUID;

/**
 * Created by Marcelo on 28/12/2015.
 */
public class PokemonMatchChance {
    UUID firstPokemon;
    UUID secondPokemon;
    double chance;

    public PokemonMatchChance(UUID firstPokemon, UUID secondPokemon, double chance) {
        this.firstPokemon = firstPokemon;
        this.secondPokemon = secondPokemon;
        this.chance = chance;
    }
}
