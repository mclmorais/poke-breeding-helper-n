package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GoalIVsFragment extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener{

    interface OnGoalUpdate {
        void updateGoal(PokemonInfo p);
        void updateNatureStatus(boolean b);
        boolean getConsiderNatureStatus();
    }

    private OnGoalUpdate mCallback;

    private final CheckBox[] goalIVs = new CheckBox[6];
    private Spinner spinnerNature, spinnerAbility;
    private View buttonPokemonSelector;
    private ImageView selectedIcon;
    private TextView selectedName;
    private CheckBox checkBoxActivateNatures;
    private CheckBox checkBoxActivateAbilities;

    private int selectedPokemonId = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (OnGoalUpdate) activity;
            else
                mCallback = (OnGoalUpdate) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGoalUpdate!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_ivs, container, false);

        goalIVs[0] = (CheckBox) view.findViewById(R.id.checkBoxGoalHP);
        goalIVs[1] = (CheckBox) view.findViewById(R.id.checkBoxGoalATK);
        goalIVs[2] = (CheckBox) view.findViewById(R.id.checkBoxGoalDEF);
        goalIVs[3] = (CheckBox) view.findViewById(R.id.checkBoxGoalSATK);
        goalIVs[4] = (CheckBox) view.findViewById(R.id.checkBoxGoalSDEF);
        goalIVs[5] = (CheckBox) view.findViewById(R.id.checkBoxGoalSPD);

        for(int i = 0; i < 6; i ++) {
            goalIVs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.updateGoal(updateGoalPokemon());
                }
            });

        }

        buttonPokemonSelector = view.findViewById(R.id.frameLayoutPokemonSelectorButton);

        buttonPokemonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });

        selectedIcon = (ImageView) view.findViewById(R.id.imageViewSelectedPokemonIcon);

        selectedName = (TextView) view.findViewById(R.id.textViewPokemonName);

        spinnerNature = (Spinner) view.findViewById(R.id.spinnerGoalIVsNatures);
        spinnerNature.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, Nature.values()));

        spinnerNature.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCallback.updateGoal(updateGoalPokemon());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerAbility = (Spinner) view.findViewById(R.id.spinnerGoalIVsAbilities);
        updateAbilities();

        checkBoxActivateNatures = (CheckBox) view.findViewById(R.id.checkBoxGoalIVsActivateNatures);
        checkBoxActivateNatures.setChecked(mCallback.getConsiderNatureStatus());
        checkBoxActivateNatures.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCallback.updateNatureStatus(isChecked);
                String s = isChecked?"considered":"ignored";
                Toast.makeText(getActivity().getApplicationContext(),"Nature " + s + ".", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    void updateAbilities() {
        if(spinnerAbility == null) return;

        ArrayList<String> abilities = new ArrayList<>();

        if(selectedPokemonId != 0) {
            abilities.add(PokemonData.getInstance().getFirstAbility(selectedPokemonId));

            String s = PokemonData.getInstance().getSecondAbility(selectedPokemonId);
            if(!s.equals("NONE") && !s.isEmpty()) {
                abilities.add(s);
            }
            s = PokemonData.getInstance().getHiddenAbility(selectedPokemonId);
            if(!s.equals("NONE") && !s.isEmpty()) {
                s += " (Hidden)";
                abilities.add(s);
            }
        }


        spinnerAbility.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(),R.layout.spinner_item,abilities));


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        //checkBoxActivateNatures.setChecked(mCallback.getConsiderNatureStatus());
    }

    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.setTargetFragment(this,0);
        selectPokemonFragment.show(fm,"");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }

    void refreshGoal(PokemonInfo goal) {
        if(goal == null) return;
        if(goal.id <= 0) return;
        selectedPokemonId = goal.id;
        updateInterfacePokemon(goal);
        for(int i = 0; i < 6; i++) {
            goalIVs[i].setChecked(goal.IVs[i] == 1);
        }
        updateAbilities();
    }

    PokemonInfo updateGoalPokemon() {
        int[] IVs = new int[6];
        for(int i = 0; i < 6; i++) {
            IVs[i] = goalIVs[i].isChecked()?1:0;
        }

        return new PokemonInfo.Builder()
                .id(selectedPokemonId)
                .gender(Gender.MALE) //TODO: Depois fazer ele poder escolher?
                .IVs(IVs)
                .nature(Nature.valueOf(spinnerNature.getSelectedItem().toString()))
                .build();
        //TODO: fazer nao ficar recriando toda vez
    }

    @Override
    public void onPokemonSelected(int id) {
        selectedPokemonId = id;
        PokemonInfo newGoal = updateGoalPokemon();
        mCallback.updateGoal(newGoal);
        updateInterfacePokemon(newGoal);
        updateAbilities();
    }

    void updateInterfacePokemon(PokemonInfo goal) {
        String name = PokemonData.getInstance().getName(goal.id);
        selectedName.setText(name);
        String iconId = "pkmn_big_" + String.format("%03d", goal.id);
        selectedIcon.setBackgroundResource(getResources().getIdentifier(iconId,"drawable",getActivity().getPackageName()));

        Nature nature = goal.nature;
        if(nature == null) nature = Nature.UNSET;
        spinnerNature.setSelection(getIndex(spinnerNature,nature.toString()));
    }

    @Override
    public boolean showEggGroupFilter() {
        return false;
    }

    @Override
    public boolean showOnlyBasic() {
        return true;
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equals(myString)){
                index = i;
            }
        }
        return index;
    }

    @Override
    public PokemonInfo getGoal() {
        return updateGoalPokemon();
    }
}
