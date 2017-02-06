package databasemanager.jsondeserializers;

import android.util.SparseArray;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import databasemanager.jsondatablocks.JsonAbilityDataBlock;
import databasemanager.jsondatablocks.JsonNatureDataBlock;

/**
 * Used by the {@link databasemanager.JsonFileReader} class to deserialize the Natures JSON file
 * into a {@link SparseArray} containing {@link databasemanager.jsondatablocks.JsonAbilityDataBlock}.
 */


public class JsonAbilityDeserializer extends JsonGenericDeserializer<SparseArray<JsonAbilityDataBlock>> {
    
    private Type structureType = new TypeToken<SparseArray<JsonAbilityDataBlock>>() {
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
    public SparseArray<JsonAbilityDataBlock> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext
            context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("abilities").getAsJsonArray();

        SparseArray<JsonAbilityDataBlock> abilityData = new SparseArray<>(260);

        for (JsonElement abilityJsonElement : jsonArray) {

            JsonObject abilityJsonObject = (JsonObject) abilityJsonElement;

            JsonAbilityDataBlock deserializedAbilityBlock = new
                    JsonAbilityDataBlock(abilityJsonObject.get("num").getAsInt(),
                    abilityJsonObject.get("name").getAsString());

            abilityData.append(abilityJsonObject.get("num").getAsInt(), deserializedAbilityBlock);

        }

        return abilityData;
    }
}
