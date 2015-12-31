package marcelo.breguenait.breedinghelper;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Marcelo on 28/12/2015.
 */
public class BreedingManager {

    MyDatabase database;

    int languageId = 9;//TODO: Fazer virar user selectable


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

        for(int i = 225; i < 227; i++) {
            int[] IVs = {1, 1, 1, 1, 0, 1};
            storedPokemonList.add(new StoredPokemon.Builder()
                    .setPokemonId(i)
                    .setAbilitySlot(3)
                    .setNatureId(2)
                    .setGenderId(1)
                    .setIVs(IVs)
                    .createStoredPokemon());
        }
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

    public int getGoalId() {
        return goalPokemon.getPokemonId();
    }

    /**
     * This method should be called when the user has selected a new Pokemon as a goal.
     *
     * @param goalId The ID of the new goal Pokemon.
     */
    public void setGoalId(int goalId) {
        goalPokemon.setPokemonId(goalId);
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


    public void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {

        StoredPokemon newPokemon = new StoredPokemon.Builder()
                .setPokemonId(pokemonId)
                .setGenderId(genderId)
                .setIVs(IVs)
                .setNatureId(natureId)
                .setAbilitySlot(abilitySlot)
                .createStoredPokemon();

        storedPokemonList.add(newPokemon);
    }

    /**
     * TODO: Do this by UUID
     * Removes a pokemon from a position in the list.
     *
     * @param position Position of the Pokemon to be removed.
     */
    @Deprecated
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
                                    firstPokemon.getUUID(),
                                    secondPokemon.getUUID(),
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
        return database.getListOfNatures(languageId);
        //return PokemonData.getInstance().getListOfNatures();
    }

    HashMap<Integer, String> getListOfGoalAbilities() {
        return database.getListOfAbilities(goalPokemon.getPokemonId() > 0 ? goalPokemon.getPokemonId() : 1, 9); //TODO: TEMPORARIO - qd nao tem defaulta pro bulbasauro, fazer lidar com não ter depois
    }

    HashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return database.getListOfAbilities(pokemonId, 9);
    }

    int getGenderRate(int pokemonId) {
        return database.getGenderRate(pokemonId);
    }



    @Deprecated
        //TODO: nao usar isso!
    StoredPokemon getGoalPokemon() {
        return goalPokemon;
    }

    ArrayList<Integer> getCompatiblePokemonList(int pokemonId) {
        if (goalPokemon.getPokemonId() > 0) //Only returns something if a goal is set
            return database.getCompatiblePokemonList(pokemonId);
        else
            return new ArrayList<>(0);
    }

    ArrayList<String> getPokemonNames() {
        return database.getPokemonNames(languageId);
    }

    ArrayList<Integer> getPokemonIds() {
        return database.getPokemonIds();
    }

    ArrayList<InterfaceStoredPokemon> getInterfaceStoredPokemonList() {

        ArrayList<InterfaceStoredPokemon> interfaceStoredPokemonList =
                new ArrayList<>(storedPokemonList.size());

        for (StoredPokemon storedPokemon : storedPokemonList) {
            InterfaceStoredPokemon interfaceStoredPokemon = new InterfaceStoredPokemon(
                    storedPokemon.getUUID(),
                    storedPokemon.getPokemonId(),
                    storedPokemon.getGenderId(),
                    storedPokemon.getIVs()
            );

            interfaceStoredPokemonList.add(interfaceStoredPokemon);
        }
        return interfaceStoredPokemonList;
    }

    InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {

        StoredPokemon desiredPokemon = null;

        int[] IVs = {-1,-1,-1,-1,-1,-1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if(storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if(desiredPokemon == null)
            return new InterfaceViewerPokemon(-1,-1,IVs,"","","");
        else {
            String natureName = database.getNatureName(desiredPokemon.getNatureId(), languageId);
            String abilityName = database.getAbilityName(desiredPokemon.getPokemonId(), desiredPokemon.getAbilitySlot(), languageId);
            String pokemonName = database.getPokemonName(desiredPokemon.getPokemonId(), languageId);
            return new InterfaceViewerPokemon(
                    desiredPokemon.getPokemonId(),
                    desiredPokemon.getGenderId(),
                    desiredPokemon.getIVs(),
                    natureName,
                    abilityName,
                    pokemonName
            );

        }

    }

    String getPokemonName(int pokemonId) {
        return database.getPokemonName(pokemonId, languageId);
    }

    InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
        StoredPokemon desiredPokemon = null;

        int[] IVs = {-1,-1,-1,-1,-1,-1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if(storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if(desiredPokemon == null)
            return new InterfaceModifierPokemon(-1,-1,IVs,-1,-1);
        else {
            return new InterfaceModifierPokemon(
                    desiredPokemon.getPokemonId(),
                    desiredPokemon.getGenderId(),
                    desiredPokemon.getIVs(),
                    desiredPokemon.getNatureId(),
                    desiredPokemon.getAbilitySlot()
            );

        }

    }

    void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {

        StoredPokemon pokemonToBeChanged = null;

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if(storedPokemon.getUUID().equals(uuid)) {
                pokemonToBeChanged = storedPokemon;
                break;
            }
        }

        if(pokemonToBeChanged == null) {
            Log.d("BM", "DIDNT FIND THE UUID THAT WAS SUPPOSED TO BE CHANGED!!!");
        }
        else {
            pokemonToBeChanged.setPokemonId(pokemonId);
            pokemonToBeChanged.setGenderId(genderId);
            pokemonToBeChanged.setIVs(IVs);
            pokemonToBeChanged.setNatureId(natureId);
            pokemonToBeChanged.setAbilitySlot(abilitySlot);
        }


    }

    public void removePokemon(UUID uuid) {
        StoredPokemon pokemonToBeChanged = null;

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if(storedPokemon.getUUID().equals(uuid)) {
                storedPokemonList.remove(storedPokemon);
                break;
            }
        }


    }


}
