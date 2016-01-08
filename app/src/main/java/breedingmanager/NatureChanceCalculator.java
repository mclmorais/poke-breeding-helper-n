package breedingmanager;

import android.util.Log;

import databasemanager.DatabaseConstants;

public class NatureChanceCalculator {

    private boolean considerNature = false;
    private boolean hasEverstone = true;

    public boolean considerNature() {
        return considerNature;
    }

    public void setConsiderNature(boolean considerNature) {
        this.considerNature = considerNature;
    }

    public boolean hasEverstone() {
        return hasEverstone;
    }

    public void setHasEverstone(boolean hasEverstone) {
        this.hasEverstone = hasEverstone;
    }

    public double getNatureChance(StoredPokemon firstPokemon,
                                  StoredPokemon secondPokemon,
                                  StoredPokemon goalPokemon,
                                  double chance) {
        if (DatabaseConstants.natureIsValid(goalPokemon.getNatureId())) {
            if (considerNature) {
                if (!hasEverstone ||
                        (firstPokemon.getNatureId() != goalPokemon.getNatureId()
                                && secondPokemon.getNatureId() != goalPokemon.getNatureId()))
                    chance *= (1.0 / 25.0);
            }
        } else {
            Log.w("BM", "Goal Pokémon has an invalid nature. Changing it to Hardy.");
            goalPokemon.setNatureId(1);
        }
        if (firstPokemon.getNatureId() < 1) {
            firstPokemon.setNatureId(1);
            Log.w("BM", "Pokémon " + firstPokemon.getUUID().toString() + " has an invalid " +
                    "nature. Changing it to Hardy.");
        }
        if (secondPokemon.getNatureId() < 1) {
            secondPokemon.setNatureId(1);
            Log.w("BM", "Pokémon " + secondPokemon.getUUID().toString() + " has an invalid " +
                    "nature. Changing it to Hardy.");
        }

        return chance;
    }
}
