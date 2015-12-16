package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.SparseArray;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcelo on 12/12/2015.
 */
public class MyDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "pkmnsql.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

//    public Cursor getPokemonMoves(int pokemonId, int pokemonVersion) {
//        SQLiteDatabase database= getReadableDatabase();
//        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
//
//        String [] sqlSelect = {"move_id", "pokemon_move_method_id", "level"};
//        String sqlTables = "pokemon_moves";
//        String selection = "pokemon_id=" + Integer.toString(pokemonId)+ " and version_group_id=" + Integer.toString(pokemonVersion);
//
//        queryBuilder.setTables(sqlTables);
//
//        Cursor c = queryBuilder.query(database, sqlSelect, selection,null,null,null,null,null,null);
//        c.moveToFirst();
//        return c;
//    }

    public List<MoveInfo> getPokemonMovesInfo(int pokemonId, int pokemonVersionId, int methodId, int languageId) {



        //SQLiteDatabase database= getReadableDatabase();

        //move, level
        Cursor pokemonMovesCursor = getPokemonMoves(pokemonId, pokemonVersionId, methodId);
        List<MoveInfo> moveInfoList = new ArrayList<>(pokemonMovesCursor.getCount());
        pokemonMovesCursor.moveToFirst();

        for (int move = 0; move < pokemonMovesCursor.getCount(); move++) {


            String s = getMoveName(pokemonMovesCursor.getInt(0), pokemonVersionId, languageId);

            //type, power, acc, class
            Cursor moveInfoCursor = getMoveInfo(pokemonMovesCursor.getInt(0));
            moveInfoCursor.moveToFirst();

            //typename
            Cursor typeNameCursor = getTypeName(moveInfoCursor.getInt(0), languageId);
            typeNameCursor.moveToFirst();

            //classname
            Cursor classNameCursor = getClassName(moveInfoCursor.getInt(3), languageId);

            moveInfoList.add(new MoveInfo(
                    pokemonMovesCursor.getInt(1),
                    moveInfoCursor.getInt(2),
                    moveInfoCursor.getInt(1),
                    s,
                    classNameCursor.getString(0),
                    typeNameCursor.getString(0),
                    moveInfoCursor.getInt(0))
            );

            pokemonMovesCursor.moveToNext();
        }

    return moveInfoList;


    }

    public List<Integer> getPokemonMovesId2(int pokemonId, int pokemonVersionId, int methodId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"move_id"};
        String sqlTables = "pokemon_moves";
        String selection = "pokemon_id="
                + Integer.toString(pokemonId)
                + " and version_group_id="
                + Integer.toString(pokemonVersionId)
                + " and pokemon_move_method_id="
                + Integer.toString(methodId);


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

    public Cursor getPokemonMoves(int pokemonId, int pokemonVersionId, int methodId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"move_id","level"};
        String sqlTables = "pokemon_moves";
        String selection = "pokemon_id="
                + Integer.toString(pokemonId)
                + " and version_group_id="
                + Integer.toString(pokemonVersionId)
                + " and pokemon_move_method_id="
                + Integer.toString(methodId);


        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection,null,null,null,"level",null,null);
        c.moveToFirst();

        return c;
    }

    public Cursor getClassName(int classId, int languageId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        String s = "SELECT name FROM move_damage_class_prose WHERE move_damage_class_id="
                + Integer.toString(classId)
                + " AND local_language_id="
                + Integer.toString(languageId);



        Cursor c = database.rawQuery(s,null);
        c.moveToFirst();
        return c;
    }

    public Cursor getTypeName(int typeId, int languageId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        String s = "SELECT name FROM type_names WHERE type_id="
                + Integer.toString(typeId)
                + " AND local_language_id="
                + Integer.toString(languageId);



        Cursor c = database.rawQuery(s,null);
        c.moveToFirst();
        return c;
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

    public Cursor getMoveInfo(int moveId) {
        SQLiteDatabase database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"type_id", "power", "accuracy", "damage_class_id"};
        String sqlTables = "moves";
        String selection = "id="+ Integer.toString(moveId);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection, null, null, null, null, null, null);
        c.moveToFirst();
        return c;

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


        Cursor c = database.rawQuery(pokemonNames, null);

        c.moveToFirst();


        List<String> list = new ArrayList<>();
        while(!c.isAfterLast()) {
            list.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        return list;
    }

    public String getPokemonName(int pokemonId) {
        SQLiteDatabase database= getReadableDatabase();
        String s = "SELECT name FROM pokemon_species_names where local_language_id=9 and pokemon_species_id="+Integer.toString(pokemonId);

        Cursor c = database.rawQuery(s,null);
        c.moveToFirst();
        return c.getString(0);
    }

}
