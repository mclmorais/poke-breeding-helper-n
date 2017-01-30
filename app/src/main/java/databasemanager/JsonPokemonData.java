package databasemanager;

import java.util.ArrayList;

/**
 * Contains general pokemon data retrieved from JSON files. This class receives its information
 * from the JsonDataInterpreter class and provides it to the JsonDataProvider class.
 *
 * Negative values and empty string represent ERROR VALUES: filled data should never contain them.
 * If the pokemon doesn't have a value (e.g. only has one type instead of two), is should be set to
 * a "none" value instead of being left at -1.
 */

class JsonPokemonData {
    private int number = -1;
    private String species = "";
    private int[] types = {-1, -1};
    private int genderRestriction = -1;
    private int genderRatio = -1;
    private int[] abilities = {-1, -1, -1};
    private int form = -1;
    private int[] eggGroups = {-1, -1};
    private int previousEvolution = -1;
    private ArrayList<Integer> possibleEvolutions = new ArrayList<>();
    private int color = -1;
}
