package breedingmanager;

import java.util.ArrayList;

import databasemanager.JsonDatabaseManager;

public class MoveManager {
    private final JsonDatabaseManager database = JsonDatabaseManager.getInstance();
    private int languageId = 9; //TODO: Make dynamic

    public ArrayList<MoveVerbose> getEggMoves(int pokemonId) {
        return (ArrayList<MoveVerbose>)database.getPokemonMoves(pokemonId, 16, 1, languageId);
    }


}
