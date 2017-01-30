package databasemanager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import breedingmanager.MoveVerbose;

/**
 * Describes generic virtual functions that are needed by the app for its operation.
 * A database that extends this class should override all virtual functions in order to
 * function properly. Database calls in the app should only be done to functions present
 * in this class, and never to functions from the child classes, so that a possible
 * transition to another database can happen without need of refactoring at the app's code.
 */

interface NecessaryDatabaseCalls {

    String getPokemonName(int pokemonId, int languageId);
    String getAbilityName(int pokemonId, int abilitySlot, int languageId);
    LinkedHashMap<Integer, String> getNatureNames(int languageId);
    ArrayList<Integer> getNatureIdsSortedByIncreasedStat();
    LinkedHashMap<Integer, String> getListOfAbilitiesNames(int pokemonId, int languageId);
    List<MoveVerbose> getPokemonMoves(int pokemonId, int pokemonVersionId, int methodId, int languageId);
    ArrayList<Integer> getPokemonEggGroupIds(int pokemonId);
    int getEvolutionChainId(int pokemonId);
    int getGenderRate(int pokemonId);
    ArrayList<Integer> getCompatiblePokemonList(int pokemonId);
    ArrayList<Integer> getPokemonFamilyList(int pokemonId);
    ArrayList<Integer> getBasicPokemonList();
    String getNatureName(int natureId, int languageId);
    int getAbilitySlot(int pokemonId, int abilityId);
    ArrayList<Integer> getPokemonIds();
    ArrayList<String> getPokemonNames(int languageId);
    String getNatureChangedStatName(int natureId, int languageId, boolean increased);
}
