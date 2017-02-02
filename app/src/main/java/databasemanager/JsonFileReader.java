package databasemanager;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import databasemanager.jsondatablocks.JsonNatureDataBlock;
import databasemanager.jsondeserializers.JsonNatureDeserializer;

/**
 * Retrieves all pokemon-related data from JSON files and provides it to the manager class.
 */

public class JsonFileReader {

    public JsonFileReader(Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        JsonNatureDeserializer natureDeserializer = new JsonNatureDeserializer();
        gsonBuilder.registerTypeAdapter(natureDeserializer.getType(), natureDeserializer);
        Gson natureGson = gsonBuilder.create();
        final InputStream assetFile;
        try {
            assetFile = context.getResources().getAssets().open("natures.json");
            Reader reader = new InputStreamReader(assetFile, "UTF-8");
            SparseArray<JsonNatureDataBlock> natureData =
                    natureGson.fromJson(reader,natureDeserializer.getType());
            JsonDatabaseManager.setNatureData(natureData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
