package databasemanager;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

import databasemanager.jsondatablocks.JsonNatureDataBlock;
import databasemanager.jsondatablocks.JsonTypeDataBlock;
import databasemanager.jsondeserializers.JsonGenericDeserializer;
import databasemanager.jsondeserializers.JsonNatureDeserializer;
import databasemanager.jsondeserializers.JsonTypeDeserializer;

/**
 * Retrieves all pokemon-related data from JSON files and provides it to the manager class.
 */

public class JsonFileReader {

    public JsonFileReader(Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson customGson;
        InputStream assetFile;
        InputStreamReader reader;

        SparseArray<JsonNatureDataBlock> natureData =
                OpenWithCustomDeserializer(new JsonNatureDeserializer(), "natures.json", context);
        JsonDatabaseManager.setNatureData(natureData);
        SparseArray<JsonTypeDataBlock> typeData = OpenWithCustomDeserializer(new
                JsonTypeDeserializer(), "types.json", context);
        JsonDatabaseManager.setTypeData(typeData);

        Type structureType = new TypeToken<SparseArray<JsonNatureDataBlock>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(structureType, new JsonNatureDeserializer());
        customGson = gsonBuilder.create();
        try {
            assetFile = context.getResources().getAssets().open("natures.json");
            reader = new InputStreamReader(assetFile, "UTF-8");
            SparseArray<JsonNatureDataBlock> readData = customGson.fromJson(reader, structureType);
            JsonDatabaseManager.setNatureData(readData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private <T> T OpenWithCustomDeserializer(JsonGenericDeserializer deserializer,
                                             String fileName,
                                             Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson customGson;
        InputStream assetFile;
        InputStreamReader reader;

        Type structureType = deserializer.getStructureType();
        gsonBuilder.registerTypeAdapter(structureType, deserializer);
        customGson = gsonBuilder.create();

        try {
            assetFile = context.getResources().getAssets().open(fileName);
            reader = new InputStreamReader(assetFile, "UTF-8");
            return customGson.fromJson(reader, structureType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
