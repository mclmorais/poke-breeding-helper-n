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

        String [] sqlSelect = {"move_id", "pokemon_move_method_id", "level"};
        String sqlTables = "pokemon_moves";
        String selection = "pokemon_id=" + Integer.toString(pokemonId)+ " and version_group_id=" + Integer.toString(pokemonVersion);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection,null,null,null,null,null,null);
        c.moveToFirst();
        return c;
    }

    public List<Integer> getPokemonEggMoves(int pokemonId, int pokemonVersionId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"move_id"};
        String sqlTables = "pokemon_moves";
        String selection = "pokemon_id="
                + Integer.toString(pokemonId)
                + " and version_group_id="
                + Integer.toString(pokemonVersionId)
                + " and pokemon_move_method_id=2";


        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection,null,null,null,null,null,null);
        c.moveToFirst();


        List<Integer> list = new ArrayList<>();
        while(!c.isAfterLast()) {
            list.add(c.getInt(0));
            c.moveToNext();
        }

        return list;
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

    public String getMoveName(int id, int pokemonVersionId, int languageId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"name"};
        String sqlTables = "move_names";
        String selection = "local_language_id="+Integer.toString(languageId) + " and move_id="+Integer.toString(id);

        queryBuilder.setTables(sqlTables);

        Cursor c = null;

        c = queryBuilder.query(database, sqlSelect, selection, null, null, null, null, null, null);

        c.moveToFirst();

        return c.getString(0);

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

    public List<String> getParentsEggMoveNames(int pokemonId, int moveId) {
        SQLiteDatabase database= getReadableDatabase();


        String initialPokemonEggGroups =
                "SELECT egg_group_id FROM pokemon_egg_groups WHERE species_id="+Integer.toString(pokemonId);
        String pokemonsWithSameEggGroup =
                "SELECT DISTINCT species_id FROM pokemon_egg_groups WHERE egg_group_id in("+initialPokemonEggGroups + ")";
        String pokemonsWithMove =
                "SELECT pokemon_id FROM pokemon_moves WHERE move_id="+Integer.toString(moveId)+" AND version_group_id=15 AND pokemon_id<>"+Integer.toString(pokemonId);

        String pokemonsWithMoveFromSameEggGroup =
                pokemonsWithSameEggGroup+" INTERSECT "+pokemonsWithMove;
        String pokemonNames =
                "SELECT name FROM pokemon_species_names WHERE local_language_id=9 AND pokemon_species_id in("+pokemonsWithMoveFromSameEggGroup+")";


        Cursor c = database.rawQuery(pokemonNames,null);

        c.moveToFirst();


        List<String> list = new ArrayList<>();
        while(!c.isAfterLast()) {
            list.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        return list;


    }

}
