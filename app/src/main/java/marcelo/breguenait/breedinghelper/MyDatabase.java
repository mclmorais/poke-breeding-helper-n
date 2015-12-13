package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.SparseArray;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Marcelo on 12/12/2015.
 */
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "pkmnsql.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public Cursor getPokemonMoves(int pokemonId, int pokemonVersion) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"move_id", "pokemon_move_method_id"};
        String sqlTables = "pokemon_moves";
        String selection = "pokemon_id=1 and version_group_id=" + Integer.toString(pokemonVersion);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection,null,null,null,null,null,null);
        c.moveToFirst();
        return c;
    }

    public TreeMap<Integer, ArrayList<PokemonMoveDB>> getAllPokemonMoves(int pokemonVersion) {

        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"pokemon_id","move_id", "pokemon_move_method_id","level"};
        String sqlTables = "pokemon_moves";
        String selection = "version_group_id=" + Integer.toString(pokemonVersion);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection,null,null,null,null,null,null);
        c.moveToFirst();

        TreeMap<Integer, ArrayList<PokemonMoveDB>> map = new TreeMap<>();

        while(!c.isAfterLast()) {

            if (!map.containsKey(c.getInt(0)))
                map.put(c.getInt(0), new ArrayList<PokemonMoveDB>());

            PokemonMoveDB move = new PokemonMoveDB(c.getInt(1), c.getInt(2), c.getInt(3));

            map.get(c.getInt(0)).add(move);
            c.moveToNext();
        }

        return map;
    }

    public SparseArray<String> getListOfMoves() {
        SQLiteDatabase database = getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] selectedColumns = {"move_id", "name"};
        String queriedTables = "move_names";
        String selection = "local_language_id=9";
        queryBuilder.setTables(queriedTables);

        Cursor c = queryBuilder.query(database,selectedColumns,selection,null,null,null,null,null,null);
        c.moveToFirst();

        SparseArray<String> list = new SparseArray<>();
        while(!c.isAfterLast()) {
            list.append(c.getInt(0),c.getString(1));
            c.moveToNext();
        }
        int x = 3;
        return list;



    }

}
