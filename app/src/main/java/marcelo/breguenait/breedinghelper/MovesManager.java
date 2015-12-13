package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Marcelo on 13/12/2015.
 */
public class MovesManager {

    MyDatabase pokemonDatabase;
    SparseArray<String> movesList;
    TreeMap<Integer, ArrayList<PokemonMoveDB>> pokemonList;

    MovesManager(Context context) {
 //       pokemonDatabase = new MyDatabase(context);
 //       movesList = pokemonDatabase.getListOfMoves();
//        pokemonList = pokemonDatabase.getAllPokemonMoves(15);

    }



}

class PokemonMoveDB {
    int id;
    int method_id;
    int level;

    public PokemonMoveDB(int id, int method_id, int level) {
        this.id = id;
        this.method_id = method_id;
        this.level = level;
    }
}

