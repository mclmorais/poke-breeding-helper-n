package marcelo.breguenait.breedinghelper;
//TODO: fazer o filtro de colocar na frente lembrar de nidoran e volbeat
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

//TODO: tirar babies de matches boas!

public class BreedingManager {

    MyDatabase database;

    int languageId = 9;//TODO: Fazer virar user selectable


    IvChanceCalculator ivChanceCalculator;
    NatureChanceCalculator natureChanceCalculator;
    AbilityChanceCalculator abilityChanceCalculator;
    BreedingCompatibilityChecker breedingCompatibilityChecker;

    private StoredPokemon goalPokemon;

    private boolean destinyKnot = true;
    private ArrayList<StoredPokemon> storedPokemonList = new ArrayList<>();

    public BreedingManager() {
        ivChanceCalculator = new IvChanceCalculator();
        natureChanceCalculator = new NatureChanceCalculator();
        abilityChanceCalculator = new AbilityChanceCalculator();
        breedingCompatibilityChecker = new BreedingCompatibilityChecker();
        goalPokemon = new StoredPokemon.Builder().createStoredPokemon();
        database = MyDatabase.getInstance();

    }

    public boolean hasDestinyKnot() {
        return destinyKnot;
    }

    public void setDestinyKnot(boolean destinyKnot) {
        this.destinyKnot = destinyKnot;
    }

    public ArrayList<ChancePokemonMatch> calculateBestMatches() {

        //Doesn't do any calculations if the goal Pokemon isn't set
        if (goalPokemon.getPokemonId() == -1)
            return new ArrayList<>(0);

        ArrayList<ChancePokemonMatch> chancePokemonMatchList = new ArrayList<>();

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
                            destinyKnot);

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
                    if (chance > 0.0d)
                        chancePokemonMatchList.add(
                                new ChancePokemonMatch(
                                        firstPokemon.getUUID(),
                                        secondPokemon.getUUID(),
                                        chance));
                }
            }
        }

        class ChanceComparator implements Comparator<ChancePokemonMatch> {
            @Override
            public int compare(ChancePokemonMatch e1, ChancePokemonMatch e2) {
                return Double.compare(e1.getChance(), e2.getChance());
            }
        }

        //Sorts list by biggest to smallest chance
        if (!chancePokemonMatchList.isEmpty()) {
            Collections.sort(chancePokemonMatchList, new ChanceComparator());
            Collections.reverse(chancePokemonMatchList);
        }

        return chancePokemonMatchList;
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

    ArrayList<String> getListOfNatures() {
        return database.getListOfNatures(languageId);
        //return PokemonData.getInstance().getListOfNatures();
    }

    HashMap<Integer, String> getListOfGoalAbilities() {
        return database.getListOfAbilities(goalPokemon.getPokemonId(), languageId);
    }

    HashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return database.getListOfAbilities(pokemonId, languageId);
    }

    int getGenderRate(int pokemonId) {
        return database.getGenderRate(pokemonId);
    }

    ArrayList<Integer> getCompatiblePokemonList(int pokemonId) {
        if (goalPokemon.getPokemonId() > 0) //Only returns something if a goal is set
            return database.getCompatiblePokemonList(pokemonId);
        else
            return new ArrayList<>(0);
    }

    ArrayList<Integer> getPokemonFamilyList(int pokemonId) {
        if (goalPokemon.getPokemonId() > 0) //Only returns something if a goal is set
            return database.getPokemonFamilyList(pokemonId);
        else
            return new ArrayList<>(0);
    }



    ArrayList<Integer> getBasicPokemonList() {
        return database.getBasicPokemonList();
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

        int[] IVs = {-1, -1, -1, -1, -1, -1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if (storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if (desiredPokemon == null)
            return new InterfaceViewerPokemon(-1, -1, IVs, "", "", "");
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

        int[] IVs = {-1, -1, -1, -1, -1, -1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if (storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if (desiredPokemon == null)
            return new InterfaceModifierPokemon(-1, -1, IVs, -1, -1);
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
            if (storedPokemon.getUUID().equals(uuid)) {
                pokemonToBeChanged = storedPokemon;
                break;
            }
        }

        if (pokemonToBeChanged == null) {
            Log.d("BM", "DIDNT FIND THE UUID THAT WAS SUPPOSED TO BE CHANGED!!!");
        } else {
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
            if (storedPokemon.getUUID().equals(uuid)) {
                storedPokemonList.remove(storedPokemon);
                break;
            }
        }


    }

    InterfaceChancePokemon getInterfaceChancePokemon(UUID uuid) {
        StoredPokemon desiredPokemon = null;

        int[] IVs = {-1, -1, -1, -1, -1, -1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if (storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if (desiredPokemon == null)
            return new InterfaceChancePokemon(-1, -1, IVs, "", "", false);
        else {
            String natureName = database.getNatureName(desiredPokemon.getNatureId(), languageId);
            String abilityName = database.getAbilityName(desiredPokemon.getPokemonId(), desiredPokemon.getAbilitySlot(), languageId);
            boolean sameNature = (desiredPokemon.getNatureId() == goalPokemon.getNatureId());

            return new InterfaceChancePokemon(
                    desiredPokemon.getPokemonId(),
                    desiredPokemon.getGenderId(),
                    desiredPokemon.getIVs(),
                    natureName,
                    abilityName,
                    sameNature
            );
        }
    }

    InterfaceGoalPokemon getInterfaceGoalPokemon() {
        String pokemonName = database.getPokemonName(goalPokemon.getPokemonId(), languageId);
        return new InterfaceGoalPokemon(
                goalPokemon.getIVs(),
                goalPokemon.getPokemonId(),
                pokemonName,
                goalPokemon.getNatureId(),
                goalPokemon.getAbilitySlot()
        );

    }

    ArrayList<StoredPokemon> getStoredPokemonObjects() {
        return storedPokemonList;
    }

    StoredPokemon getGoalObject() {
        return goalPokemon;
    }

    void replaceStoredPokemonObjects(ArrayList<StoredPokemon> storedPokemonList) {
        this.storedPokemonList = storedPokemonList;
    }

    void replaceGoalObject(StoredPokemon goalPokemon) {
        this.goalPokemon = goalPokemon;
    }


    public void setConsiderNature(boolean b) {
        natureChanceCalculator.setConsiderNature(b);
    }

    public void setConsiderAbility(boolean b) {
        abilityChanceCalculator.setConsiderAbility(b);
    }

    boolean considerAbility() {
        return abilityChanceCalculator.considerAbility();
    }

    boolean considerNature() {
        return natureChanceCalculator.considerNature();
    }

    boolean hasEverstone() {
        return natureChanceCalculator.hasEverstone();
    }

    void setEverstone(boolean b) {
        natureChanceCalculator.setHasEverstone(b);
    }

}
