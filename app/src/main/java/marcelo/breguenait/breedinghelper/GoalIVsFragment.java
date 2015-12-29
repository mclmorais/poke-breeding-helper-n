package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

//TODO: Make checks on the ability spinner when switching pokemon (right now OK - switches to the first)
// (put it always on the same slot or on the first if it doesn't have the previous slot)

public class GoalIVsFragment extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener {

    @Bind({R.id.checkBoxGoalHP,
            R.id.checkBoxGoalATK,
            R.id.checkBoxGoalDEF,
            R.id.checkBoxGoalSATK,
            R.id.checkBoxGoalSDEF,
            R.id.checkBoxGoalSPD})
    CheckBox[] goalIVs;

    @Bind(R.id.frameLayoutPokemonSelectorButton)
    View buttonPokemonSelector;
    @Bind(R.id.imageViewSelectedPokemonIcon)
    ImageView selectedIcon;
    @Bind(R.id.spinnerGoalIVsNatures)
    Spinner spinnerNature;
    @Bind(R.id.spinnerGoalIVsAbilities)
    Spinner spinnerAbility;
    @Bind(R.id.checkBoxGoalIVsActivateNatures)
    CheckBox checkBoxActivateNatures;
    @Bind(R.id.checkBoxGoalIVsActivateAbilities)
    CheckBox checkBoxActivateAbilities;
    @Bind(R.id.textViewPokemonName)
    TextView selectedName;
    private OnGoalUpdate mCallback;
    private FeedDataGoalIVs feederCallback;
    private UpdateGoal      updaterCallback;
    private ArrayList<Integer> abilitySlots;
    private ArrayList<Integer> abilityIds;

    private final AdapterView.OnItemSelectedListener updateGoalNatureOnSeletion = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCallback.getGoal().id > 0)
                mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
            onInterfaceGoalNatureChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (mCallback.getGoal().id > 0)
                mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
            onInterfaceGoalNatureChanged();
        }
    };

    private final AdapterView.OnItemSelectedListener updateGoalAbilityOnSeletion = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mCallback.getGoal().id > 0)
                mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
            onInterfaceGoalAbilityChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            if (mCallback.getGoal().id > 0)
                mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
            onInterfaceGoalAbilityChanged();
        }
    };

    private final View.OnClickListener updateGoalOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallback.updateGoal(buildGoalPokemon(mCallback.getGoal().id));
            onInterfaceGoalIVsChanged();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                mCallback = (OnGoalUpdate) getActivity();
            else
                mCallback = (OnGoalUpdate) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnGoalUpdate!");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataGoalIVs) getActivity();
            else
                feederCallback = (FeedDataGoalIVs) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement FeedDataGoalIVs!");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterCallback = (UpdateGoal) getActivity();
            else
                updaterCallback = (UpdateGoal) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement UpdateGoal!");
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

        ButterKnife.bind(this, view);

        for (CheckBox goalIV : goalIVs) goalIV.setOnClickListener(updateGoalOnClick);

        buttonPokemonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });

        spinnerNature.setOnItemSelectedListener(updateGoalNatureOnSeletion);
        spinnerAbility.setOnItemSelectedListener(updateGoalAbilityOnSeletion);

