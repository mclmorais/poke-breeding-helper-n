package databasemanager.jsondeserializers;

import com.google.gson.JsonDeserializer;

import java.lang.reflect.Type;

/**
 * Deserialized Interface that asks classes that extend it to provide more information about the
 * objects that it will handle. {@link #getDataBlockClass()} should return the class type of the
 * data block that will be used to deserialized the file. {@link #getStructureType()} should return
 * the Structure (list, map, array etc) that will handle the appropriate Data Blocks.
 */

interface JsonCustomDeserializer<T> extends JsonDeserializer<T> {
    Type getStructureType();
    Class<?> getDataBlockClass();
}
