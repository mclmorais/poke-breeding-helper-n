package marcelo.breguenait.breedinghelper;

import java.util.ArrayList;

/**
 * Created by Marcelo on 16/12/2015.
 */
public class MoveInfo {

    private final String name;
    private final String type;
    private final int    typeId;
    private final int    power;
    private final int    accuracy;
    private final String moveClass;
    private final int    level;
    private final int    machineNumber;
    private final boolean isHiddenMachine;
    private final ArrayList<Integer> parentIds;

    public MoveInfo(String name, String type, int typeId, int power, int accuracy, String moveClass, int level, int machineNumber, boolean isHiddenMachine, ArrayList<Integer> parentIds) {
        this.name = name;
        this.type = type;
        this.typeId = typeId;
        this.power = power;
        this.accuracy = accuracy;
        this.moveClass = moveClass;
        this.level = level;
        this.machineNumber = machineNumber;
        this.isHiddenMachine = isHiddenMachine;
        this.parentIds = parentIds;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getTypeId() {
        return typeId;
    }

    public int getPower() {
        return power;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public String getMoveClass() {
        return moveClass;
    }

    public int getLevel() {
        return level;
    }

    public int getMachineNumber() {
        return machineNumber;
    }

    public boolean isHiddenMachine() {
        return isHiddenMachine;
    }

    public ArrayList<Integer> getParentIds() {
        return parentIds;
    }
}
