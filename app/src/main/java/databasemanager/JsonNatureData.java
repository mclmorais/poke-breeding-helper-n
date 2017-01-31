package databasemanager;

import java.util.ArrayList;

/**
 * Contains nature retrieved from JSON files. This class receives its information
 * from the JsonDataInterpreter class and provides it to the JsonDataProvider class.
 *
 * Negative values and empty string represent ERROR VALUES: filled data should never contain them.
 * If the pokemon doesn't have a value (e.g. only has one type instead of two), is should be set to
 * a "none" value instead of being left at -1.
 */

class JsonNatureData {

    ArrayList<JsonNatureDataBlock> data = new ArrayList<>();





}

class JsonNatureDataBlock {
    private int id = -1;
    private String name = "";
    private int increased_stat_id = -1;
    private int decreased_stat_id = -1;

    JsonNatureDataBlock(int id, String name, int increased_stat_id, int decreased_stat_id) {
        this.id = id;
        this.name = name;
        this.increased_stat_id = increased_stat_id;
        this.decreased_stat_id = decreased_stat_id;
    }
}
