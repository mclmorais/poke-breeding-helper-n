package marcelo.breguenait.breedinghelper.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import marcelo.breguenait.breedinghelper.LevelMovesListFragment;
import marcelo.breguenait.breedinghelper.MachineMovesListFragment;

/**
 * Created by Marcelo on 13/12/2015.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
    LevelMovesListFragment tab1, tab3;
    MachineMovesListFragment tab2;
    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        tab1 = new LevelMovesListFragment();
        tab2 = new MachineMovesListFragment();
        tab3 = new LevelMovesListFragment();
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return tab1;
            case 1:
                return tab2;
            case 2:
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }



}