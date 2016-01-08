package databasemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import marcelo.breguenait.breedinghelper.MoveInfo;

public class MyDatabase extends SQLiteAssetHelper {

    private final String DEBUG_DATABASE = "SQL_DB";

    private static final String DATABASE_NAME = "pkmnsql.db";
    private static final int DATABASE_VERSION = 1;
    private static MyDatabase instance;
    SQLiteDatabase database;

    private MyDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        database = getReadableDatabase();

    }

    public static void initialize(Context c) {

        instance = new MyDatabase(c);
    }

    public static MyDatabase getInstance() {
        return instance;

    }

    private Cursor getFirstEvolutionMoves(Cursor movesCursor, int pokemonId, int pokemonVersionId, int methodId) {
        if (movesCursor.getCount() == 0) { //If current pokemon doesnt have egg moves
            Cursor preEvolutionCursor = getPreEvolutionId(pokemonId); //gets its pre evolution

            if (preEvolutionCursor.isNull(0)) { //If it doesn't have a preevolution
                //well tough shit, this is what you get
                Cursor newMovesCursor = getPokemonMoves(preEvolutionCursor.getInt(0), pokemonVersionId, methodId);
                preEvolutionCursor.close();
                return newMovesCursor;
            } else { //if it has a preevolution
                int preEvolutionId = preEvolutionCursor.getInt(0);
                Cursor preEvolutionMovesCursor = getPokemonMoves(preEvolutionId, pokemonVersionId, methodId);
                if (preEvolutionMovesCursor.getCount() == 0) {//if the preevolution also doesnt have egg moves
                    preEvolutionCursor.close();
                    return getFirstEvolutionMoves(preEvolutionMovesCursor, preEvolutionId, pokemonVersionId, methodId);
                } else //if it has moves
                    preEvolutionCursor.close();
                return preEvolutionMovesCursor;

            }
        }
        return movesCursor; //if it somehow got here shit got fucked
    }

    private Cursor getPreEvolutionId(int pokemonId) {
        String s = "SELECT evolves_from_species_id FROM pokemon_species WHERE id=" +
                Integer.toString(pokemonId);

        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        return c;
    }

    private Cursor getMoveMachineNumberCursor(int moveId, int pokemonVersionId) {
        String s = "SELECT machine_number FROM machines WHERE version_group_id="
                + Integer.toString(pokemonVersionId)
                + " AND move_id="
                + Integer.toString(moveId);

        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        return c;
    }

    private Cursor getPokemonMoves(int pokemonId, int pokemonVersionId, int methodId) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"move_id", "level"};
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

        String s = "SELECT name FROM move_damage_class_prose WHERE move_damage_class_id="
                + Integer.toString(classId)
                + " AND local_language_id="
                + Integer.toString(languageId);


        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        return c;
    }

    private Cursor getTypeName(int typeId, int languageId) {


        String s = "SELECT name FROM type_names WHERE type_id="
                + Integer.toString(typeId)
                + " AND local_language_id="
                + Integer.toString(languageId);


        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        return c;
    }

    private String getMoveName(int id, int languageId) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"name"};
        String sqlTables = "move_names";
        String selection = "local_language_id=" + Integer.toString(languageId) + " and move_id=" + Integer.toString(id);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection, null, null, null, null, null, null);

        c.moveToFirst();
        String s = c.getString(0);
        c.close();
        return s;

    }

    private Cursor getMoveInfo(int moveId) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        String[] sqlSelect = {"type_id", "power", "accuracy", "damage_class_id"};
        String sqlTables = "moves";
        String selection = "id=" + Integer.toString(moveId);

        queryBuilder.setTables(sqlTables);

        Cursor c = queryBuilder.query(database, sqlSelect, selection, null, null, null, null, null, null);
        c.moveToFirst();
        return c;

    }

    private Cursor getEggMoveParentsId(int pokemonId, int moveId) {

        Cursor c = getEggMoveParentsIdLevel(pokemonId, moveId);
        if (c.getCount() == 0) { //If no compatible pokemon learn the move through level up, it means they also learn it through breeding
            c = getEggMoveParentsIdChained(pokemonId, moveId);

        }

        c.moveToFirst();
        return c;

    }

    private Cursor getEggMoveParentsIdLevel(int pokemonId, int moveId) {
        String initialPokemonEggGroups =
                "SELECT egg_group_id FROM pokemon_egg_groups WHERE species_id=" + Integer.toString(pokemonId);
        String pokemonsWithSameEggGroup =
                "SELECT DISTINCT species_id FROM pokemon_egg_groups WHERE egg_group_id in(" + initialPokemonEggGroups + ")";
        String pokemonsWithMove =
                "SELECT pokemon_id FROM pokemon_moves WHERE move_id=" + Integer.toString(moveId) + " AND version_group_id=15 AND pokemon_id<>" + Integer.toString(pokemonId)
                        + " AND pokemon_move_method_id=1";

        String pokemonsWithMoveFromSameEggGroup =
                pokemonsWithSameEggGroup + " INTERSECT " + pokemonsWithMove;

        return database.rawQuery(pokemonsWithMoveFromSameEggGroup, null); //poceymans that learn through level

    }

    private Cursor getEggMoveParentsIdChained(int pokemonId, int moveId) {
        String initialPokemonEggGroups =
                "SELECT egg_group_id FROM pokemon_egg_groups WHERE species_id=" + Integer.toString(pokemonId);
        String pokemonsWithSameEggGroup =
                "SELECT DISTINCT species_id FROM pokemon_egg_groups WHERE egg_group_id in(" + initialPokemonEggGroups + ")";
        String pokemonsWithMove =
                "SELECT pokemon_id FROM pokemon_moves WHERE move_id=" + Integer.toString(moveId) + " AND version_group_id=15 AND pokemon_id<>" + Integer.toString(pokemonId)
                        + " AND pokemon_move_method_id=2";

        String pokemonsWithMoveFromSameEggGroup =
                pokemonsWithSameEggGroup + " INTERSECT " + pokemonsWithMove;

        return database.rawQuery(pokemonsWithMoveFromSameEggGroup, null); //poceymans that learn through egg

    }

    @SuppressWarnings("unused")
    private List<String> getParentsEggMoveNames(int pokemonId, int moveId) {

        String initialPokemonEggGroups =
                "SELECT egg_group_id FROM pokemon_egg_groups WHERE species_id=" + Integer.toString(pokemonId);
        String pokemonsWithSameEggGroup =
                "SELECT DISTINCT species_id FROM pokemon_egg_groups WHERE egg_group_id in(" + initialPokemonEggGroups + ")";
        String pokemonsWithMove =
                "SELECT pokemon_id FROM pokemon_moves WHERE move_id=" + Integer.toString(moveId) + " AND version_group_id=15 AND pokemon_id<>" + Integer.toString(pokemonId);

        String pokemonsWithMoveFromSameEggGroup =
                pokemonsWithSameEggGroup + " INTERSECT " + pokemonsWithMove;
        String pokemonNames =
                "SELECT name FROM pokemon_species_names WHERE local_language_id=9 AND pokemon_species_id in(" + pokemonsWithMoveFromSameEggGroup + ")";


        Cursor c = database.rawQuery(pokemonNames, null);

        c.moveToFirst();


        List<String> list = new ArrayList<>();
        while (!c.isAfterLast()) {
            list.add(c.getString(0));
            c.moveToNext();
        }
        c.close();

        return list;
    }

    /**
     * Gets a Pokémon name string from the database.
     * @param pokemonId ID of the Pokémon to be queried
     * @param languageId Language ID of the game to be queried
     * @return
     */
    public String getPokemonName(int pokemonId, int languageId) {
        String s = "SELECT " +
                "name " +
                "FROM " +
                "pokemon_species_names " +
                "WHERE " +
                "local_language_id=" +
                Integer.toString(languageId) +
                " AND " +
                "pokemon_species_id=" +
                Integer.toString(pokemonId);

        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        String name;
        if (c.getCount() > 0)
            name = c.getString(0);
        else
            name = "";

        c.close();
        return name;
    }

    /**
     * Queries the database for a list of Natures' IDs and names.
     * @param languageId Language ID of the game to be queried.
     * @return Returns a map of Nature names linked to their IDs. Returns an empty list if
     * the query comes back empty.
     */
    public LinkedHashMap<Integer, String> getNatureNames(int languageId) {
        String s = "SELECT " +
                "name, nature_id " +
                "FROM " +
                "nature_names " +
                "WHERE " +
                "local_language_id="
                + String.valueOf(languageId);

        Cursor cursorNatureNames = database.rawQuery(s, null);
        LinkedHashMap<Integer, String> namesList = new LinkedHashMap<>(cursorNatureNames.getCount());

        if(cursorNatureNames.getCount() > 0) {
            cursorNatureNames.moveToFirst();
            while (!cursorNatureNames.isAfterLast()) {
                namesList.put(cursorNatureNames.getInt(1), cursorNatureNames.getString(0));
                cursorNatureNames.moveToNext();
            }
        }
        cursorNatureNames.close();

        return namesList;
    }

    /**
     * Queries natures IDs in a custom order sorted by the increased stat. Hardy is put at the
     * beginning and the other neutral natures are put at the end.
     * @return A list of the sorted IDs. Returns an empty list if the query didn't return anything.
     */
    public ArrayList<Integer> getNatureIdsSortedByIncreasedStat() {
        String s = "SELECT id FROM natures WHERE decreased_stat_id<>increased_stat_id ORDER BY increased_stat_id";

        Cursor c = database.rawQuery(s, null);

        ArrayList<Integer> natureIds = new ArrayList<>(c.getCount());

        if(c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                natureIds.add(c.getInt(0));
                c.moveToNext();
            }

        }
        c.close();

        boolean firstAtTop = true;
        s = "SELECT id FROM natures WHERE decreased_stat_id==increased_stat_id ORDER BY increased_stat_id";
        c = database.rawQuery(s, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                if (firstAtTop) {
                    natureIds.add(0, c.getInt(0));
                    firstAtTop = false;
                } else {
                    natureIds.add(c.getInt(0));
                }
                c.moveToNext();
            }
        }
        c.close();

        return natureIds;
    }


    /**
     * Queries the database for the names of the abilities that a certain Pokémon can have.
     * @param pokemonId The ID of the Pokémon to be queried.
     * @param languageId Language ID of the game to be queried.
     * @return A map of the abilities' name and its respective slot. Returns an empty map if the
     * query failed.
     */
    public LinkedHashMap<Integer, String> getListOfAbilitiesNames(int pokemonId, int languageId) {

        String s = "SELECT ability_id, slot FROM pokemon_abilities WHERE pokemon_id="
                + Integer.toString(pokemonId);

        //ABILITYID, SLOT
        Cursor cursorAbilitiesIdSlot = database.rawQuery(s, null);
        LinkedHashMap<Integer, String> abilities = new LinkedHashMap<>(cursorAbilitiesIdSlot.getCount());

        if(cursorAbilitiesIdSlot.getCount() > 0) {
            cursorAbilitiesIdSlot.moveToFirst();

            while (!cursorAbilitiesIdSlot.isAfterLast()) {
                s = "SELECT name FROM ability_names WHERE local_language_id=" +
                        Integer.toString(languageId) +
                        " and ability_id=" +
                        cursorAbilitiesIdSlot.getInt(0);

                //NAME
                Cursor cursorAbilityNames = database.rawQuery(s, null);
                if(cursorAbilityNames.getCount() > 0) {
                    cursorAbilityNames.moveToFirst();
                    abilities.put(cursorAbilitiesIdSlot.getInt(1), cursorAbilityNames.getString(0));
                }
                cursorAbilityNames.close();
                cursorAbilitiesIdSlot.moveToNext();
            }
        }

        cursorAbilitiesIdSlot.close();

        return abilities;
    }

    public List<MoveInfo> getPokemonMovesInfo(int pokemonId, int pokemonVersionId, int methodId, int languageId) {

        //move, level
        Cursor pokemonMovesCursor = getPokemonMoves(pokemonId, pokemonVersionId, methodId);
        List<MoveInfo> moveInfoList = new ArrayList<>(pokemonMovesCursor.getCount());

        if (methodId == 2 && pokemonMovesCursor.getCount() == 0) {
            pokemonMovesCursor.close();
            pokemonMovesCursor = getFirstEvolutionMoves(pokemonMovesCursor, pokemonId, pokemonVersionId, methodId);
        }


        for (int move = 0; move < pokemonMovesCursor.getCount(); move++) {

            MoveInfo.MoveInfoBuilder moveInfoBuilder = new MoveInfo.MoveInfoBuilder();

            if (methodId == 1) //If its from leveling up
                moveInfoBuilder.setLevel(pokemonMovesCursor.getInt(1));
            else if (methodId == 4) { //If its from a machine
                Cursor moveMachineNumberCursor = getMoveMachineNumberCursor(
                        pokemonMovesCursor.getInt(0), pokemonVersionId);
                int machineNumber = moveMachineNumberCursor.getInt(0);
                moveInfoBuilder.setMachineNumber(machineNumber);
                if (machineNumber > 100)
                    moveInfoBuilder.isHiddenMachine();
                moveMachineNumberCursor.close();
            } else if (methodId == 2) {
                Cursor parentIdCursor = getEggMoveParentsId(pokemonId, pokemonMovesCursor.getInt(0));

                ArrayList<Integer> parentIds = new ArrayList<>();
                for (int i = 0; i < parentIdCursor.getCount(); i++) {
                    parentIds.add(parentIdCursor.getInt(0));
                    parentIdCursor.moveToNext();
                }
                moveInfoBuilder.setParents(parentIds);
                parentIdCursor.close();

            }

            String s = getMoveName(pokemonMovesCursor.getInt(0), languageId);
            moveInfoBuilder.setName(s);

            //type, power, acc, class
            Cursor moveInfoCursor = getMoveInfo(pokemonMovesCursor.getInt(0));
            moveInfoBuilder.setTypeId(moveInfoCursor.getInt(0));
            moveInfoBuilder.setPower(moveInfoCursor.getInt(1));
            moveInfoBuilder.setAccuracy(moveInfoCursor.getInt(2));

            //typename
            Cursor typeNameCursor = getTypeName(moveInfoCursor.getInt(0), languageId);
            if (typeNameCursor.getCount() > 0)
                moveInfoBuilder.setType(typeNameCursor.getString(0));
            else
                moveInfoBuilder.setType("");

            //classname
            Cursor classNameCursor = getClassName(moveInfoCursor.getInt(3), languageId);
            if (classNameCursor.getCount() > 0)
                moveInfoBuilder.setMoveClass(classNameCursor.getString(0));
            else
                moveInfoBuilder.setMoveClass("");

            moveInfoList.add(moveInfoBuilder.createMoveInfo());


            pokemonMovesCursor.moveToNext();
            moveInfoCursor.close();
            typeNameCursor.close();
            classNameCursor.close();
        }
        pokemonMovesCursor.close();


        return moveInfoList;


    }

    public ArrayList<Integer> getPokemonEggGroupIds(int pokemonId) {

        String s = "SELECT " +
                "egg_group_id " +
                "FROM " +
                "pokemon_egg_groups " +
                "WHERE " +
                "species_id=" +
                Integer.toString(pokemonId);

        Cursor cursorEggGroupIds = database.rawQuery(s, null);
        cursorEggGroupIds.moveToFirst();

        ArrayList<Integer> eggGroups = new ArrayList<>(cursorEggGroupIds.getCount());

        while (!cursorEggGroupIds.isAfterLast()) {
            eggGroups.add(cursorEggGroupIds.getInt(0));
            cursorEggGroupIds.moveToNext();
        }
        cursorEggGroupIds.close();

        return eggGroups;
    }

    /**
     * Queries the evolution chain of a certain Pokémon.
     * @param pokemonId The ID of the Pokémon to be queried.
     * @return The evolution chain ID. Returns -1 if the query failed.
     */
    public int getEvolutionChainId(int pokemonId) {
        String s = "SELECT " +
                "evolution_chain_id " +
                "FROM " +
                "pokemon_species " +
                "WHERE" +
                " id=" +
                Integer.toString(pokemonId);

        int evolutionChainId = -1;
        Cursor cursorEvolutionChainId = database.rawQuery(s, null);
        if(cursorEvolutionChainId.getCount() > 0) {
            cursorEvolutionChainId.moveToFirst();
            evolutionChainId = cursorEvolutionChainId.getInt(0);
        }
        cursorEvolutionChainId.close();
        return evolutionChainId;
    }

    /**
     * Queries the gender rate identifier of a certain Pokémon. {@link DatabaseConstants} has values that
     * illustrate what the returned values mean.
     * @param pokemonId The ID of the Pokémon to be queried.
     * @return The gender rate of the queried Pokémon. Returns -2 if the query failed (due to the
     * fact that the value -1 is valid in this case).
     */
    public int getGenderRate(int pokemonId) {
        String s = "SELECT " +
                "gender_rate " +
                "FROM " +
                "pokemon_species " +
                "WHERE id=" +
                Integer.toString(pokemonId);

        int genderRate = -2;

        Cursor cursorGenderRate = database.rawQuery(s, null);
        if(cursorGenderRate.getCount() > 0) {
            cursorGenderRate.moveToFirst();
            genderRate = cursorGenderRate.getInt(0);
        }
        cursorGenderRate.close();
        return genderRate;
    }

    /**
     * Queries a list of all the Pokémon in the same Egg Group of the queried Pokémon if it's
     * not genderless; otherwise, queries the family of the received genderless pokémon. Both
     * queries also return ditto.
     * @param pokemonId The ID of the Pokémon to be queried.
     * @return A list of compatible pokémon IDs. Returns an empty list if the query failed.
     */
    public ArrayList<Integer> getCompatiblePokemonList(int pokemonId) {

        int genderRate = getGenderRate(pokemonId);

        String s;
        if (genderRate == -1) {
            //Family and ditto
            s = "SELECT " +
                    "id " +
                    "FROM " +
                    "pokemon_species " +
                    "WHERE " +
                    "evolution_chain_id " +
                    "IN(" +
                    "SELECT " +
                    "evolution_chain_id " +
                    "FROM " +
                    "pokemon_species " +
                    "WHERE " +
                    "id=" +
                    Integer.toString(pokemonId) +
                    ") OR " +
                    "evolution_chain_id=66"; //Ditto
        } else {
            //EggGroup and ditto
            s = "SELECT DISTINCT " +
                    "species_id " +
                    "FROM " +
                    "pokemon_egg_groups " +
                    "WHERE " +
                    "egg_group_id " +
                    "in(" +
                    "SELECT " +
                    "egg_group_id " +
                    "FROM " +
                    "pokemon_egg_groups " +
                    "WHERE " +
                    "species_id=" +
                    Integer.toString(pokemonId) +
                    ") OR " +
                    "egg_group_id=13"; //Ditto

        }

        Cursor cursorCompatible = database.rawQuery(s, null);
        ArrayList<Integer> compatiblePokemonList = new ArrayList<>(cursorCompatible.getCount());

        if(cursorCompatible.getCount() > 0) {
            cursorCompatible.moveToFirst();

            while (!cursorCompatible.isAfterLast()) {
                compatiblePokemonList.add(cursorCompatible.getInt(0));
                cursorCompatible.moveToNext();
            }
        }
        cursorCompatible.close();
        return compatiblePokemonList;
    }

    /**
     * Queries the IDs of Pokémon in the same family as the queried one.
     * @param pokemonId The ID of the Pokémon to be queried. Special cases (Nidorans and
     *                  Volbeat/Illumise) are also considered.
     * @return A list of Pokémon in the same family. Returns an empty list if the query failed.
     */
    public ArrayList<Integer> getPokemonFamilyList(int pokemonId) {

        String s = "SELECT " +
                "id " +
                "FROM " +
                "pokemon_species " +
                "WHERE " +
                "evolution_chain_id " +
                "IN(" +
                "SELECT " +
                "evolution_chain_id " +
                "FROM " +
                "pokemon_species " +
                "WHERE " +
                "id=" +
                Integer.toString(pokemonId) +
                ")";


        Cursor cursorCompatible = database.rawQuery(s, null);
        ArrayList<Integer> compatiblePokemonList = new ArrayList<>(cursorCompatible.getCount());

        if(cursorCompatible.getCount() > 0) {
            cursorCompatible.moveToFirst();
            while (!cursorCompatible.isAfterLast()) {
                compatiblePokemonList.add(cursorCompatible.getInt(0));
                cursorCompatible.moveToNext();
            }
        }
        cursorCompatible.close();

        //Hacks for the exceptions. Maybe add another table on the db to solve this?
        if (pokemonId == 29) {
            compatiblePokemonList.add(32);
            compatiblePokemonList.add(33);
            compatiblePokemonList.add(34);
        }
        if (pokemonId == 32 || pokemonId == 33 || pokemonId == 34) {
            compatiblePokemonList.add(29);
        }
        if (pokemonId == 313) {
            compatiblePokemonList.add(314);
        }
        if (pokemonId == 314) {
            compatiblePokemonList.add(313);
        }

        return compatiblePokemonList;
    }

    /**
     * Queries all Pokémon that:
     * * Are the first form in an evolution chain (unless the first form is a baby)
     * * Are not babies
     * * Can breed
     * @return A list of basic Pokémon IDs. Returns an empty list if the query failed.
     */
    public ArrayList<Integer> getBasicPokemonList() {
        String s = "SELECT id FROM pokemon_species WHERE evolves_from_species_id IS NULL AND is_baby=0 AND id<>132" +
                " UNION " +
                "SELECT id FROM pokemon_species WHERE evolves_from_species_id IN(SELECT id FROM pokemon_species where is_baby=1)" +
                " EXCEPT " +
                "SELECT species_id FROM pokemon_egg_groups WHERE egg_group_id=15";

        Cursor cursorBasicPokemon = database.rawQuery(s, null);
        ArrayList<Integer> pokemonList = new ArrayList<>(cursorBasicPokemon.getCount());
        if(cursorBasicPokemon.getCount() > 0) {
            cursorBasicPokemon.moveToFirst();
            while (!cursorBasicPokemon.isAfterLast()) {
                pokemonList.add(cursorBasicPokemon.getInt(0));
                cursorBasicPokemon.moveToNext();
            }
        }
        cursorBasicPokemon.close();

        return pokemonList;
    }

    /**
     * Queries the name of a nature given its ID and a game language.
     * @param natureId ID of the nature to be queried
     * @param languageId ID of the game language to be queried
     * @return A String contained the desired name. Returns an empty string ("") if the
     * query failed.
     */
    public String getNatureName(int natureId, int languageId) {
        String s = "SELECT " +
                "name " +
                "FROM " +
                "nature_names " +
                "WHERE " +
                "local_language_id=" +
                Integer.toString(languageId) +
                " AND " +
                "nature_id=" +
                Integer.toString(natureId);

        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        String name;
        if (c.getCount() > 0)
            name = c.getString(0);
        else {
            name = "";
            Log.d(DEBUG_DATABASE, "Couldn't find nature with id="
                    + String.valueOf(natureId)
                    + " and language_id=" + String.valueOf(languageId));
        }
        c.close();
        return name;
    }

    /**
     * Queries an ability name given it's slot on the queried Pokémon and game language.
     * @param pokemonId ID of the Pokémon to be queried.
     * @param abilitySlot Slot of the ability on the queried Pokémon.
     * @param languageId ID of the game language.
     * @return A string of the desired name. Returns an empty string if the query failed.
     */
    public String getAbilityName(int pokemonId, int abilitySlot, int languageId) {
        String s = "SELECT " +
                "name " +
                "FROM " +
                "ability_names " +
                "WHERE " +
                "local_language_id=" +
                Integer.toString(languageId) +
                " AND " +
                "ability_id " +
                "IN(" +
                "SELECT " +
                "ability_id " +
                "FROM " +
                "pokemon_abilities " +
                "WHERE " +
                "pokemon_id=" +
                Integer.toString(pokemonId) +
                " AND " +
                "slot=" +
                Integer.toString(abilitySlot) +
                ")";


        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        String name;
        if (c.getCount() > 0)
            name = c.getString(0);
        else {
            name = "";
            Log.d(DEBUG_DATABASE, "NAME OF THE ABILITY WAS NOT FOUND!!!!!");
        }

        c.close();
        return name;
    }

    /**
     * Queries the slot of an ability in a certain Pokémon.
     * @param pokemonId ID of the Pokémon to be queried.
     * @param abilityId ID of the Pokémon's ability.
     * @return The slot of the desired ability. Returns -1 if the query failed.
     */
    public int getAbilitySlot(int pokemonId, int abilityId) {
        String s = "SELECT " +
                "slot " +
                "FROM " +
                "pokemon_abilities " +
                "WHERE " +
                "pokemon_id=" +
                String.valueOf(pokemonId) +
                " AND " +
                "ability_id=" +
                String.valueOf(abilityId);
        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        int slot;

        if (c.getCount() > 0)
            slot = c.getInt(0);
        else
            slot = -1;

        c.close();
        return slot;

    }

    /**
     * Queries the list of all possible Pokémon IDs.
     * @return A list of all the IDs. Returns an empty list if the query failed.
     */
    public ArrayList<Integer> getPokemonIds() {

        String s = "SELECT id FROM pokemon_species ORDER BY id ASC";

        Cursor cursorIds = database.rawQuery(s, null);

        ArrayList<Integer> ids = new ArrayList<>(cursorIds.getCount());

        if(cursorIds.getCount() > 0) {
            cursorIds.moveToFirst();
            while (!cursorIds.isAfterLast()) {

                ids.add(cursorIds.getInt(0));

                cursorIds.moveToNext();
            }
        }

        cursorIds.close();

        return ids;

    }

    /**
     * Queries a list of all Pokémon names in a given language. The order of the names is the same
     * as the one given by {@link #getPokemonIds()}.
     * @param languageId ID of the game language to be queried.
     * @return A list of desired names. Returns an empty list if the query failed.
     */
    public ArrayList<String> getPokemonNames(int languageId) {

        String s = "SELECT " +
                "name " +
                "FROM " +
                "pokemon_species_names " +
                "WHERE " +
                "local_language_id=" +
                Integer.toString(languageId);

        Cursor cursorIds = database.rawQuery(s, null);

        ArrayList<String> names = new ArrayList<>(cursorIds.getCount());

        if(cursorIds.getCount() > 0) {
            cursorIds.moveToFirst();
            while (!cursorIds.isAfterLast()) {
                names.add(cursorIds.getString(0));
                cursorIds.moveToNext();
            }
        }

        cursorIds.close();

        return names;

    }

    /**
     * Queries the name of the stat that the queried nature changes in a certain language.
     * @param natureId The ID of the nature to be queried.
     * @param languageId The ID of the game language.
     * @param increased If it should return the increased (true) or the decreased (false) value.
     * @return A string containing the name of the affected stat. Returns an empty string ("")
     * if the query fails.
     */
    public String getNatureChangedStatName(int natureId, int languageId, boolean increased) {

        String whichStat = increased ? "increased_stat_id " : "decreased_stat_id ";

        String s = "SELECT " +
                "name " +
                "FROM " +
                "stat_names " +
                "WHERE " +
                "local_language_id=" +
                String.valueOf(languageId) +
                " AND " +
                "stat_id " +
                "IN (" +
                "SELECT " +
                whichStat +
                "FROM " +
                "natures " +
                "WHERE " +
                "id=" +
                String.valueOf(natureId) +
                ")";

        Cursor c = database.rawQuery(s, null);
        c.moveToFirst();
        String name = "";
        if (c.getCount() > 0)
            name = c.getString(0);

        c.close();

        return name;
    }


}
