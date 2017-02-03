package databasemanager.jsondatablocks;

/**
 * "Block" of data containing information related to natures. The list of these objects is
 * created by
 * {@link databasemanager.JsonFileReader} and provided to
 * {@link databasemanager.JsonDatabaseManager}. Negative values and null strings represent ERROR
 * VALUES: filled data should never contain them.
 * If the pokemon doesn't have a value (e.g. only has one type instead of two), is should be set to
 * a "none" value instead of being left at -1.
 */
public class JsonNatureDataBlock {
    private int id = -1;
    private String name = null;
    private int increased_stat_id = -1;
    private int decreased_stat_id = -1;

    public JsonNatureDataBlock(int id, String name, int increased_stat_id, int decreased_stat_id) {
        this.id = id;
        this.name = name;
        this.increased_stat_id = increased_stat_id;
        this.decreased_stat_id = decreased_stat_id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getIncreased_stat_id() {
        return increased_stat_id;
    }

    public int getDecreased_stat_id() {
        return decreased_stat_id;
    }
}
