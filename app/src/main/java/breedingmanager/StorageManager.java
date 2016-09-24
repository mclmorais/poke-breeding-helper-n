package breedingmanager;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.UUID;

import databasemanager.DatabaseConstants;
import databasemanager.MyDatabase;
import marcelo.breguenait.breedinghelper.AssistantActivity;
import marcelo.breguenait.breedinghelper.ChanceFragment;
import marcelo.breguenait.breedinghelper.InterfaceModifierPokemon;
import marcelo.breguenait.breedinghelper.InterfaceNature;
import marcelo.breguenait.breedinghelper.StoredPokemonFragment;
import marcelo.breguenait.breedinghelper.StoredPokemonViewerFragment;

//TODO: fazer o sort sempre preferir as combinações com mais IVs no caso de empate?

public class StorageManager {

    private final MyDatabase database;
    private final IvChanceCalculator ivChanceCalculator;
    private final NatureChanceCalculator natureChanceCalculator;
    private final AbilityChanceCalculator abilityChanceCalculator;
    private final BreedingCompatibilityChecker breedingCompatibilityChecker;
    private int languageId = 9;
    private StoredPokemon goalPokemon;

    private boolean destinyKnot = true;
    private ArrayList<StoredPokemon> storedPokemonList = new ArrayList<>();

    public StorageManager() {
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

    @SuppressWarnings("unused")
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

    public ArrayList<InterfaceNature> getInterfaceNatures() {

        ArrayList<InterfaceNature> interfaceNatures = new ArrayList<>();

        LinkedHashMap<Integer, String> sortedNatureNames = database.getNatureNames(languageId);


        ArrayList<Integer> sortedIds = database.getNatureIdsSortedByIncreasedStat();

        for (Integer sortedId : sortedIds) {
            String increasedStatName = database.getNatureChangedStatName(sortedId, languageId, true);
            String decreasedStatName = database.getNatureChangedStatName(sortedId, languageId, false);

            interfaceNatures.add(new InterfaceNature(
                    sortedId, sortedNatureNames.get(sortedId), increasedStatName, decreasedStatName
            ));
        }

        return interfaceNatures;
    }

    public LinkedHashMap<Integer, String> getListOfGoalAbilities() {
        return database.getListOfAbilitiesNames(goalPokemon.getPokemonId(), languageId);
    }

    public LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return database.getListOfAbilitiesNames(pokemonId, languageId);
    }

    public int getGenderRate(int pokemonId) {
        return database.getGenderRate(pokemonId);
    }

    public ArrayList<Integer> getCompatiblePokemonList(int pokemonId) {
        if (DatabaseConstants.pokemonIdIsValid(goalPokemon.getPokemonId())) //Only returns something if a goal is set and valid
            return database.getCompatiblePokemonList(pokemonId);
        else
            return new ArrayList<>(0);
    }

    public ArrayList<Integer> getPokemonFamilyList(int pokemonId) {
        if (goalPokemon.getPokemonId() > 0) //Only returns something if a goal is set
            return database.getPokemonFamilyList(pokemonId);
        else
            return new ArrayList<>(0);
    }

    public ArrayList<Integer> getBasicPokemonList() {
        return database.getBasicPokemonList();
    }

    public ArrayList<String> getPokemonNames() {
        return database.getPokemonNames(languageId);
    }

    public ArrayList<Integer> getPokemonIds() {
        return database.getPokemonIds();
    }

    public ArrayList<StoredPokemonFragment.InterfaceStoredPokemon> getInterfaceStoredPokemonList() {

        ArrayList<StoredPokemonFragment.InterfaceStoredPokemon> interfaceStoredPokemonList =
                new ArrayList<>(storedPokemonList.size());

        for (StoredPokemon storedPokemon : storedPokemonList) {
            StoredPokemonFragment.InterfaceStoredPokemon interfaceStoredPokemon = new StoredPokemonFragment.InterfaceStoredPokemon(
                    storedPokemon.getUUID(),
                    storedPokemon.getPokemonId(),
                    storedPokemon.getGenderId(),
                    storedPokemon.getIVs()
            );

            interfaceStoredPokemonList.add(interfaceStoredPokemon);
        }
        return interfaceStoredPokemonList;
    }

