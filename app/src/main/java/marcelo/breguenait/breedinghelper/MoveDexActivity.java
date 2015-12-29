package marcelo.breguenait.breedinghelper;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MoveDexActivity extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener, AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = MoveDexActivity.class.getSimpleName();


    ActionBarDrawerToggle drawerToggle;

    InitialActivity initialActivity;
    @Bind(R.id.moveDex_toolbar)
    Toolbar toolbar;
    @Bind(R.id.moveDex_buttonSelectPokemon)
    Button buttonSelectPokemon;
    @Bind(R.id.moveDex_tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.moveDex_floatingIcon)
    ImageView floatingIcon;
    @Bind(R.id.moveDex_collapsingToolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.moveDex_viewPager)
    ViewPager viewPager;
    List<MoveInfo> movesList;
    int debugVersion = 16;
    private MyDatabase db;
    private PagerAdapter pagerAdapter;
    private int currentPoceymanId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_move_dex, container, false);
        ButterKnife.bind(this, v);

        cleanOldFragments();

        initialActivity = (InitialActivity) getActivity();

        movesList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= 21)
            floatingIcon.setElevation(100);

        drawerToggle = setupDrawerToggle();
        initialActivity.mDrawer.setDrawerListener(drawerToggle);


//        setSupportActionBar(toolbar);
//        if(getSupportActionBar() != null)
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");

        buttonSelectPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });


        db = MyDatabase.getInstance();// new MyDatabase(getContext());

        tabLayout.addTab(tabLayout.newTab().setText("Level Up"));
        tabLayout.addTab(tabLayout.newTab().setText("TM/HM"));
        tabLayout.addTab(tabLayout.newTab().setText("Egg Moves"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pagerAdapter);
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


        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        //onPokemonSelected(getIntent().getIntExtra("goalPokemon",0));
    }


    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(),
                initialActivity.getDrawer(), toolbar,
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

        currentPoceymanId = id;
        String iconId = "pkmn_big_" + String.format("%03d", id);
        floatingIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getContext().getPackageName()));

        collapsingToolbarLayout.setTitle(db.getPokemonName(id));

        EggMovesListFragment page = (EggMovesListFragment) pagerAdapter.getItem(2);
        if (page.mRecyclerView != null) {
            page.mRecyclerView.removeAllViews();
            page.mRecyclerView.setAdapter(new EggMoveAdapter(getContext(), null, page.mRecyclerView));
        }
        if (page.loadingIcon != null)
            page.loadingIcon.setVisibility(View.VISIBLE);
        if (page.noMovesText != null)
            page.noMovesText.setVisibility(View.GONE);


        new LoadMovesAsync().execute(MoveDexActivity.this, db, id, debugVersion, 1, 9);
        new LoadMovesAsync().execute(MoveDexActivity.this, db, id, debugVersion, 4, 9);
        new LoadMovesAsync().execute(MoveDexActivity.this, db, id, debugVersion, 2, 9);

        //showMoves(id);
        //showMovesDB(id);
        //movesManager.showMoves(id);

        //movesManager.showMovesAsync(id);
        //new MyAsyncTask().execute(movesManager, id);
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
    public PokemonInfo getGoal() {
        return null;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
    }

    public void updateListofMoves(ArrayList<MoveInfo> list, int methodId) {
        movesList = list;


        if (methodId == 1) {
            LevelMovesListFragment page = (LevelMovesListFragment) pagerAdapter.getItem(0);
            if (page != null)
                page.switchData(list);
        } else if (methodId == 4) {
            MachineMovesListFragment page = (MachineMovesListFragment) pagerAdapter.getItem(1);


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
            EggMovesListFragment page = (EggMovesListFragment) pagerAdapter.getItem(2);


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

    private class LoadMovesAsync extends AsyncTask {

        MoveDexActivity moveDexActivity;
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
            this.moveDexActivity = (MoveDexActivity) params[0];
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
            moveDexActivity.updateListofMoves(moves, methodId);
        }

    }

}

