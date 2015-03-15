package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;

class PokemonData {

    private static PokemonData instance;

    private SparseArray<PokemonDataBlock> tabledPokemonData;
    private SparseArray<AbilityDataBlock> tabledAbilityData;

    private PokemonData(Context c) {
        readPokemonData(c);
        readAbilityData(c);
    }

    void readPokemonData(Context c) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        /*Teaches gson how to deal with Maps of that type*/
        Type typeOfHashMap = new TypeToken<SparseArray<PokemonDataBlock>>(){}.getType();
        gsonBuilder.registerTypeAdapter(typeOfHashMap, new PokemonJsonDeserializer(c));
        Gson gson = gsonBuilder.create();

        final InputStream assetFile;
        try {
            assetFile = c.getResources().getAssets().open("pkmn.json");
            Reader reader = new InputStreamReader(assetFile, "UTF-8");
            tabledPokemonData = gson.fromJson(reader, typeOfHashMap);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readAbilityData(Context c) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        Type typeOfHashMap = new TypeToken<SparseArray<AbilityDataBlock>>(){}.getType();
        gsonBuilder.registerTypeAdapter(typeOfHashMap, new AbilityJsonDeserializer(c));
        Gson gson = gsonBuilder.create();

        final InputStream assetFile;
        try {
            assetFile = c.getResources().getAssets().open("abilities.json");
            Reader reader = new InputStreamReader(assetFile, "UTF-8");
            tabledAbilityData = gson.fromJson(reader, typeOfHashMap);

            int x = 2;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize(Context c) {

        instance = new PokemonData(c);
    }

    public static PokemonData getInstance() {
//        //TODO: WAT
//        if(instance == null) {
//           // instance = new PokemonData();
//        }

        return instance;



    }

    ArrayList<PokemonDataBlock> getOrderedData() {
        ArrayList<PokemonDataBlock> a = asList(tabledPokemonData);
        PokemonDataBlock ditto = a.get(Constants.DITTO_ID-1);
        a.remove(Constants.DITTO_ID-1);
        a.add(0,ditto);
        return a;
    }

    int getDataCount() {
        return tabledPokemonData.size();
    }

    Drawable getDrawableFromId(int id) {
        if(id > 0)
            return tabledPokemonData.get(id).drawable;
        else {
            throw new IllegalArgumentException("Invalid Pokemon number when requesting it's drawable from PokemonData! (Should be 1-714)");
        }
    }

    String getName(int id) {
        return tabledPokemonData.get(id).name;
    }

    EggGroup getFirstEggGroup(int id) {
        if(id > 0)
            return tabledPokemonData.get(id).eggGroup1;
        else
            return EggGroup.UNDISCOVERED;
    }

    EggGroup getSecondEggGroup(int id) {
        if(id > 0)
            return tabledPokemonData.get(id).eggGroup2;
        else
            return EggGroup.UNDISCOVERED;
    }

    GenderRestriction getGenderRestriction(int id) {
        return tabledPokemonData.get(id).genderRestriction;
    }

    int getBasicPokemon(int id) {
        if(id > 0)
            return tabledPokemonData.get(id).breeds;
        else
            return 0;
    }

    String getFirstAbility(int id) {
        if(tabledPokemonData.get(id).ability1 > 0)
            return tabledAbilityData.get(tabledPokemonData.get(id).ability1).name;
        else
            return "";
    }

    int getFirstAbilityId(int id) {
        return tabledPokemonData.get(id).ability1;
    }

    int getSecondAbilityId(int id) {
        return tabledPokemonData.get(id).ability2;
    }

    int getHiddenAbilityId(int id) {
        return tabledPokemonData.get(id).abilityHidden;
    }

    String getSecondAbility(int id) {
        if(tabledPokemonData.get(id).ability2 > 0)
            return tabledAbilityData.get(tabledPokemonData.get(id).ability2).name;
        else
            return "";
    }

    String getHiddenAbility(int id) {
        if(tabledPokemonData.get(id).abilityHidden > 0)
            return tabledAbilityData.get(tabledPokemonData.get(id).abilityHidden).name;
        else
            return "";
    }

    private static <C> ArrayList<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        ArrayList<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

}

class PokemonJsonDeserializer implements JsonDeserializer<SparseArray<PokemonDataBlock>> {

    final Context mContext;
    PokemonJsonDeserializer(Context context){
        mContext = context;
    }

    @Override
    public SparseArray<PokemonDataBlock> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray;
        jArray = json.getAsJsonObject().get("pokemons").getAsJsonArray();

        SparseArray<PokemonDataBlock> pokemons = new SparseArray<>();

        for (int i = 0; i < jArray.size(); i++) {
            JsonObject jObject = (JsonObject) jArray.get(i);

            String name;
            if(jObject.has("name"))
                    name = jObject.get("name").getAsString();
            else
                name = "ERROR";

            String eggGroup1;
            if(jObject.has("egg_group_1"))
                eggGroup1 = jObject.get("egg_group_1").getAsString();
            else
             eggGroup1 = "UNSET";

            String eggGroup2;
            if(jObject.has("egg_group_2"))
                eggGroup2 = jObject.get("egg_group_2").getAsString();
            else
                eggGroup2 = "UNSET";

            //int id = i+1; //USAR APENAS PARA DEBUG COM PKMN_TEST!!!
            int id = jObject.get("id").getAsInt();

            GenderRestriction genderRestriction = GenderRestriction.valueOf(jObject.get("restricted_gender").getAsString());

            int breeds = jObject.get("breeds").getAsInt();

            int ability1;
            if(jObject.has("ability_1"))
                ability1 = jObject.get("ability_1").getAsInt();
            else
                ability1 = 0;

            int ability2;
            if(jObject.has("ability_2"))
                ability2 = jObject.get("ability_2").getAsInt();
            else
                ability2 = 0;

            int abilityHidden;
            if(jObject.has("ability_hidden"))
                abilityHidden = jObject.get("ability_hidden").getAsInt();
            else
                abilityHidden = 0;

            PokemonDataBlock dataBlock = new PokemonDataBlock(
                    id,
                    name,
                    eggGroup1,
                    eggGroup2,
                    genderRestriction,
                    breeds,
                    ability1,
                    ability2,
                    abilityHidden,
                    mContext);

            pokemons.put(id, dataBlock);
        }

        return pokemons;
    }
}

class AbilityJsonDeserializer implements JsonDeserializer<SparseArray<AbilityDataBlock>> {