//        checkBoxActivateNatures = (CheckBox) view.findViewById(R.id.checkBoxGoalIVsActivateNatures);
        checkBoxActivateNatures.setChecked(mCallback.getConsiderNatureStatus());
        checkBoxActivateNatures.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCallback.updateNatureStatus(isChecked);
                String s = isChecked ? "considered" : "ignored";
                Toast.makeText(getActivity().getApplicationContext(), "Nature " + s + ".", Toast.LENGTH_SHORT).show();
            }
        });

        //  checkBoxActivateAbilities = (CheckBox) view.findViewById(R.id.checkBoxGoalIVsActivateAbilities);
        checkBoxActivateAbilities.setChecked(mCallback.getConsiderAbilityStatus());
        checkBoxActivateAbilities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCallback.updateAbilityStatus(isChecked);
                String s = isChecked ? "considered" : "ignored";
                Toast.makeText(getActivity().getApplicationContext(), "Ability " + s + ".", Toast.LENGTH_SHORT).show();
            }
        });

        initialize();
        return view;
    }

    private void initialize() {
        PokemonInfo goal = mCallback.getGoal();
        if (goal == null) return;

        populateNatureSpinner();
        populateAbilitySpinner(goal.id);
        updateInterfacePokemon(goal);
    }

    private void onInterfaceGoalNatureChanged() {

        int natureId = spinnerNature.getSelectedItemPosition()+1;

        updaterCallback.updateGoalNature(natureId);
    }

    private void onInterfaceGoalAbilityChanged() {

        int spinnerPosition = spinnerAbility.getSelectedItemPosition();

        int abilitySlot = abilitySlots.get(spinnerPosition);

        updaterCallback.updateGoalAbilitySlot(abilitySlot);

    }

    private void onInterfaceGoalIVsChanged() {

        int[] IVs = new int[6];
        for (int i = 0; i < 6; i++) {
            IVs[i] = goalIVs[i].isChecked() ? 1 : 0;
        }

        updaterCallback.updateGoalIVs(IVs);
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
        for (int i = 0; i < 6; i++) {
            IVs[i] = goalIVs[i].isChecked() ? 1 : 0;
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

        updaterCallback.updateGoalId(id);

        PokemonInfo newGoal = buildGoalPokemon(id); //TODO: remover
        mCallback.updateGoal(newGoal);
        updateInterfacePokemon(newGoal);
    }

    private void updateInterfacePokemon(PokemonInfo goal) throws IllegalArgumentException {

        if (goal == null) return;
        if (goal.id <= 0) return;

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
        ArrayList<String> natureNames = feederCallback.getListOfNatures();
        spinnerNature.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, natureNames));
    }

    /**
     * Populates the Ability spinner with all the available abilities of the selected Pokémon.
     *
     * @param pokemonId The Id of the selected Pokémon.
     */
    private void populateAbilitySpinner(int pokemonId) {
        if (spinnerAbility == null) return;

        HashMap<Integer, String> abilities = feederCallback.getListOfAbilities();

        ArrayList<String> abilityStrings = new ArrayList<>();
        abilityIds = new ArrayList<>(); //TODO: remove
        abilitySlots = new ArrayList<>();

        if(abilities.containsKey(1)) {
            abilityStrings.add(abilities.get(1));
            abilitySlots.add(1);
        }
        if(abilities.containsKey(2)) {
            abilityStrings.add(abilities.get(2));
            abilitySlots.add(2);
        }
        if(abilities.containsKey(3)) {
            abilityStrings.add(abilities.get(3) + " (Hidden)");
            abilitySlots.add(3);
        }



        //XXXX TO BE REMOVED
        if (pokemonId != 0) {
            abilityIds.add(PokemonData.getInstance().getFirstAbilityId(pokemonId));

            int s = PokemonData.getInstance().getSecondAbilityId(pokemonId);
            if (s > 0)
                abilityIds.add(s);

            s = PokemonData.getInstance().getHiddenAbilityId(pokemonId);
            if (s > 0)
                abilityIds.add(s);
        } else {
            abilityIds.add(0);
        }


        spinnerAbility.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, abilityStrings));
    }

    /**
     * Updates the selected nature on the Nature spinner.
     *
     * @param nature The nature that will be set as the selection on the spinner.
     */
    private void updateNatureSpinnerSelection(Nature nature) {
        spinnerNature.setSelection(getIndex(spinnerNature, PokemonData.getInstance().getNatureName(nature.ordinal())));
    }

    /**
     * Updates the state of the IV checkboxes
     *
     * @param IVs The values that the checkboxes will be set to
     */
    private void updateIVCheckboxes(int[] IVs) {
        for (int i = 0; i < 6; i++) {
            goalIVs[i].setChecked(IVs[i] == 1);
        }
    }

    /**
     * Given an ID, updates the textView with the appropriate Pokémon name
     *
     * @param id the ID of the Pokémon to be used
     */
    private void updateDisplayedName(int id) {
        String name = PokemonData.getInstance().getName(id);
        selectedName.setText(name);
    }

    /**
     * Given an ID, updates the imageView with the appropriate Pokémon icon
     *
     * @param id the ID of the Pokémon to be used
     */
    private void updateDisplayedIcon(int id) {
        String iconId = "pkmn_big_" + String.format("%03d", id);
        selectedIcon.setBackgroundResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));
    }

    /**
     * Updates the selected ability on the Ability spinner.
     *
     * @param abilityId Ability that will be set as selected.
     * @param pokemonId Id of the current Pokémon that will be used to check if the ability is hidden for it.
     */
    private void updateAbilitySpinnerSelection(int abilityId, int pokemonId) {
        String abilityName = PokemonData.getInstance().getAbilityName(abilityId);

        if (abilityId == PokemonData.getInstance().getHiddenAbilityId(pokemonId))
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

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
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

    interface OnGoalUpdate {
        void updateGoal(PokemonInfo p);

        void updateNatureStatus(boolean b);

        void updateAbilityStatus(boolean b);

        boolean getConsiderNatureStatus();

        boolean getConsiderAbilityStatus();

        PokemonInfo getGoal();
    }

    interface FeedDataGoalIVs {
        ArrayList<String> getListOfNatures();
        HashMap<Integer,String> getListOfAbilities();
    }

    interface UpdateGoal {
        void updateGoalId(int id);
        void updateGoalNature(int natureId);
        void updateGoalAbilitySlot(int abilitySlot);
        void updateGoalIVs(int[] IVs);
    }


}
