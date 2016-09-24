package breedingmanager;

import java.util.ArrayList;

public class MoveVerbose {

    private final String name;
    private final String type;
    private final int typeId;
    private final int power;
    private final int accuracy;
    private final String moveClass;
    private final int level;
    private final int machineNumber;
    private final boolean isHiddenMachine;
    private final ArrayList<Integer> parentIds;

    public MoveVerbose(String name,
                       String type,
                       int typeId,
                       int power,
                       int accuracy,
                       String moveClass,
                       int level,
                       int machineNumber,
                       boolean isHiddenMachine,
                       ArrayList<Integer> parentIds) {
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

    @SuppressWarnings("UnusedReturnValue")
    public static class MoveInfoBuilder {
        private String name = "";
        private String type = "";
        private int typeId = 1;
        private int power = 1;
        private int accuracy = 1;
        private String moveClass = "";
        private int level = 1;
        private int machineNumber = 1;
        private boolean isHiddenMachine = false;
        private ArrayList<Integer> parentIds = null;


        public MoveInfoBuilder setParents(ArrayList<Integer> parentIds) {
            this.parentIds = parentIds;
            return this;
        }

        public MoveInfoBuilder setName(String name) {
            this.name = name;
            return this;
        }

        public MoveInfoBuilder setType(String type) {
            this.type = type;
            return this;
        }

        public MoveInfoBuilder setTypeId(int typeId) {
            this.typeId = typeId;
            return this;
        }

        public MoveInfoBuilder setPower(int power) {
            this.power = power;
            return this;
        }

        public MoveInfoBuilder setAccuracy(int accuracy) {
            this.accuracy = accuracy;
            return this;
        }

        public MoveInfoBuilder setMoveClass(String moveClass) {
            this.moveClass = moveClass;
            return this;
        }

        public MoveInfoBuilder setLevel(int level) {
            this.level = level;
            return this;
        }

        public MoveInfoBuilder setMachineNumber(int machineNumber) {
            this.machineNumber = machineNumber;
            return this;
        }

        public MoveInfoBuilder isHiddenMachine() {
            this.isHiddenMachine = true;
            return this;
        }

        public MoveVerbose createMoveInfo() {
            return new MoveVerbose(name, type, typeId, power, accuracy, moveClass, level, machineNumber, isHiddenMachine, parentIds);
        }
    }
}
