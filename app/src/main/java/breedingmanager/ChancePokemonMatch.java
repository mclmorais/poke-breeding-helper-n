package breedingmanager;

import java.util.UUID;

public class ChancePokemonMatch {
    private final UUID firstPokemon;
    private final UUID secondPokemon;
    private final double chance;

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
