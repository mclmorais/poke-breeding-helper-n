package databasemanager.jsondeserializers;

import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import databasemanager.jsondatablocks.JsonEggGroupDataBlock;
import databasemanager.jsondatablocks.JsonNatureDataBlock;

/**
 * Used by the {@link databasemanager.JsonFileReader} class to deserialize the Natures JSON file
 * into a {@link SparseArray} containing
 * {@link databasemanager.jsondatablocks.JsonEggGroupDataBlock}.
 */
public class JsonEggGroupDeserializer extends
        JsonGenericDeserializer<SparseArray<JsonEggGroupDataBlock>> {

    private final Type structureType = new TypeToken<SparseArray<JsonNatureDataBlock>>() {
    }.getType();

    @Override
    public Type getStructureType() {
        return structureType;
    }

    @Override
    public Class<?> getDataBlockClass() {
        return JsonEggGroupDataBlock.class;
    }

    @Override
    public SparseArray<JsonEggGroupDataBlock> deserialize(JsonElement json, Type typeOfT,
                                                          JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("egg_groups").getAsJsonArray();

        SparseArray<JsonEggGroupDataBlock> eggGroupData = new SparseArray<>();

        for (JsonElement eggGroupJsonElement : jsonArray) {

            JsonObject eggGroupJsonObject = (JsonObject) eggGroupJsonElement;

            JsonEggGroupDataBlock deserializedEggGroupBlock = new
                    JsonEggGroupDataBlock(eggGroupJsonObject.get("id").getAsInt(),
                    eggGroupJsonObject.get("name").getAsString());

            eggGroupData.append(eggGroupJsonObject.get("id").getAsInt(), deserializedEggGroupBlock);
        }

        return eggGroupData;
    }
}
