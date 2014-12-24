package marcelo.breguenait.breedinghelper;

/**
 * Created by Marcelo on 18/12/2014.
 */
public class ActivePokemons {
    private HatchInfo maleParent;
    private HatchInfo femaleParent;
    private HatchInfo goal;

    public HatchInfo getMaleParent() {
        return maleParent;
    }

    public void setMaleParent(HatchInfo maleParent) {
        this.maleParent = maleParent;
    }

    public HatchInfo getFemaleParent() {
        return femaleParent;
    }

    public void setFemaleParent(HatchInfo femaleParent) {
        this.femaleParent = femaleParent;
    }

    public HatchInfo getGoal() {
        return goal;
    }

    public void setGoal(HatchInfo goal) {
        this.goal = goal;
    }

}
