package marcelo.breguenait.breedinghelper;

/**
 * Created by Marcelo on 06/01/2016.
 */
public class InterfaceNature {
    public final int id;
    public final String natureName;
    public final String increasedStatName;
    public final String decreasedStatName;

    public InterfaceNature(int id, String natureName, String increasedStatName, String decreasedStatName) {
        this.id = id;
        this.natureName = natureName;
        this.increasedStatName = increasedStatName;
        this.decreasedStatName = decreasedStatName;
    }
}
