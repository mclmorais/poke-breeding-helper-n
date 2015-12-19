package marcelo.breguenait.breedinghelper;

import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MoveDexActivity extends AppCompatActivity implements SelectPokemonFragment.OnPokemonSelectedListener,AppBarLayout.OnOffsetChangedListener {

    private static final String TAG = MoveDexActivity.class.getSimpleName();

    ImageView floatingIcon;
    CollapsingToolbarLayout collapsingToolbarLayout;

    private MyDatabase db;

    Toolbar mToolbar;
    Button buttonSelectPokemon;
    TabLayout tabLayout;
    private PagerAdapter pagerAdapter;
    List<MoveInfo> movesList;
    private ViewPager viewPager;
    int debugVersion = 16;
    private int currentPoceymanId;

    void bindActivity() {
        buttonSelectPokemon = (Button) findViewById(R.id.moveDex_buttonSelectPokemon);
        floatingIcon = (ImageView) findViewById(R.id.moveDex_floatingIcon);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.moveDex_collapsingToolbar);
        tabLayout = (TabLayout) findViewById(R.id.moveDex_tabLayout);
        viewPager = (ViewPager) findViewById(R.id.moveDex_viewPager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_dex);

        movesList = new ArrayList<>();

        bindActivity();

        if(Build.VERSION.SDK_INT >= 21)
            floatingIcon.setElevation(100);

        mToolbar = (Toolbar) findViewById(R.id.moveDex_toolbar);
        setSupportActionBar(mToolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        buttonSelectPokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });





        db = new MyDatabase(this);

        tabLayout.addTab(tabLayout.newTab().setText("Level Up"));
        tabLayout.addTab(tabLayout.newTab().setText("TM/HM"));
        tabLayout.addTab(tabLayout.newTab().setText("Egg Moves"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        pagerAdapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());

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


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }


    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
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
        currentPoceymanId = id;
        String iconId = "pkmn_big_" + String.format("%03d", id);
        floatingIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", MoveDexActivity.this.getPackageName()));

        collapsingToolbarLayout.setTitle(db.getPokemonName(id));

        EggMovesListFragment page = (EggMovesListFragment) pagerAdapter.getItem(2);
        page.mRecyclerView.removeAllViews();
        page.mRecyclerView.setAdapter(new EggMoveAdapter(this,null,page.mRecyclerView));
        page.loadingIcon.setVisibility(View.VISIBLE);
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


        if(methodId == 1) {
            LevelMovesListFragment page = (LevelMovesListFragment) pagerAdapter.getItem(0);
            if (page != null)
                page.switchData(list);
        }
        else if (methodId == 4) {
            MachineMovesListFragment page = (MachineMovesListFragment) pagerAdapter.getItem(1);


                class CustomComparator implements Comparator<MoveInfo> {
                    @Override
                    public int compare(MoveInfo o1, MoveInfo o2) {
                        return Integer.compare(o1.getMachineNumber(), o2.getMachineNumber());
                    }
                }
            if (page != null) {
                Collections.sort(list, new CustomComparator());
                page.switchData(list);


            }

        }
        else if (methodId == 2) {
            EggMovesListFragment page = (EggMovesListFragment) pagerAdapter.getItem(2);


            class CustomComparator implements Comparator<MoveInfo> {
                @Override
                public int compare(MoveInfo o1, MoveInfo o2) {
                    return (o1.getName().compareTo(o2.getName()));
                }
            }

            if(page != null) {
                Collections.sort(list, new CustomComparator());
                page.switchData(list);
            }
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
            Log.d(TAG,"Starting!");

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

            moves = (ArrayList<MoveInfo>) database.getPokemonMovesInfo(id,gameId,methodId,languageId);

            return 0;
        }


        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            Log.d(TAG,"Finished!");
            moveDexActivity.updateListofMoves(moves, methodId);
        }

    }



}

