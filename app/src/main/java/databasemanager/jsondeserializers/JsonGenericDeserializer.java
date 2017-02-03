package databasemanager.jsondeserializers;

/**
 * Class from which all other custom deserializers extend from. This allows the calling of all
 * kinds of deserializers from this same class.
 */

abstract public class JsonGenericDeserializer<T> implements  JsonCustomDeserializer<T>
{

}
