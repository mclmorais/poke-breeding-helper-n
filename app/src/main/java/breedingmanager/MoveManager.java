package breedingmanager;

import java.util.ArrayList;
import java.util.List;

import databasemanager.MyDatabase;

public class MoveManager {
    private final MyDatabase database = MyDatabase.getInstance();
    private int languageId = 9; //TODO: Make dynamic

    public ArrayList<MoveVerbose> getEggMoves(int pokemonId) {
        return (ArrayList<MoveVerbose>)database.getPokemonMoves(pokemonId, 16, 1, languageId);
    }


}
