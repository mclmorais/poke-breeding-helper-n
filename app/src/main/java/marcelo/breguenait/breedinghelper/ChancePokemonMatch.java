package marcelo.breguenait.breedinghelper;

import java.util.UUID;

/**
 * Created by Marcelo on 28/12/2015.
 */
public class ChancePokemonMatch {
    private UUID firstPokemon;
    private UUID secondPokemon;
    private double chance;

    public ChancePokemonMatch(UUID firstPokemon, UUID secondPokemon, double chance) {
        this.firstPokemon = firstPokemon;
        this.secondPokemon = secondPokemon;
        this.chance = chance;
    }

    public UUID getFirstPokemonUUID() {
        return firstPokemon;
    }

    public UUID getSecondPokemonUUID() {
        return secondPokemon;
    }

    public double getChance() {
        return chance;
    }
}
