package breedingmanager;

import databasemanager.DatabaseConstants;

class AbilityChanceCalculator {
    private boolean considerAbility = false;

    public boolean considerAbility() {
        return considerAbility;
    }

    public void setConsiderAbility(boolean considerAbility) {
        this.considerAbility = considerAbility;
    }

    public double getAbilityChance(StoredPokemon firstPokemon,
                                   StoredPokemon secondPokemon,
                                   StoredPokemon goalPokemon,
                                   double chance) {

        int firstPokemonSlot, secondPokemonSlot, goalSlot;

        if (considerAbility && goalPokemon.getAbilitySlot() > 0) {
            firstPokemonSlot = firstPokemon.getAbilitySlot();
            secondPokemonSlot = secondPokemon.getAbilitySlot();
            goalSlot = goalPokemon.getAbilitySlot();

            if (firstPokemonSlot == goalSlot) {
                //If the first pokemon has the same nature and is female, chance is 80%
                if (firstPokemon.getGenderId() == DatabaseConstants.FEMALE_ID)
                    return chance * 0.8;
                    //If the first pokemon is male/genderless, has the nature and is with a ditto, chance is 80%
                else if (firstPokemon.getGenderId() == DatabaseConstants.MALE_ID
                        || firstPokemon.getGenderId() == DatabaseConstants.GENDERLESS_ID
                        && secondPokemon.getPokemonId() == DatabaseConstants.DITTO_ID)
                    return chance * 0.8;
            }

            if (secondPokemonSlot == goalSlot) {
                //If the second pokemon has the nature and is female, chance is 80%
                if (secondPokemon.getGenderId() == DatabaseConstants.FEMALE_ID)
                    return chance * 0.8;
                else if (secondPokemon.getGenderId() == DatabaseConstants.MALE_ID
                        || secondPokemon.getGenderId() == DatabaseConstants.GENDERLESS_ID
                        && firstPokemon.getPokemonId() == DatabaseConstants.DITTO_ID)
                    return chance * 0.8;
            }

            //If none of the appropriate conditions applied, chance is 20% for non-hidden or 0% for hidden
            if (goalSlot != 3) {
                return chance * 0.2;
            }

            return 0.0;

        }

        return chance;
    }
}
