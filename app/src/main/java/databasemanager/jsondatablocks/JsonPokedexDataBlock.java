package databasemanager.jsondatablocks;

import java.util.ArrayList;

/**
 * Contains general pokemon data retrieved from JSON files. This class receives its information
 * from the JsonFileReader class and provides it to the JsonDatabaseManager class.
 * <p>
 * Negative values and empty string represent ERROR VALUES: filled data should never contain them.
 * If the pokemon doesn't have a value (e.g. only has one type instead of two), is should be set to
 * a "none" value instead of being left at -1.
 */

public class JsonPokedexDataBlock {
    private int id = -1;

    private int number = -1;
    private String name = null;
    private int gender = -1;
    private int[] types = {-1, -1};
    private int[] abilities = {-1, -1, -1};
    private int[] eggGroups = {-1, -1};
    private int evolutionChain = -1;
    private int previousEvolution = -1;
    private ArrayList<Integer> possibleEvolutions = new ArrayList<>();

    public JsonPokedexDataBlock(int id, int number, String name, int gender, int[] types, int[] abilities, int[]
            eggGroups, int evolutionChain, int previousEvolution, ArrayList<Integer> possibleEvolutions) {
        this.id = id;
        this.number = number;
        this.name = name;
        this.gender = gender;
        this.types = types;
        this.abilities = abilities;
        this.eggGroups = eggGroups;
        this.evolutionChain = evolutionChain;
        this.previousEvolution = previousEvolution;
        this.possibleEvolutions = possibleEvolutions;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public int[] getTypes() {
        return types;
    }

    public int[] getAbilities() {
        return abilities;
    }

    public int[] getEggGroups() {
        return eggGroups;
    }

    public int getEvolutionChain() {
        return evolutionChain;
    }

    public int getPreviousEvolution() {
        return previousEvolution;
    }

    public ArrayList<Integer> getPossibleEvolutions() {
        return possibleEvolutions;
    }
}


