package marcelo.breguenait.breedinghelper;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import breedingmanager.StoredPokemon;
import butterknife.Bind;
import butterknife.ButterKnife;
import databasemanager.MyDatabase;

public class MoveDexFragment extends Fragment implements
        SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon,
        AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = MoveDexFragment.class.getSimpleName();
    private final Gson gson = new Gson();
    int languageId = 9;
    ActionBarDrawerToggle drawerToggle;
    MainActivity mainActivity;
    @Bind(R.id.moveDex_toolbar)
    Toolbar toolbar;
    @Bind(R.id.moveDex_buttonSelectPokemon)
    FloatingActionButton buttonSelectPokemon;
    @Bind(R.id.moveDex_tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.moveDex_floatingIcon)
    ImageView floatingIcon;
    @Bind(R.id.moveDex_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.moveDex_viewPager)
    ViewPager viewPager;
    List<MoveInfo> movesList;
    int debugVersion = 16; //TODO: fazer pegar do usuário
    private MyDatabase db;
    private MovesPagerAdapter movesPagerAdapter;


    void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonString;

        languageId = Integer.valueOf(sharedPref.getString("gameLanguage", "9"));

        jsonString = sharedPref.getString("jsonBreedingManagerGoal", null);
        if (jsonString != null) {
            onPokemonSelected(gson.fromJson(jsonString, StoredPokemon.class).getPokemonId());
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_move_dex, container, false);
        ButterKnife.bind(this, v);


        cleanOldFragments();

        mainActivity = (MainActivity) getActivity();

        movesList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 21)
            floatingIcon.setElevation(100);

        drawerToggle = setupDrawerToggle();
        mainActivity.mDrawer.setDrawerListener(drawerToggle);


        buttonSelectPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });


        db = MyDatabase.getInstance();

        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_level_up));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_machine));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.label_egg_moves));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        movesPagerAdapter = new MovesPagerAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(movesPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        readData();


        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawerToggle.syncState();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(),
                mainActivity.getDrawer(), toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.setTargetFragment(this, 0);
        selectPokemonFragment.show(fm, "");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }

    @Override
    public void onPokemonSelected(int id) {

        if (id == 0)
            return;

        String iconId = "pkmn_big_" + String.format("%03d", id);
        floatingIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getContext().getPackageName()));

        collapsingToolbarLayout.setTitle(db.getPokemonName(id, languageId));

        EggMovesListFragment page = (EggMovesListFragment) movesPagerAdapter.getItem(2);
        if (page.mRecyclerView != null) {
            page.mRecyclerView.removeAllViews();
            page.mRecyclerView.setAdapter(new EggMovesListFragment.EggMoveAdapter(getContext(), null, page.mRecyclerView));
        }
        if (page.loadingIcon != null)
            page.loadingIcon.setVisibility(View.VISIBLE);
        if (page.noMovesText != null)
            page.noMovesText.setVisibility(View.GONE);


        //noinspection unchecked
        new LoadMovesAsync().execute(MoveDexFragment.this, db, id, debugVersion, 1, languageId);
        //noinspection unchecked
        new LoadMovesAsync().execute(MoveDexFragment.this, db, id, debugVersion, 4, languageId);
        //noinspection unchecked
        new LoadMovesAsync().execute(MoveDexFragment.this, db, id, debugVersion, 2, languageId);

    }

    @Override
    public boolean showEggGroupFilter() {
        return false;
    }

    @Override
    public boolean showOnlyBasic() {
        return false;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    }

    public void updateListofMoves(ArrayList<MoveInfo> list, int methodId) {
        movesList = list;


        if (methodId == 1) {
            LevelMovesListFragment page = (LevelMovesListFragment) movesPagerAdapter.getItem(0);
            if (page != null)
                page.switchData(list);
        } else if (methodId == 4) {
            MachineMovesListFragment page = (MachineMovesListFragment) movesPagerAdapter.getItem(1);


            class CustomComparator implements Comparator<MoveInfo> {
                @Override
                public int compare(MoveInfo o1, MoveInfo o2) {
                    return o1.getMachineNumber() < o2.getMachineNumber() ? -1 : 1;//Integer.compare(o1.getMachineNumber(), o2.getMachineNumber());
                }
            }
            if (page != null) {
                Collections.sort(list, new CustomComparator());
                page.switchData(list);


            }

        } else if (methodId == 2) {
            EggMovesListFragment page = (EggMovesListFragment) movesPagerAdapter.getItem(2);


            class CustomComparator implements Comparator<MoveInfo> {
                @Override
                public int compare(MoveInfo o1, MoveInfo o2) {
                    return (o1.getName().compareTo(o2.getName()));
                }
            }

            if (page != null) {
                Collections.sort(list, new CustomComparator());
                page.switchData(list);
            }
        }
    }

    private void cleanOldFragments() {
        List<Fragment> fragments = getFragmentManager().getFragments();
        if (fragments != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            for (Fragment f : fragments) {
                if (f instanceof LevelMovesListFragment ||
                        f instanceof MachineMovesListFragment ||
                        f instanceof EggMovesListFragment
                        ) {
                    ft.remove(f);
                }
            }
            ft.commit();
        }
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return db.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return db.getPokemonNames(languageId);
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return new ArrayList<>(0);
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return new ArrayList<>(0);
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return new ArrayList<>(0);
    }

    public static class MovesPagerAdapter extends FragmentPagerAdapter {
        final int mNumOfTabs;
        final LevelMovesListFragment tab1;
        final MachineMovesListFragment tab2;
        final EggMovesListFragment tab3;

        public MovesPagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
            tab1 = new LevelMovesListFragment();
            tab2 = new MachineMovesListFragment();
            tab3 = new EggMovesListFragment();
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

    private class LoadMovesAsync extends AsyncTask {

        MoveDexFragment moveDexFragment;
        MyDatabase database;
        ArrayList<MoveInfo> moves;
        private int methodId;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "Starting!");

        }

        @Override
        protected Object doInBackground(Object[] params) {
            moves = new ArrayList<>();
            this.moveDexFragment = (MoveDexFragment) params[0];
            database = (MyDatabase) params[1];
            int id = (int) params[2];
            int gameId = (int) params[3];
            methodId = (int) params[4];
            int languageId = (int) params[5];

            moves = (ArrayList<MoveInfo>) database.getPokemonMovesInfo(id, gameId, methodId, languageId);

            return 0;
        }


        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d(TAG, "Finished!");
            moveDexFragment.updateListofMoves(moves, methodId);
        }

    }
}

