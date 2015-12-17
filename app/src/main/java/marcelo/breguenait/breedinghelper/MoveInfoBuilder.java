package marcelo.breguenait.breedinghelper;

public class MoveInfoBuilder {
    private String name = "";
    private String type = "";
    private int typeId = 1;
    private int power = 1;
    private int accuracy = 1;
    private String moveClass = "";
    private int level = 1;
    private int machineNumber = 1;
    private boolean isHiddenMachine = false;

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

    public MoveInfoBuilder isHiddenMachine(boolean isHiddenMachine) {
        this.isHiddenMachine = isHiddenMachine;
        return this;
    }

    public MoveInfo createMoveInfo() {
        return new MoveInfo(name, type, typeId, power, accuracy, moveClass, level, machineNumber, isHiddenMachine);
    }
}