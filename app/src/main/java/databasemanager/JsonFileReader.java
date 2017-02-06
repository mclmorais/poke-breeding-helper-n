package databasemanager;

import android.content.Context;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;

import databasemanager.jsondatablocks.JsonAbilityDataBlock;
import databasemanager.jsondatablocks.JsonEggGroupDataBlock;
import databasemanager.jsondatablocks.JsonNatureDataBlock;
import databasemanager.jsondatablocks.JsonPokedexDataBlock;
import databasemanager.jsondatablocks.JsonTypeDataBlock;
import databasemanager.jsondeserializers.JsonAbilityDeserializer;
import databasemanager.jsondeserializers.JsonEggGroupDeserializer;
import databasemanager.jsondeserializers.JsonGenericDeserializer;
import databasemanager.jsondeserializers.JsonNatureDeserializer;
import databasemanager.jsondeserializers.JsonPokedexDeserializer;
import databasemanager.jsondeserializers.JsonTypeDeserializer;

/**
 * Retrieves all pokemon-related data from JSON files and provides it to the manager class.
 */

public class JsonFileReader {

    public JsonFileReader(Context context) {

        SparseArray<JsonNatureDataBlock> natureData =
                OpenWithCustomDeserializer(new JsonNatureDeserializer(), "natures.json", context);
        JsonDatabaseManager.setNatureData(natureData);
        SparseArray<JsonTypeDataBlock> typeData =
                OpenWithCustomDeserializer(new JsonTypeDeserializer(), "types.json", context);
        JsonDatabaseManager.setTypeData(typeData);
        SparseArray<JsonEggGroupDataBlock> eggGroupData =
                OpenWithCustomDeserializer(new JsonEggGroupDeserializer(), "egg_groups.json", context);
        JsonDatabaseManager.setEggGroupData(eggGroupData);
        SparseArray<JsonAbilityDataBlock> abilityData =
                OpenWithCustomDeserializer(new JsonAbilityDeserializer(), "abilities.json", context);
        JsonDatabaseManager.setAbilityData(abilityData);
        SparseArray<JsonPokedexDataBlock> pokedexData =
            OpenWithCustomDeserializer(new JsonPokedexDeserializer(), "pokedex.json", context);
        JsonDatabaseManager.setPokedexData(pokedexData);


    }


    private <T> T OpenWithCustomDeserializer(JsonGenericDeserializer deserializer, String
            fileName, Context context) {
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
