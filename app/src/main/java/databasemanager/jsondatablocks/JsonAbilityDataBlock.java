package databasemanager.jsondatablocks;

/**
 * "Block" of data containing information related to abilities. The list of these objects is
 * created by
 * {@link databasemanager.JsonFileReader} and provided to
 * {@link databasemanager.JsonDatabaseManager}. Negative values and null strings represent ERROR
 * VALUES: filled data should never contain them.
 * If the pokemon doesn't have a value (e.g. only has one type instead of two), is should be set to
 * a "none" value instead of being left at -1.
 */

public class JsonAbilityDataBlock {
    private int id = -1;
    private String name = null;

    public JsonAbilityDataBlock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