    public StoredPokemonViewerFragment.InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {

        StoredPokemon desiredPokemon = null;

        int[] IVs = {-1, -1, -1, -1, -1, -1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if (storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if (desiredPokemon == null)
            return new StoredPokemonViewerFragment.InterfaceViewerPokemon(-1, -1, IVs, "", "", "");
        else {
            String natureName = database.getNatureName(desiredPokemon.getNatureId(), languageId);
            String abilityName = database.getAbilityName(desiredPokemon.getPokemonId(), desiredPokemon.getAbilitySlot(), languageId);
            String pokemonName = database.getPokemonName(desiredPokemon.getPokemonId(), languageId);
            return new StoredPokemonViewerFragment.InterfaceViewerPokemon(
                    desiredPokemon.getPokemonId(),
                    desiredPokemon.getGenderId(),
                    desiredPokemon.getIVs(),
                    natureName,
                    abilityName,
                    pokemonName
            );

        }

    }

    public String getPokemonName(int pokemonId) {
        return database.getPokemonName(pokemonId, languageId);
    }

    public InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
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

    public void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {

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

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if (storedPokemon.getUUID().equals(uuid)) {
                storedPokemonList.remove(storedPokemon);
                break;
            }
        }


    }

    public ChanceFragment.InterfaceChancePokemon getInterfaceChancePokemon(UUID uuid) {
        StoredPokemon desiredPokemon = null;

        int[] IVs = {-1, -1, -1, -1, -1, -1};

        for (StoredPokemon storedPokemon : storedPokemonList) {
            if (storedPokemon.getUUID().equals(uuid)) {
                desiredPokemon = storedPokemon;
                break;
            }
        }

        if (desiredPokemon == null)
            return new ChanceFragment.InterfaceChancePokemon(-1, -1, IVs, "", "", false);
        else {
            String natureName = database.getNatureName(desiredPokemon.getNatureId(), languageId);
            String abilityName = database.getAbilityName(desiredPokemon.getPokemonId(), desiredPokemon.getAbilitySlot(), languageId);
            boolean sameNature = (desiredPokemon.getNatureId() == goalPokemon.getNatureId());

            return new ChanceFragment.InterfaceChancePokemon(
                    desiredPokemon.getPokemonId(),
                    desiredPokemon.getGenderId(),
                    desiredPokemon.getIVs(),
                    natureName,
                    abilityName,
                    sameNature
            );
        }
    }

    public AssistantActivity.InterfaceGoalPokemon getInterfaceGoalPokemon() {
        String pokemonName = database.getPokemonName(goalPokemon.getPokemonId(), languageId);
        return new AssistantActivity.InterfaceGoalPokemon(
                goalPokemon.getIVs(),
                goalPokemon.getPokemonId(),
                pokemonName,
                goalPokemon.getNatureId(),
                goalPokemon.getAbilitySlot()
        );
    }


    public ArrayList<StoredPokemon> getStoredPokemonObjects() {
        return storedPokemonList;
    }

    public StoredPokemon getGoalObject() {
        return goalPokemon;
    }

    public void replaceStoredPokemonObjects(ArrayList<StoredPokemon> storedPokemonList) {
        this.storedPokemonList = storedPokemonList;
    }

    public void replaceGoalObject(StoredPokemon goalPokemon) {
        this.goalPokemon = goalPokemon;
    }

    public void setConsiderNature(boolean b) {
        natureChanceCalculator.setConsiderNature(b);
    }

    public void setConsiderAbility(boolean b) {
        abilityChanceCalculator.setConsiderAbility(b);
    }

    public boolean considerAbility() {
        return abilityChanceCalculator.considerAbility();
    }

    public boolean considerNature() {
        return natureChanceCalculator.considerNature();
    }

    public boolean hasEverstone() {
        return natureChanceCalculator.hasEverstone();
    }

    public void setEverstone(boolean b) {
        natureChanceCalculator.setHasEverstone(b);
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }
}
