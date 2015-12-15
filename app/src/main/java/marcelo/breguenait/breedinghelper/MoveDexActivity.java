package marcelo.breguenait.breedinghelper;

import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class MoveDexActivity extends AppCompatActivity implements SelectPokemonFragment.OnPokemonSelectedListener,AppBarLayout.OnOffsetChangedListener {

    ImageView icon;
    MovesManager movesManager;

    private List<String>parentHeaderInformation;

    private MyDatabase db;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_dex);

//        setSupportActionBar((Toolbar) findViewById(R.id.main_activity_toolbar));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mToolbar = (Toolbar) findViewById(R.id.TOOLBAR);
        setSupportActionBar(mToolbar);
        movesManager = new MovesManager(getApplicationContext());


        Button debugButton = (Button) findViewById(R.id.button_debug);

        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectPokemonFragment(view);
            }
        });

        icon = (ImageView) findViewById(R.id.imageViewIcon);


        db = new MyDatabase(this);

        parentHeaderInformation = new ArrayList<String>();
        parentHeaderInformation.add("Cars");
        parentHeaderInformation.add("Houses");
        parentHeaderInformation.add("Football Clubs");
        HashMap<String, List<String>> allChildItems = returnGroupedChildItems();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private HashMap<String, List<String>> returnGroupedChildItems(){
        HashMap<String, List<String>> childContent = new HashMap<String, List<String>>();
        List<String> cars = new ArrayList<String>();
        cars.add("Volvo");
        cars.add("BMW");
        cars.add("Toyota");
        cars.add("Nissan");
        List<String> houses = new ArrayList<String>();
        houses.add("Duplex");
        houses.add("Twin Duplex");
        houses.add("Bungalow");
        houses.add("Two Storey");
        List<String> footballClubs = new ArrayList<String>();
        footballClubs.add("Liverpool");
        footballClubs.add("Arsenal");
        footballClubs.add("Stoke City");
        footballClubs.add("West Ham");
        childContent.put(parentHeaderInformation.get(0), cars);
        childContent.put(parentHeaderInformation.get(1), houses);
        childContent.put(parentHeaderInformation.get(2), footballClubs);
        return childContent;
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

    private void showMovesDB(int id) {

        String tutorMoveString = "Tutor moves: \n";
        Cursor pokemonMoves = db.getPokemonMoves(id, 15);

        List<Integer> moves = db.getPokemonEggMoves(id,15);

        String s = "";
        for(Integer move : moves) {
            s += db.getMoveName(move,15,9);
            s += "\n";

            List<String> parents = db.getParentsEggMoveNames(id, move);
            for (String parent : parents) {
                s += "-->" + parent;
                s += "\n";
            }

        }

        System.out.println(s);
    }

    @Override
    public void onPokemonSelected(int id) {
        String iconId = "pkmn_big_" + String.format("%03d", id);
        icon.setImageResource(getResources().getIdentifier(iconId, "drawable", MoveDexActivity.this.getPackageName()));
        //showMoves(id);
        //showMovesDB(id);
        //movesManager.showMoves(id);
        movesManager.showMovesAsync(id);
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
}

