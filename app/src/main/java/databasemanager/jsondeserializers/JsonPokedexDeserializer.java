package databasemanager.jsondeserializers;

import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

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
        JsonArray jsonArray = jsonObject.get("natures").getAsJsonArray();

        SparseArray<JsonPokedexDataBlock> natureData = new SparseArray<>();

        for (JsonElement natureJsonElement : jsonArray) {

            JsonObject natureJsonObject = (JsonObject) natureJsonElement;

//            JsonPokedexDataBlock deserializedNatureBlock = new
//                    JsonPokedexDataBlock(natureJsonObject.get("id").getAsInt(),
//                    natureJsonObject.get("identifier").getAsString(),
//                    natureJsonObject.get("increased_stat_id").getAsInt(),
//                    natureJsonObject.get("decreased_stat_id").getAsInt());

          //  natureData.append(natureJsonObject.get("id").getAsInt(), deserializedNatureBlock);

        }

        return natureData;
    }
}
