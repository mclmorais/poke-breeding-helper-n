package breedingmanager;

import java.util.ArrayList;

import databasemanager.JsonDataProvider;

public class MoveManager {
    private final JsonDataProvider database = JsonDataProvider.getInstance();
    private int languageId = 9; //TODO: Make dynamic

    public ArrayList<MoveVerbose> getEggMoves(int pokemonId) {
        return (ArrayList<MoveVerbose>)database.getPokemonMoves(pokemonId, 16, 1, languageId);
    }


}