    final Context mContext;

    AbilityJsonDeserializer(Context mContext) {
        this.mContext = mContext;
        
    }

    @Override
    public SparseArray<AbilityDataBlock> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray jArray;
        jArray = json.getAsJsonObject().get("abilities").getAsJsonArray();
        
        SparseArray<AbilityDataBlock> abilities = new SparseArray<>();
        
        for(int i = 0; i < jArray.size(); i++){
            JsonObject jObject = (JsonObject) jArray.get(i);
            String name;
            if(jObject.has("name"))
                name = jObject.get("name").getAsString();
            else
                name = "ERROR";

            String description;
            if(jObject.has("description"))
                description = jObject.get("description").getAsString();
            else
                description = "ERROR";

            int id = jObject.get("id").getAsInt();

            AbilityDataBlock dataBlock = new AbilityDataBlock(
                    id,
                    name,
                    description
            );

            abilities.put(id,dataBlock);
        }
        
        return abilities;
    }
}

class PokemonDataBlock {
    final String name;
    final EggGroup eggGroup1;
    final EggGroup eggGroup2;
    final int id;
    final Drawable drawable;
    final GenderRestriction genderRestriction;
    final int breeds;
    final int ability1;
    final int ability2;
    final int abilityHidden;

    PokemonDataBlock(int id, String name, String eggGroup1, String eggGroup2, GenderRestriction genderRestriction, int breeds, int a1, int a2, int ah, Context c) {
        this.name      = name;
        this.eggGroup1 = EggGroup.valueOf(eggGroup1);
        this.eggGroup2 = EggGroup.valueOf(eggGroup2);
        this.id = id;
        String iconId = "pkmn_" + String.format("%03d", id);
        this.drawable = c.getResources().getDrawable(c.getResources().getIdentifier(iconId, "drawable", c.getPackageName()));
        this.genderRestriction = genderRestriction;
        this.breeds = breeds;
        this.ability1 = a1;
        this.ability2 = a2;
        this.abilityHidden = ah;
    }
}

class AbilityDataBlock {
    final int id;
    final String name;
    final String description;

    AbilityDataBlock(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
