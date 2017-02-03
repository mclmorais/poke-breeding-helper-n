package databasemanager.jsondatablocks;

/**
 * Contains general egg group data retrieved from JSON files. This class receives its information
 * from the JsonFileReader class and provides it to the JsonDatabaseManager class.
 */

public class JsonEggGroupDataBlock {
    private int id = -1;
    private String name = null;

    public JsonEggGroupDataBlock(int id, String name) {
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
