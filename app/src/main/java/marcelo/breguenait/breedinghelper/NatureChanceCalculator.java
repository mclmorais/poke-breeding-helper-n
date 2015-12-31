package marcelo.breguenait.breedinghelper;

/**
 * Created by Marcelo on 28/12/2015.
 */
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
        if(considerNature && goalPokemon.getNatureId() > 0) {
            if(!hasEverstone ||
                    (firstPokemon.getNatureId() != goalPokemon.getNatureId()
                            && secondPokemon.getNatureId() != goalPokemon.getNatureId()))
                    chance *= (1.0/25.0);
        }

        return chance;
    }
}
