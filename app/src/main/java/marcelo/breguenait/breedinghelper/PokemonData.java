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
//TODO: fazer funçao que aplica essa lógica (no lugar da atual que só ve se é femea e só está funcionando em alguns lugares)

class PokemonData {

    private static PokemonData instance;

    private SparseArray<PokemonDataBlock> tabledData;

    PokemonData(Context c) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        /*Teaches gson how to deal with Maps of that type*/
        Type typeOfHashMap = new TypeToken<SparseArray<PokemonDataBlock>>(){}.getType();
        gsonBuilder.registerTypeAdapter(typeOfHashMap, new PokemonJsonDeserializer(c));
        Gson gson = gsonBuilder.create();

        final InputStream assetFile;
        try {
            assetFile = c.getResources().getAssets().open("pkmn.json");
            Reader reader = new InputStreamReader(assetFile, "UTF-8");
            tabledData = gson.fromJson(reader, typeOfHashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initialize(Context c) {

        instance = new PokemonData(c);
    }

    public static PokemonData getInstance() {
        //TODO: WAT
        if(instance == null) {
           // instance = new PokemonData();
        }

        return instance;



    }

    ArrayList<PokemonDataBlock> getOrderedData() {
        ArrayList<PokemonDataBlock> a = asList(tabledData);
        PokemonDataBlock ditto = a.get(Constants.DITTO_ID-1);
        a.remove(Constants.DITTO_ID-1);
        a.add(0,ditto);
        return a;
    }

    int getDataCount() {
        return tabledData.size();
    }

    Drawable getDrawableFromId(int id) {
        if(id > 0)
            return tabledData.get(id).drawable;
        else {
            throw new IllegalArgumentException("Invalid Pokemon number when requesting it's drawable from PokemonData! (Should be 1-714)");
        }
    }

    String getName(int id) {
        return tabledData.get(id).name;
    }
    EggGroup getFirstEggGroup(int id) {
        if(id > 0)
            return tabledData.get(id).eggGroup1;
        else
            return EggGroup.UNDISCOVERED;
    }

    EggGroup getSecondEggGroup(int id) {
        if(id > 0)
            return tabledData.get(id).eggGroup2;
        else
            return EggGroup.UNDISCOVERED;
    }

    GenderRestriction getGenderRestriction(int id) {
        return tabledData.get(id).genderRestriction;
    }

    int getBasicPokemon(int id) {
        if(id > 0)
            return tabledData.get(id).breeds;
        else
            return 0;
    }

    private static <C> ArrayList<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) return null;
        ArrayList<C> arrayList = new ArrayList<C>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++)
            arrayList.add(sparseArray.valueAt(i));
        return arrayList;
    }

}

class PokemonJsonDeserializer implements JsonDeserializer<SparseArray<PokemonDataBlock>> {

    Context mContext;
    PokemonJsonDeserializer(Context context){
        mContext = context;
    }

    @Override
    public SparseArray<PokemonDataBlock> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonArray jArray;
        jArray = json.getAsJsonObject().get("pokemons").getAsJsonArray();

        SparseArray<PokemonDataBlock> pokemons = new SparseArray<PokemonDataBlock>();

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
             eggGroup1 = "UNKNOWN";

            String eggGroup2;
            if(jObject.has("egg_group_2"))
                eggGroup2 = jObject.get("egg_group_2").getAsString();
            else
                eggGroup2 = "UNKNOWN";

            //int id = i+1; //USAR APENAS PARA DEBUG COM PKMN_TEST!!!
            int id = jObject.get("id").getAsInt();

            GenderRestriction genderRestriction = GenderRestriction.valueOf(jObject.get("restricted_gender").getAsString());

            int breeds = jObject.get("breeds").getAsInt();



            PokemonDataBlock dataBlock = new PokemonDataBlock(
                    id,
                    name,
                    eggGroup1,
                    eggGroup2,
                    genderRestriction,
                    breeds,
                    mContext);

            pokemons.put(id, dataBlock);
        }

        return pokemons;
    }
}

class PokemonDataBlock {
    String name;
    EggGroup eggGroup1;
    EggGroup eggGroup2;
    int id;
    Drawable drawable;
    GenderRestriction genderRestriction;
    int breeds;

    PokemonDataBlock(int id, String name, String eggGroup1, String eggGroup2, GenderRestriction genderRestriction, int breeds,Context c) {
        this.name      = name;
        this.eggGroup1 = EggGroup.valueOf(eggGroup1);
        this.eggGroup2 = EggGroup.valueOf(eggGroup2);
        this.id = id;
        String iconId = "pkmn_" + String.format("%03d", id);
        this.drawable = c.getResources().getDrawable(c.getResources().getIdentifier(iconId, "drawable", c.getPackageName()));
        this.genderRestriction = genderRestriction;
        this.breeds = breeds;
    }
}
