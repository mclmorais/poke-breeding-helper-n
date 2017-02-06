package databasemanager;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import breedingmanager.MoveVerbose;
import databasemanager.jsondatablocks.JsonAbilityDataBlock;
import databasemanager.jsondatablocks.JsonEggGroupDataBlock;
import databasemanager.jsondatablocks.JsonNatureDataBlock;
import databasemanager.jsondatablocks.JsonPokedexDataBlock;
import databasemanager.jsondatablocks.JsonTypeDataBlock;

/**
 * Implementation of the necessary database calls through JSON data. All calls for data in the app
 * should be made from this class. As of 30/01/2017, this is the only implementation that is up to
 * date with the 7th generation.
 */

public class JsonDatabaseManager implements NecessaryDatabaseCalls {

    private static JsonDatabaseManager instance;

    private static SparseArray<JsonNatureDataBlock> natureData = new SparseArray<>();
    private static SparseArray<JsonTypeDataBlock> typeData = new SparseArray<>();
    private static SparseArray<JsonEggGroupDataBlock> eggGroupData = new SparseArray<>();
    private static SparseArray<JsonAbilityDataBlock> abilityData = new SparseArray<>();
    private static SparseArray<JsonPokedexDataBlock> pokedexData = new SparseArray<>();
    private final SqlDatabase tempSqlDatabase = SqlDatabase.getInstance();

    private JsonDatabaseManager() {
    }

    static void setNatureData(SparseArray<JsonNatureDataBlock> natureData) {
        JsonDatabaseManager.natureData = natureData;
    }

    static void setTypeData(SparseArray<JsonTypeDataBlock> typeData) {
        JsonDatabaseManager.typeData = typeData;
    }

    static void setEggGroupData(SparseArray<JsonEggGroupDataBlock> eggGroupData) {
        JsonDatabaseManager.eggGroupData = eggGroupData;
    }

    static void setAbilityData(SparseArray<JsonAbilityDataBlock> abilityData) {
        JsonDatabaseManager.abilityData = abilityData;
    }

    static void setPokedexData(SparseArray<JsonPokedexDataBlock> pokedexData) {
        JsonDatabaseManager.pokedexData = pokedexData;
    }

    public static void initialize(Context c) {
        instance = new JsonDatabaseManager();
    }

    public static JsonDatabaseManager getInstance() {
        return instance;

    }

    @Override
    public String getPokemonName(int pokemonId, int languageId) {
        return pokedexData.get(pokemonId).getName();
        //return tempSqlDatabase.getPokemonName(pokemonId, languageId);
    }

    @Override
    public LinkedHashMap<Integer, String> getNatureNames(int languageId) {
        LinkedHashMap<Integer, String> linkedNatures = new LinkedHashMap<>();
        for (int i = 0; i < natureData.size(); i++) {
            linkedNatures.put(natureData.valueAt(i).getId(), natureData.valueAt(i).getCapitalizedName());
        }
        return linkedNatures;
        //return tempSqlDatabase.getNatureNames(languageId);
    }

    @Override
    public ArrayList<Integer> getNatureIdsSortedByIncreasedStat() {
        ArrayList<JsonNatureDataBlock> natureDataList = new ArrayList<>();
        for (int i = 0; i < natureData.size(); i++)
            natureDataList.add(natureData.valueAt(i));
        Collections.sort(natureDataList, new Comparator<JsonNatureDataBlock>() {
            @Override
            public int compare(JsonNatureDataBlock o1, JsonNatureDataBlock o2) {
                return o1.getIncreasedStatId() - o2.getIncreasedStatId();
            }
        });

        ArrayList<Integer> sortedIds = new ArrayList<>();
        for (Iterator<JsonNatureDataBlock> iterator = natureDataList.iterator(); iterator.hasNext(); ) {
            JsonNatureDataBlock b = iterator.next();
            if (b.getIncreasedStatId() == b.getDecreasedStatId()) {
                iterator.remove();
                continue;
            }
            sortedIds.add(b.getId());
        }
        return sortedIds;
        //return tempSqlDatabase.getNatureIdsSortedByIncreasedStat();
    }

    @Override
    public LinkedHashMap<Integer, String> getListOfAbilitiesNames(int pokemonId, int languageId) {
        int[] abilityIds = pokedexData.get(pokemonId).getAbilities();
        LinkedHashMap<Integer, String> abilityNames = new LinkedHashMap<>();
        for (int i = 0; i < abilityIds.length; i++) {
            if (abilityIds[i] > 0)
                abilityNames.put(i + 1, abilityData.get(abilityIds[i]).getName());
        }
        return abilityNames;
        //return tempSqlDatabase.getListOfAbilitiesNames(pokemonId, languageId);
    }

    @Override
    public List<MoveVerbose> getPokemonMoves(int pokemonId, int pokemonVersionId, int methodId, int languageId) {
        return tempSqlDatabase.getPokemonMoves(pokemonId, pokemonVersionId, methodId, languageId);
    }

    @Override
    public ArrayList<Integer> getPokemonEggGroupIds(int pokemonId) {
        ArrayList<Integer> eggGroupIds = new ArrayList<>();
        for (int id : pokedexData.get(pokemonId).getEggGroups())
            if (id > 0)
                eggGroupIds.add(id);
        return eggGroupIds;
        //return tempSqlDatabase.getPokemonEggGroupIds(pokemonId);
    }

    @Override
    public int getEvolutionChainId(int pokemonId) {
        return pokedexData.get(pokemonId).getEvolutionChainId();
        //return tempSqlDatabase.getEvolutionChainId(pokemonId);
    }

    @Override
    public int getGenderRate(int pokemonId) {
        //TODO: Transform
        return tempSqlDatabase.getGenderRate(pokemonId);
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList(int pokemonId) {
        return tempSqlDatabase.getCompatiblePokemonList(pokemonId);
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList(int pokemonId) {
        return tempSqlDatabase.getPokemonFamilyList(pokemonId);
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return tempSqlDatabase.getBasicPokemonList();
    }

    @Override
    public String getNatureName(int natureId, int languageId) {
        return tempSqlDatabase.getNatureName(natureId, languageId);
    }

    @Override
    public int getAbilitySlot(int pokemonId, int abilityId) {
        return tempSqlDatabase.getAbilitySlot(pokemonId, abilityId);
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return tempSqlDatabase.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames(int languageId) {
        return tempSqlDatabase.getPokemonNames(languageId);
    }

    @Override
    public String getAbilityName(int pokemonId, int abilitySlot, int languageId) {
        return tempSqlDatabase.getAbilityName(pokemonId, abilitySlot, languageId);
    }

    @Override
    public String getNatureChangedStatName(int natureId, int languageId, boolean increased) {
        return tempSqlDatabase.getNatureChangedStatName(natureId, languageId, increased);
    }
}
