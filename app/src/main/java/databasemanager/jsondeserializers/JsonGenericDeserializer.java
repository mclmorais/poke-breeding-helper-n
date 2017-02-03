package databasemanager.jsondeserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by Marcelo on 03/02/2017.
 */

abstract public class JsonGenericDeserializer<T> implements  JsonCustomDeserializer<T>
{

}
