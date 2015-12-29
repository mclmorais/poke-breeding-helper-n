package marcelo.breguenait.breedinghelper;

/**
 * Created by Marcelo on 28/12/2015.
 */
public class AbilityChanceCalculator {

    private static int FEMALE = 1;
    private static int MALE = 2;
    private static int GENDERLESS = 3;
    private static int DITTO = 4;

    boolean considerAbility = false;

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
                if (firstPokemon.getGenderId() == FEMALE)
                    return chance * 0.8;
                    //If the first pokemon is male/genderless, has the nature and is with a ditto, chance is 80%
                else if (firstPokemon.getGenderId() == MALE
                        || firstPokemon.getGenderId() == GENDERLESS
                        && secondPokemon.getGenderId() == DITTO)
                    return chance * 0.8;
            }

            if (secondPokemonSlot == goalSlot) {
                //If the second pokemon has the nature and is female, chance is 80%
                if (secondPokemon.getGenderId() == FEMALE)
                    return chance * 0.8;
                else if (secondPokemon.getGenderId() == MALE
                        || secondPokemon.getGenderId() == GENDERLESS
                        && firstPokemon.getGenderId() == DITTO)
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
