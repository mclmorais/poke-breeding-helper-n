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
        void updateAbilityStatus(boolean b);
        boolean getConsiderNatureStatus();
        boolean getConsiderAbilityStatus();
        PokemonInfo getGoal();
    }

    private OnGoalUpdate mCallback;

    private final CheckBox[] goalIVs = new CheckBox[6];
    private Spinner spinnerNature, spinnerAbility;
    private View buttonPokemonSelector;
    private ImageView selectedIcon;
    private TextView selectedName;
    private CheckBox checkBoxActivateNatures;
    private CheckBox checkBoxActivateAbilities;

    private ArrayList<String> abilityStrings;
    private ArrayList<Integer> abilityIds;

    private final View.OnClickListener updateGoalOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
        }
    };

    private final AdapterView.OnItemSelectedListener updateGoalOnSelection = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if(mCallback.getGoal().id > 0)
                mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
        }
    };


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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
            goalIVs[i].setOnClickListener(updateGoalOnClick);
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
        spinnerAbility = (Spinner) view.findViewById(R.id.spinnerGoalIVsAbilities);


        spinnerNature.setOnItemSelectedListener(updateGoalOnSelection);
        spinnerAbility.setOnItemSelectedListener(updateGoalOnSelection);

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

        checkBoxActivateAbilities = (CheckBox) view.findViewById(R.id.checkBoxGoalIVsActivateAbilities);
        checkBoxActivateAbilities.setChecked(mCallback.getConsiderAbilityStatus());
        checkBoxActivateAbilities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCallback.updateAbilityStatus(isChecked);
                String s = isChecked?"considered":"ignored";
                Toast.makeText(getActivity().getApplicationContext(),"Ability " + s + ".", Toast.LENGTH_SHORT).show();
            }
        });

        initialize();
        return view;
    }

    private void initialize() {
        PokemonInfo goal = mCallback.getGoal();
        if(goal == null) return;

        populateNatureSpinner();
        populateAbilitySpinner(goal.id);
        updateInterfacePokemon(goal);
    }

    private void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.setTargetFragment(this, 0);
        selectPokemonFragment.show(fm, "");
    }

    private PokemonInfo buildGoalPokemon(int id) {


        int[] IVs = new int[6];
        for(int i = 0; i < 6; i++) {
            IVs[i] = goalIVs[i].isChecked()?1:0;
        }

        return new PokemonInfo.Builder()
                .id(id)
                .gender(Gender.MALE) //TODO: Depois fazer ele poder escolher?
                .IVs(IVs)
                .nature(Nature.values()[spinnerNature.getSelectedItemPosition()])
                .ability(abilityIds.get(spinnerAbility.getSelectedItemPosition()))
                .build();
        //TODO: fazer nao ficar recriando toda vez
    }

    @Override
    public void onPokemonSelected(int id) {

        PokemonInfo newGoal = buildGoalPokemon(id);
        mCallback.updateGoal(newGoal);
        updateInterfacePokemon(newGoal);

    }

    private void updateInterfacePokemon(PokemonInfo goal) throws IllegalArgumentException {

        if(goal == null) return;
        if(goal.id <= 0) return;

        updateIVCheckboxes(goal.IVs);

        updateDisplayedName(goal.id);

        updateDisplayedIcon(goal.id);

        updateNatureSpinnerSelection(goal.nature);

        populateAbilitySpinner(goal.id);

        updateAbilitySpinnerSelection(goal.ability, goal.id);
    }

    /**
     * Populates the Nature spinner with all possible natures.
     */
    private void populateNatureSpinner() {
        ArrayList<String> natureNames = PokemonData.getInstance().getListOfNatures();
        spinnerNature.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, natureNames));
    }

    /**
     * Populates the Ability spinner with all the available abilities of the selected Pokémon.
     * @param pokemonId The Id of the selected Pokémon.
     */
    private void populateAbilitySpinner(int pokemonId) {
        if(spinnerAbility == null) return;

        abilityStrings = new ArrayList<>();

        abilityIds = new ArrayList<>();

        if(pokemonId != 0) {
            abilityStrings.add(PokemonData.getInstance().getFirstAbility(pokemonId));

            String s = PokemonData.getInstance().getSecondAbility(pokemonId);
            if(!s.equals("NONE") && !s.isEmpty()) {
                abilityStrings.add(s);
            }
            s = PokemonData.getInstance().getHiddenAbility(pokemonId);
            if(!s.equals("NONE") && !s.isEmpty()) {
                s += " (Hidden)";
                abilityStrings.add(s);
            }
        }
        else {
            abilityStrings.add("Unset");
        }

        if(pokemonId != 0) {
            abilityIds.add(PokemonData.getInstance().getFirstAbilityId(pokemonId));

            int s = PokemonData.getInstance().getSecondAbilityId(pokemonId);
            if(s > 0)
                abilityIds.add(s);

            s = PokemonData.getInstance().getHiddenAbilityId(pokemonId);
            if (s > 0)
                abilityIds.add(s);
        }
        else {
            abilityIds.add(0);
        }


        spinnerAbility.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, abilityStrings));


    }

    /**
     * Updates the selected nature on the Nature spinner.
     * @param nature The nature that will be set as the selection on the spinner.
     */
    private void updateNatureSpinnerSelection(Nature nature) {
        spinnerNature.setSelection(getIndex(spinnerNature, PokemonData.getInstance().getNatureName(nature.ordinal())));
    }

    /**
     * Updates the state of the IV checkboxes
     * @param IVs The values that the checkboxes will be set to
     */
    private void updateIVCheckboxes(int [] IVs) {
        for(int i = 0; i < 6; i++) {
            goalIVs[i].setChecked(IVs[i] == 1);
        }
    }

    /**
     * Given an ID, updates the textView with the appropriate Pokémon name
     * @param id the ID of the Pokémon to be used
     */
    private void updateDisplayedName(int id) {
        String name = PokemonData.getInstance().getName(id);
        selectedName.setText(name);
    }

    /**
     * Given an ID, updates the imageView with the appropriate Pokémon icon
     * @param id the ID of the Pokémon to be used
     */
    private void updateDisplayedIcon(int id) {
        String iconId = "pkmn_big_" + String.format("%03d", id);
        selectedIcon.setBackgroundResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));
    }

    /**
     * Updates the selected ability on the Ability spinner.
     * @param abilityId Ability that will be set as selected.
     * @param pokemonId Id of the current Pokémon that will be used to check if the ability is hidden for it.
     */
    private void updateAbilitySpinnerSelection(int abilityId, int pokemonId) {
        String abilityName = PokemonData.getInstance().getAbilityName(abilityId);

        if(abilityId == PokemonData.getInstance().getHiddenAbilityId(pokemonId))
            abilityName += " (Hidden)";//TODO: mudar para string do sistema

        spinnerAbility.setSelection(getIndex(spinnerAbility, abilityName));

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
        return mCallback.getGoal();
    }

    private Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }


}
