package marcelo.breguenait.breedinghelper;

public class BreedingCompatibilityChecker {


    private static final int FEMALE = 1;
    private static final int MALE = 2;
    private static final int GENDERLESS = 3;
    private static final int DITTO_ID = 132;

    public boolean checkCompatibility(StoredPokemon firstPokemon,
                                      StoredPokemon secondPokemon,
                                      StoredPokemon goalPokemon) {

        /*Undiscovered Pokemon can't breed*/
        if(firstPokemon.getEggGroup1Id() == 15 || secondPokemon.getEggGroup1Id() == 15)
            return false;

        /*If it's MALE + FEMALE*/
        if (firstPokemon.getGenderId() == MALE && secondPokemon.getGenderId() == FEMALE) {
            //Female has to be in Goal's family
            boolean femaleIsCompatible = checkFamilyCompatibility(secondPokemon, goalPokemon);
            //Male has to be in Goal's egg group
            boolean maleIsCompatible = checkEggGroupCompatibility(firstPokemon, goalPokemon);

            return (femaleIsCompatible && maleIsCompatible);
        }
        /*If it's FEMALE + MALE*/
        else if (firstPokemon.getGenderId() == FEMALE && secondPokemon.getGenderId() == MALE) {
            //Female has to be in Goal's family
            boolean femaleIsCompatible = checkFamilyCompatibility(firstPokemon, goalPokemon);
            //Male has to be in Goal's egg group
            boolean maleIsCompatible = checkEggGroupCompatibility(secondPokemon, goalPokemon);

            return (femaleIsCompatible && maleIsCompatible);
        }
        /*If it's DITTO + SOMETHING*/
        else if (firstPokemon.getPokemonId() == DITTO_ID) {
            //Other pokemon can't also be a ditto and has to be in the family of the goal
            if (secondPokemon.getPokemonId() != DITTO_ID) {
                return checkFamilyCompatibility(secondPokemon, goalPokemon);
            }
        }
        /*If it's SOMETHING + DITTO*/
        else if (secondPokemon.getPokemonId() == DITTO_ID) {
            //Other pokemon can't also be a ditto and has to be in the family of the goal
            return checkFamilyCompatibility(firstPokemon, goalPokemon);

        }

    //    System.err.println("Could NOT determine gender compatibility!");
        return false;
    }


    private boolean checkFamilyCompatibility(StoredPokemon potentialPokemon,
                                             StoredPokemon goalPokemon) {
        int potentialEvolutionChainId = potentialPokemon.getEvolutionChainId();
        int goalEvolutionChainId = goalPokemon.getEvolutionChainId();

        //Shortcut for nidoran anomaly
        if(potentialEvolutionChainId == 12 && goalEvolutionChainId == 13) return true;

        //Shortcut for volbeat/illumise anomaly
        if(potentialEvolutionChainId == 157 && goalEvolutionChainId == 156) return true;

        /*Checks if they're the same AND if they're not unset (-1)*/
        return potentialEvolutionChainId > 0
                && goalEvolutionChainId > 0
                && (potentialEvolutionChainId == goalEvolutionChainId);

    }

    private boolean checkEggGroupCompatibility(StoredPokemon potentialPokemon,
                                               StoredPokemon goalPokemon) {
        

        /*If the 1st egg group of the potential pokemon matches any of the goal
        * pokemon, it's compatible*/
        if (potentialPokemon.getEggGroup1Id() == goalPokemon.getEggGroup1Id()
                || potentialPokemon.getEggGroup1Id() == goalPokemon.getEggGroup2Id())
            return true;

        /*If the potential Pokemon has a 2nd egg group and it's compatible with any
        * of the goal pokemon, it's compatible*/
        if (potentialPokemon.getEggGroup2Id() > 0) {
            if (potentialPokemon.getEggGroup2Id() == goalPokemon.getEggGroup1Id()
                    || potentialPokemon.getEggGroup2Id() == goalPokemon.getEggGroup2Id())
                return true;
        }

        /*Otherwise, it's not compatible*/
        return false;
    }

}
