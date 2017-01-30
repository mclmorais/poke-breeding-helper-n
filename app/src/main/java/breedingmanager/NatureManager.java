package breedingmanager;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import databasemanager.DatabaseConstants;
import databasemanager.JsonDatabase;

/**
 * Created by Marcelo on 23/09/2016.
 */

public class NatureManager {
    private final JsonDatabase database = JsonDatabase.getInstance();
    private int languageId = 9; //TODO: Make dynamic
    private boolean considerNature = false;
    private boolean hasEverstone = true;

    public ArrayList<NatureVerbose> getInterfaceNatures() {

        ArrayList<NatureVerbose> natureVerboses = new ArrayList<>();

        LinkedHashMap<Integer, String> sortedNatureNames = database.getNatureNames(languageId);


        ArrayList<Integer> sortedIds = database.getNatureIdsSortedByIncreasedStat();

        for (Integer sortedId : sortedIds) {
            String increasedStatName = database.getNatureChangedStatName(sortedId, languageId, true);
            String decreasedStatName = database.getNatureChangedStatName(sortedId, languageId, false);

            natureVerboses.add(new NatureVerbose(
                    sortedId, sortedNatureNames.get(sortedId), increasedStatName, decreasedStatName
            ));
        }

        return natureVerboses;
    }

    public boolean getNatureModifier() {
        return considerNature;
    }

    public void setNatureModifier(boolean considerNature) {
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

    public static class NatureVerbose {
        public final int id;
        public final String natureName;
        public final String increasedStatName;
        public final String decreasedStatName;

        public NatureVerbose(int id, String natureName, String increasedStatName, String decreasedStatName) {
            this.id = id;
            this.natureName = natureName;
            this.increasedStatName = increasedStatName;
            this.decreasedStatName = decreasedStatName;
        }
    }
}
