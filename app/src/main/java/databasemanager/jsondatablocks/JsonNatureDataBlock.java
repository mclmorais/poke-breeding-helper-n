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
    private int increasedStatId = -1;
    private int decreasedStatId = -1;

    public JsonNatureDataBlock(int id, String name, int increasedStatId, int decreasedStatId) {
        this.id = id;
        this.name = name;
        this.increasedStatId = increasedStatId;
        this.decreasedStatId = decreasedStatId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCapitalizedName() {
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    public int getIncreasedStatId() {
        return increasedStatId;
    }

    public int getDecreasedStatId() {
        return decreasedStatId;
    }
}
