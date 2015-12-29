package marcelo.breguenait.breedinghelper;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by Marcelo on 28/12/2015.
 */
public class BreedingManager {

    MyDatabase database;


    IvChanceCalculator ivChanceCalculator;
    NatureChanceCalculator natureChanceCalculator;
    AbilityChanceCalculator abilityChanceCalculator;
    BreedingCompatibilityChecker breedingCompatibilityChecker;

    private StoredPokemon goalPokemon;

    private ArrayList<StoredPokemon> storedPokemonList = new ArrayList<>();

    private ArrayList<PokemonMatchChance> pokemonMatchChanceList = new ArrayList<>();

    public BreedingManager() {
        ivChanceCalculator = new IvChanceCalculator();
        natureChanceCalculator = new NatureChanceCalculator();
        abilityChanceCalculator = new AbilityChanceCalculator();
        breedingCompatibilityChecker = new BreedingCompatibilityChecker();
        goalPokemon = new StoredPokemon.Builder().createStoredPokemon();
        database = MyDatabase.getInstance();
    }

    /**
     * This method should be called when the user has selected a new Pokemon as a goal.
     *
     * @param goalId The ID of the new goal Pokemon.
     */
    public void setGoalId(int goalId) {
        goalPokemon.setPokemonId(goalId);
    }

    public void setGoalGender(int genderId) {
        goalPokemon.setGenderId(genderId);
    }

    public void setGoalIVs(int[] IVs) {
        goalPokemon.setIVs(IVs);
    }

    public void setGoalNature(int natureId) {
        goalPokemon.setNatureId(natureId);
    }

    public void setGoalAbilitySlot(int abilitySlot) {
        goalPokemon.setAbilitySlot(abilitySlot);
    }



    /**
     * This method should be called when loading a list of stored Pokemon from a previous instance.
     *
     * @param storedPokemonList The list that will replace the current list.
     */
    public void replaceStoredPokemonList(ArrayList<StoredPokemon> storedPokemonList) {
        if (!this.storedPokemonList.isEmpty())
            this.storedPokemonList.clear();

        try {
            if (storedPokemonList == null)
                throw new Exception("Received a null storedPokemonList when replacing lists!");
            else
                this.storedPokemonList = storedPokemonList;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * Stores a Pokemon on the list.
     *
     * @param pokemon The Pokemon to be stored.
     */
    public void storePokemon(StoredPokemon pokemon) {

        try {
            if (pokemon == null)
                throw new Exception("Received a null pokemon when adding one to the stored list!");
            if (storedPokemonList == null)
                throw new Exception("Tried storing a new Pokemon but the list was null!");

            storedPokemonList.add(pokemon);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


    }

    /**
     * TODO: Do this by UUID
     * Removes a pokemon from a position in the list.
     *
     * @param position Position of the Pokemon to be removed.
     */
    public void removePokemon(int position) {
        try {
            if (storedPokemonList == null)
                throw new Exception("Tried removing a Pokemon but the list was null!");
            if (position < 0 || position >= storedPokemonList.size())
                throw new Exception("Tried removing a Pokemon from a position that is not " +
                        "on the list!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void calculateBestMatches() {
        //Doesn't do any calculations if the goal Pokemon isn't set
        if (goalPokemon.getPokemonId() == -1)
            return;

        try {
            if (pokemonMatchChanceList == null)
                throw new Exception("matchChanceList was null when " +
                        "trying to calculate best matches!");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        //Runs through all possible different combinations of pokémon
        for (int i = 0; i < storedPokemonList.size(); i++) {
            StoredPokemon firstPokemon = storedPokemonList.get(i);

            for (int j = (i + 1); j < storedPokemonList.size(); j++) {
                StoredPokemon secondPokemon = storedPokemonList.get(j);

                //Checks for gender/family/egg group compatibility
                if (breedingCompatibilityChecker.
                        checkCompatibility(firstPokemon, secondPokemon, goalPokemon)) {

                    //Gets raw iv chance
                    double chance = ivChanceCalculator.getIvChance(
                            firstPokemon.getIVs(),
                            secondPokemon.getIVs(),
                            goalPokemon.getIVs(),
                            true);

                    //Adds nature chance multiplier
                    chance = natureChanceCalculator.getNatureChance(
                            firstPokemon,
                            secondPokemon,
                            goalPokemon,
                            chance);

                    //Adds ability chance multiplier
                    chance = abilityChanceCalculator.getAbilityChance(
                            firstPokemon,
                            secondPokemon,
                            goalPokemon,
                            chance);

                    //Adds chance to list
                    pokemonMatchChanceList.add(
                            new PokemonMatchChance(
                                    firstPokemon.getStoredId(),
                                    secondPokemon.getStoredId(),
                                    chance));
                }
            }
        }

        class ChanceComparator implements Comparator<PokemonMatchChance> {
            @Override
            public int compare(PokemonMatchChance e1, PokemonMatchChance e2) {
                return Double.compare(e1.chance, e2.chance);
            }
        }

        //Sorts list by biggest to smallest chance
        if (!pokemonMatchChanceList.isEmpty()) {
            Collections.sort(pokemonMatchChanceList, new ChanceComparator());
            Collections.reverse(pokemonMatchChanceList);
        }
    }

    ArrayList<String> getListOfNatures() {
        return database.getListOfNatures(9); //TODO: Fazer 9 virar user selectable
        //return PokemonData.getInstance().getListOfNatures();
    }

    HashMap<Integer, String> getListOfAbilities() {

        return database.getListOfAbilities(goalPokemon.getPokemonId() > 0 ? goalPokemon.getPokemonId():1, 9); //TODO: TEMPORARIO - qd nao tem defaulta pro bulbasauro, fazer lidar com não ter depois
    }
}
