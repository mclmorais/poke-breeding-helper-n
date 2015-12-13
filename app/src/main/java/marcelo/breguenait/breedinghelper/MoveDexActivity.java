package marcelo.breguenait.breedinghelper;

import android.app.FragmentManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MoveDexActivity extends ActionBarActivity implements SelectPokemonFragment.OnPokemonSelectedListener {

    ImageView icon;
    TextView movesText;

    private Cursor bulbasaurMoves;
    private MyDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_dex);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button debugButton = (Button) findViewById(R.id.button_debug);

        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectPokemonFragment(view);
            }
        });

        icon = (ImageView) findViewById(R.id.imageViewIcon);

        movesText = (TextView) findViewById(R.id.textViewMoves);
        movesText.setMovementMethod(new ScrollingMovementMethod());

        db = new MyDatabase(this);
        //employees = db.getQualquerCoisa(); // you would not typically call this on the main thread
        //employees.getString(0);

        bulbasaurMoves = db.getPokemonMoves(1,16);

        List<int[]> moves = new ArrayList<>();

        while(!bulbasaurMoves.isAfterLast()) {
            int[] move = new int[2];
            move[0] = bulbasaurMoves.getInt(0);
            move[1] = bulbasaurMoves.getInt(1);
            moves.add(move);
            bulbasaurMoves.moveToNext();
        }

        SparseArray<String> listOfMoves = db.getListOfMoves();

        for(int i = 0; i < moves.size(); i++) {
            String s = listOfMoves.get(moves.get(i)[0]);
            System.out.println(s);
        }

        int x= 3;


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        bulbasaurMoves.close();
    }

    private void showMoves(int id) {

        String s = PokemonData.getInstance().getApiData().get(id).name;
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
        String title = "Tutor moves: \n";
        String tutorMoveString = title;
        String eggMoveString = "Egg Moves: \n";
        String levelupString = "Level up: \n";
        List<PokemonMove> moves = PokemonData.getInstance().getApiData().get(id).moves;

        TreeMap<Integer, String> levelMoves = new TreeMap<>();

        for(PokemonMove m : moves) {
            if(m.learnType.equals("tutor"))
                tutorMoveString += (m.name + "\n");
            if(m.learnType.equals("egg move"))
                eggMoveString += (m.name + "\n");


            if(m.learnType.equals("level up")) {
                levelMoves.put(m.level,m.name);
            }
        }

        for(TreeMap.Entry<Integer, String> entry : levelMoves.entrySet()) {
            levelupString += Integer.toString(entry.getKey()) + " " + entry.getValue() + "\n";
        }


        movesText.setText(tutorMoveString + "\n" + eggMoveString + "\n" + levelupString);
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
        String iconId = "pkmn_big_" + String.format("%03d", id);
        icon.setImageResource(getResources().getIdentifier(iconId, "drawable", MoveDexActivity.this.getPackageName()));
        showMoves(id);
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
}
