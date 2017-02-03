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
import databasemanager.jsondatablocks.JsonTypeDataBlock;

/**
 * Used by the {@link databasemanager.JsonFileReader} class to deserialize the Types JSON file
 * into a {@link SparseArray} containing {@link databasemanager.jsondatablocks.JsonTypeDataBlock}.
 */
public class JsonTypeDeserializer extends JsonGenericDeserializer<SparseArray<JsonTypeDataBlock>> {

    private final Type StructureType = new TypeToken<SparseArray<JsonTypeDataBlock>>() {
    }.getType();

    @Override
    public Type getStructureType() {
        return StructureType;
    }

    @Override
    public Class<?> getDataBlockClass() {
        return JsonNatureDataBlock.class;
    }

    @Override
    public SparseArray<JsonTypeDataBlock> deserialize(
            JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonArray jsonArray = jsonObject.get("types").getAsJsonArray();

        SparseArray<JsonTypeDataBlock> typeData = new SparseArray<>();

        for (JsonElement typeJsonElement : jsonArray) {

            JsonObject typeJsonObject = (JsonObject) typeJsonElement;

            JsonTypeDataBlock deserializedTypeBlock = new
                    JsonTypeDataBlock(typeJsonObject.get("id").getAsInt(),
                    typeJsonObject.get("name").getAsString(),
                    typeJsonObject.get("generation_id").getAsInt(),
                    typeJsonObject.get("damage_class_id").getAsInt());

            typeData.append(typeJsonObject.get("id").getAsInt(), deserializedTypeBlock);

        }

        return typeData;
    }
}
