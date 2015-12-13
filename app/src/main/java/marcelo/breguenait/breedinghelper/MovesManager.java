package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.database.Cursor;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Marcelo on 13/12/2015.
 */
public class MovesManager {

    MyDatabase db;
    SparseArray<String> movesList;
    TreeMap<Integer, ArrayList<PokemonMoveDB>> pokemonList;

    MovesManager(Context context) {
        db = new MyDatabase(context);
 //       movesList = pokemonDatabase.getListOfMoves();
//        pokemonList = pokemonDatabase.getAllPokemonMoves(15);

    }


    public void showMoves(int id) {

        String tutorMoveString = "Tutor moves: \n";
        Cursor pokemonMoves = db.getPokemonMoves(id, 15);

        List<Integer> moves = db.getPokemonEggMoves(id,15);

        String s = "";
        for(Integer move : moves) {
            s += db.getMoveName(move,15,9);
            s += "\n";

            List<String> parents = db.getParentsEggMoveNames(id, move);
            for(String parent : parents) {
                s += "-->"+parent;
                s += "\n";
            }

        }

        System.out.println(s);
    }

    @SuppressWarnings("unchecked")
    public void showMovesAsync(int id) {

        new MyAsyncTask().execute(MovesManager.this, id);

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

