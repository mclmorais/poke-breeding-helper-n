package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcelo on 12/12/2015.
 */
public class MyDatabase extends SQLiteAssetHelper  {

    private static final String DATABASE_NAME = "pkmnsql.db";
    private static final int DATABASE_VERSION = 1;

    public MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getReadableDatabase();

    }

    SQLiteDatabase database;



    public List<MoveInfo> getPokemonMovesInfo(int pokemonId, int pokemonVersionId, int methodId, int languageId) {

        //move, level
        Cursor pokemonMovesCursor = getPokemonMoves(pokemonId, pokemonVersionId, methodId);
        List<MoveInfo> moveInfoList = new ArrayList<>(pokemonMovesCursor.getCount());

        for (int move = 0; move < pokemonMovesCursor.getCount(); move++) {

            MoveInfoBuilder moveInfoBuilder = new MoveInfoBuilder();

            if(methodId == 1) //If its from leveling up
                moveInfoBuilder.setLevel(pokemonMovesCursor.getInt(1));
            else if (methodId == 4) { //If its from a machine
                Cursor moveMachineNumberCursor = getMoveMachineNumberCursor(
                        pokemonMovesCursor.getInt(0),pokemonVersionId);
                int machineNumber = moveMachineNumberCursor.getInt(0);
                moveInfoBuilder.setMachineNumber(machineNumber);
                if(machineNumber > 100)
                    moveInfoBuilder.isHiddenMachine(true);
                moveMachineNumberCursor.close();
            }
            else if (methodId == 2){
                Cursor parentIdCursor = getEggMoveParentsId(pokemonId,pokemonMovesCursor.getInt(0));

                ArrayList<Integer> parentIds = new ArrayList<>();
                for (int i = 0; i < parentIdCursor.getCount(); i++) {
                    parentIds.add(parentIdCursor.getInt(0));
                    parentIdCursor.moveToNext();
                }
                moveInfoBuilder.setParents(parentIds);
                parentIdCursor.close();
                //get parent info here
            }

            String s = getMoveName(pokemonMovesCursor.getInt(0), pokemonVersionId, languageId);
            moveInfoBuilder.setName(s);

            //type, power, acc, class
            Cursor moveInfoCursor = getMoveInfo(pokemonMovesCursor.getInt(0));
            moveInfoBuilder.setTypeId(moveInfoCursor.getInt(0));
            moveInfoBuilder.setPower(moveInfoCursor.getInt(1));
            moveInfoBuilder.setAccuracy(moveInfoCursor.getInt(2));

            //typename
            Cursor typeNameCursor = getTypeName(moveInfoCursor.getInt(0), languageId);
            moveInfoBuilder.setType(typeNameCursor.getString(0));

            //classname
            Cursor classNameCursor = getClassName(moveInfoCursor.getInt(3), languageId);
            moveInfoBuilder.setMoveClass(classNameCursor.getString(0));

            moveInfoList.add(moveInfoBuilder.createMoveInfo());



            pokemonMovesCursor.moveToNext();
            moveInfoCursor.close();
            typeNameCursor.close();
            classNameCursor.close();
        }
        pokemonMovesCursor.close();


    return moveInfoList;


    }

    private Cursor getMoveMachineNumberCursor(int moveId, int pokemonVersionId) {
        database= getReadableDatabase();
        String s = "SELECT machine_number FROM machines WHERE version_group_id="
                + Integer.toString(pokemonVersionId)
                + " AND move_id="
                + Integer.toString(moveId);

        Cursor c = database.rawQuery(s,null);
        c.moveToFirst();
        return c;
    }

    private Cursor getPokemonMoves(int pokemonId, int pokemonVersionId, int methodId) {
        database= getReadableDatabase();
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

        Cursor c = queryBuilder.query(database, sqlSelect, selection, null, null, null, "level", null, null);
        c.moveToFirst();

        return c;
    }

    private Cursor getClassName(int classId, int languageId) {
        database= getReadableDatabase();


        String s = "SELECT name FROM move_damage_class_prose WHERE move_damage_class_id="
                + Integer.toString(classId)
                + " AND local_language_id="
                + Integer.toString(languageId);



        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        return c;
    }

    private Cursor getTypeName(int typeId, int languageId) {
        database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();


        String s = "SELECT name FROM type_names WHERE type_id="
                + Integer.toString(typeId)
                + " AND local_language_id="
                + Integer.toString(languageId);



        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        return c;
    }

    private String getMoveName(int id, int pokemonVersionId, int languageId) {
        database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"name"};
        String sqlTables = "move_names";
        String selection = "local_language_id="+Integer.toString(languageId) + " and move_id="+Integer.toString(id);

        queryBuilder.setTables(sqlTables);

        Cursor c = null;

        c = queryBuilder.query(database, sqlSelect, selection, null, null, null, null, null, null);

        c.moveToFirst();
        String s = c.getString(0);
        c.close();
        return s;

    }

    private Cursor getMoveInfo(int moveId) {
        database= getReadableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String [] sqlSelect = {"type_id", "power", "accuracy", "damage_class_id"};
        String sqlTables = "moves";
        String selection = "id="+ Integer.toString(moveId);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection, null, null, null, null, null, null);
        c.moveToFirst();
        return c;

    }

    private Cursor getEggMoveParentsId(int pokemonId, int moveId) {
        database= getReadableDatabase();


        String initialPokemonEggGroups =
                "SELECT egg_group_id FROM pokemon_egg_groups WHERE species_id="+Integer.toString(pokemonId);
        String pokemonsWithSameEggGroup =
                "SELECT DISTINCT species_id FROM pokemon_egg_groups WHERE egg_group_id in("+initialPokemonEggGroups + ")";
        String pokemonsWithMove =
                "SELECT pokemon_id FROM pokemon_moves WHERE move_id="+Integer.toString(moveId)+" AND version_group_id=15 AND pokemon_id<>"+Integer.toString(pokemonId)
                + " AND pokemon_move_method_id=1";

        String pokemonsWithMoveFromSameEggGroup =
                pokemonsWithSameEggGroup+" INTERSECT "+pokemonsWithMove;

        Cursor c = database.rawQuery(pokemonsWithMoveFromSameEggGroup, null);

        if(c.getCount() == 0) {
             pokemonsWithMove =
              "SELECT pokemon_id FROM pokemon_moves WHERE move_id="+Integer.toString(moveId)+" AND version_group_id=15 AND pokemon_id<>"+Integer.toString(pokemonId)
                      + " AND pokemon_move_method_id=2";

             pokemonsWithMoveFromSameEggGroup =
                    pokemonsWithSameEggGroup+" INTERSECT "+pokemonsWithMove;
            c = database.rawQuery(pokemonsWithMoveFromSameEggGroup, null);
        }

        c.moveToFirst();
        return c;

    }

    private List<String> getParentsEggMoveNames(int pokemonId, int moveId) {
        database= getReadableDatabase();


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
        database= getReadableDatabase();
        String s = "SELECT name FROM pokemon_species_names where local_language_id=9 and pokemon_species_id="+Integer.toString(pokemonId);

        Cursor c = database.rawQuery(s,null);
        c.moveToFirst();
        return c.getString(0);
    }

}
