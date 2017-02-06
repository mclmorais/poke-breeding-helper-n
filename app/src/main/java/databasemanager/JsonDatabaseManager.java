package databasemanager;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
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

    private JsonDatabaseManager() {
    }

    public static void setNatureData(SparseArray<JsonNatureDataBlock> natureData) {
        JsonDatabaseManager.natureData = natureData;
    }

    public static void setTypeData(SparseArray<JsonTypeDataBlock> typeData) {
        JsonDatabaseManager.typeData = typeData;
    }

    public static void setEggGroupData(SparseArray<JsonEggGroupDataBlock> eggGroupData) {
        JsonDatabaseManager.eggGroupData = eggGroupData;
    }

    public static void setAbilityData(SparseArray<JsonAbilityDataBlock> abilityData) {
        JsonDatabaseManager.abilityData = abilityData;
    }

    public static void setPokedexData(SparseArray<JsonPokedexDataBlock> pokedexData) {
        JsonDatabaseManager.pokedexData = pokedexData;
    }

    public static void initialize(Context c) {

        instance = new JsonDatabaseManager();
    }

    public static JsonDatabaseManager getInstance() {
        return instance;

    }

    private final SqlDatabase tempSqlDatabase = SqlDatabase.getInstance();

    @Override
    public String getPokemonName(int pokemonId, int languageId) {
        return tempSqlDatabase.getPokemonName(pokemonId, languageId);
    }

    @Override
    public LinkedHashMap<Integer, String> getNatureNames(int languageId) {
        return tempSqlDatabase.getNatureNames(languageId);
    }

    @Override
    public ArrayList<Integer> getNatureIdsSortedByIncreasedStat() {
        return tempSqlDatabase.getNatureIdsSortedByIncreasedStat();
    }

    @Override
    public LinkedHashMap<Integer, String> getListOfAbilitiesNames(int pokemonId, int languageId) {
        return tempSqlDatabase.getListOfAbilitiesNames(pokemonId, languageId);
    }

    @Override
    public List<MoveVerbose> getPokemonMoves(int pokemonId, int pokemonVersionId, int methodId, int languageId) {
        return tempSqlDatabase.getPokemonMoves(pokemonId, pokemonVersionId, methodId, languageId);
    }

    @Override
    public ArrayList<Integer> getPokemonEggGroupIds(int pokemonId) {
        return tempSqlDatabase.getPokemonEggGroupIds(pokemonId);
    }

    @Override
    public int getEvolutionChainId(int pokemonId) {
        return tempSqlDatabase.getEvolutionChainId(pokemonId);
    }

    @Override
    public int getGenderRate(int pokemonId) {
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
