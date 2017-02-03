package databasemanager.jsondeserializers;

import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import databasemanager.jsondatablocks.JsonNatureDataBlock;

/**
 * Used by the {@link databasemanager.JsonFileReader} class to deserialize the Natures JSON file
 * into a {@link SparseArray} containing {@link databasemanager.jsondatablocks.JsonNatureDataBlock}.
 */

public class JsonNatureDeserializer extends
        JsonGenericDeserializer<SparseArray<JsonNatureDataBlock>> {

    private Type structureType = new TypeToken<SparseArray<JsonNatureDataBlock>>() {
    }.getType();

    @Override
    public Type getStructureType() {
        return structureType;
    }

    @Override
    public Class<?> getDataBlockClass() {
        return JsonNatureDataBlock.class;
    }

    @Override
    public SparseArray<JsonNatureDataBlock> deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("natures").getAsJsonArray();

        SparseArray<JsonNatureDataBlock> natureData = new SparseArray<>();

        for (JsonElement natureJsonElement : jsonArray) {

            JsonObject natureJsonObject = (JsonObject) natureJsonElement;

            JsonNatureDataBlock deserializedNatureBlock = new
                    JsonNatureDataBlock(natureJsonObject.get("id").getAsInt(),
                    natureJsonObject.get("identifier").getAsString(),
                    natureJsonObject.get("increased_stat_id").getAsInt(),
                    natureJsonObject.get("decreased_stat_id").getAsInt());

            natureData.append(natureJsonObject.get("id").getAsInt(), deserializedNatureBlock);

        }

        return natureData;
    }


}
