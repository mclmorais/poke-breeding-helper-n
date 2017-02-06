package databasemanager.jsondeserializers;

import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import databasemanager.jsondatablocks.JsonPokedexDataBlock;

/**
 * Created by Marcelo on 03/02/2017.
 */

public class JsonPokedexDeserializer extends JsonGenericDeserializer<SparseArray<JsonPokedexDataBlock>> {

    private Type structureType = new TypeToken<SparseArray<JsonPokedexDataBlock>>() {
    }.getType();

    @Override
    public Type getStructureType() {
        return structureType;
    }

    @Override
    public Class<?> getDataBlockClass() {
        return JsonPokedexDataBlock.class;
    }

    @Override
    public SparseArray<JsonPokedexDataBlock> deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("pokedex").getAsJsonArray();

        SparseArray<JsonPokedexDataBlock> pokedexData = new SparseArray<>();

        for (JsonElement pokedexJsonElement : jsonArray) {

            JsonObject pokedexJsonObject = (JsonObject) pokedexJsonElement;

            int id = pokedexJsonObject.get("id").getAsInt();
            int number = pokedexJsonObject.get("num").getAsInt();
            String name = pokedexJsonObject.get("species").getAsString();
            int gender = pokedexJsonObject.get("gender").getAsInt();

            int[] types = new int[2];
            JsonArray jsonTypes = pokedexJsonObject.get("types").getAsJsonArray();
            for (int i = 0; i < jsonTypes.size(); i++)
                types[i] = jsonTypes.get(i).getAsInt();

            int[] abilities = new int[3];
            JsonObject jsonAbilities = pokedexJsonObject.get("abilities").getAsJsonObject();
            abilities[0] = jsonAbilities.get("0").getAsInt();
            abilities[1] = jsonAbilities.get("1").getAsInt();
            abilities[2] = jsonAbilities.get("H").getAsInt();

            int[] eggGroups = new int[2];
            JsonArray jsonEggGroups = pokedexJsonObject.get("eggGroups").getAsJsonArray();
            for (int i = 0; i < jsonEggGroups.size(); i++)
                eggGroups[i] = jsonEggGroups.get(i).getAsInt();

            int evolutionChain = pokedexJsonObject.get("evolution_chain").getAsInt();

            int previousEvolution = -1;
            if (((JsonObject) pokedexJsonElement).has("prevo"))
                previousEvolution = pokedexJsonObject.get("prevo").getAsInt();
            else
                previousEvolution = 0;

            ArrayList<Integer> evolutions = new ArrayList<>(3);
            if (pokedexJsonObject.has("evos")) {
                JsonArray jsonEvolutions = pokedexJsonObject.get("evos").getAsJsonArray();
                for (JsonElement evolution : jsonEvolutions)
                    evolutions.add(evolution.getAsInt());
            }
            else
                evolutions.add(0);

            JsonPokedexDataBlock deserializedPokedexBlock = new
                    JsonPokedexDataBlock(id, number, name, gender, types, abilities, eggGroups, evolutionChain,
                    previousEvolution, evolutions);
            pokedexData.append(deserializedPokedexBlock.getId(), deserializedPokedexBlock);

        }

        return pokedexData;
    }
}
