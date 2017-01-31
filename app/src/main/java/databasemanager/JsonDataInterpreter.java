package databasemanager;

import android.content.Context;
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

/**
 * Retrieves all pokemon-related data from JSON files and provides it to the appropriate
 * 'data' class.
 */

public class JsonDataInterpreter {

    SparseArray<JsonNatureDataBlock> natureData = new SparseArray<>();


    public JsonDataInterpreter(Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();


        JsonDeserializer<SparseArray<JsonNatureDataBlock>> natureDeserializer = new JsonDeserializer<SparseArray<JsonNatureDataBlock>>() {
            @Override
            public SparseArray<JsonNatureDataBlock> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
        };

        Type typeOfHashMap = new TypeToken<SparseArray<JsonNatureDataBlock>>(){}.getType();

        gsonBuilder.registerTypeAdapter(typeOfHashMap, natureDeserializer);

        Gson customGson = gsonBuilder.create();

        final InputStream assetFile;
        try {
            assetFile = context.getResources().getAssets().open("natures.json");
            Reader reader = new InputStreamReader(assetFile, "UTF-8");
            natureData = customGson.fromJson(reader, typeOfHashMap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int x = 0;
    }

}
