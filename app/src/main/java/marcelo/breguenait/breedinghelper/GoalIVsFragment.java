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


class InterfaceGoalPokemon {

    int[] IVs;
    int pokemonId;
    String pokemonName;
    int natureId;
    int abilitySlot;

    public InterfaceGoalPokemon(int[] IVs, int pokemonId, String pokemonName, int natureId, int abilitySlot) {
        this.IVs = IVs;
        this.pokemonId = pokemonId;
        this.pokemonName = pokemonName;
        this.natureId = natureId;
        this.abilitySlot = abilitySlot;
    }

    public int[] getIVs() {
        return IVs;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public int getNatureId() {
        return natureId;
    }

    public int getAbilitySlot() {
        return abilitySlot;
    }
}

public class GoalIVsFragment extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon {

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
    private UpdateGoal updaterCallback;
    private final AdapterView.OnItemSelectedListener updateGoalNatureOnSeletion = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (spinnerNature.getTag() != position) {//TODO: TESTAR!!!!!!!!!!!!!!!!!!
                spinnerNature.setTag(-1);
                onInterfaceGoalNatureChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            onInterfaceGoalNatureChanged();
        }
    };
    private final View.OnClickListener updateGoalOnIVCheckboxChange = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onInterfaceGoalIVsChanged();
        }
    };

    private ArrayList<Integer> abilitySlots;
    private final AdapterView.OnItemSelectedListener updateGoalAbilityOnSeletion = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (spinnerAbility.getTag() != position) {
                spinnerAbility.setTag(-1);
                onInterfaceGoalAbilityChanged();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            onInterfaceGoalAbilityChanged();
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

        for (CheckBox goalIV : goalIVs) goalIV.setOnClickListener(updateGoalOnIVCheckboxChange);

        buttonPokemonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });

        spinnerNature.setOnItemSelectedListener(updateGoalNatureOnSeletion);
        spinnerAbility.setOnItemSelectedListener(updateGoalAbilityOnSeletion);

        checkBoxActivateNatures.setChecked(feederCallback.getConsiderNatureStatus());
        checkBoxActivateNatures.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updaterCallback.updateNatureStatus(isChecked);
                String s = isChecked ? "considered" : "ignored";
                Toast.makeText(getActivity().getApplicationContext(), "Nature " + s + ".", Toast.LENGTH_SHORT).show();
            }
        });

        checkBoxActivateAbilities.setChecked(feederCallback.getConsiderAbilityStatus());
        checkBoxActivateAbilities.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updaterCallback.updateAbilityStatus(isChecked);
                String s = isChecked ? "considered" : "ignored";
                Toast.makeText(getActivity().getApplicationContext(), "Ability " + s + ".", Toast.LENGTH_SHORT).show();
            }
        });

        initialize();
        return view;
    }

    private void initialize() {

        //Populates the Nature spinner
        populateNatureSpinner();

        updateInterfacePokemon();
    }

    private void onInterfaceGoalNatureChanged() {

        int natureId = spinnerNature.getSelectedItemPosition() + 1;

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

    @Override
    public void onPokemonSelected(int id) {

        updaterCallback.updateGoalId(id);

        updateInterfacePokemon();
    }

    private void updateInterfacePokemon() {

        //Gets data from the current goal pokemon
        //(stored previously to the creation of this fragment)
        InterfaceGoalPokemon interfaceGoalPokemon = feederCallback.getInterfaceGoalPokemon();

        //Sets the IV checkboxes according to the received data
        //TODO: Onclick makes it so this doesn't trigger right now, try tag method later
        updateIVCheckboxes(interfaceGoalPokemon.getIVs());

        //Sets the name and icon of the goal pokemon according to the received data
        updateDisplayedName(interfaceGoalPokemon.getPokemonName());
        updateDisplayedIcon(interfaceGoalPokemon.getPokemonId());

        //Sets the position of the spinner according to the received data
        //TODO: this can't trigger the listener or it will be redundantly writing something that it just read
        updateNatureSpinnerSelection(interfaceGoalPokemon.getNatureId());

        //Populates the ability spinner with data of the current goal pokemon (makes its own callback)
        populateAbilitySpinner();

        //Sets the position of the ability spinner according to the received data
        //TODO: this can't trigger the listener or it will be redundantly writing something that it just read
        updateAbilitySpinnerSelection(interfaceGoalPokemon.getAbilitySlot());
    }

    /**
     * Populates the Nature spinner with all possible natures.
     */
    private void populateNatureSpinner() {
        ArrayList<String> natureNames = feederCallback.getListOfNatures();
        spinnerNature.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, natureNames));
    }

    private void populateAbilitySpinner() {
        if (spinnerAbility == null) return;

        HashMap<Integer, String> abilities = feederCallback.getListOfGoalAbilities();

        ArrayList<String> abilityStrings = new ArrayList<>();
        abilitySlots = new ArrayList<>();

        if (abilities.containsKey(1)) {
            abilityStrings.add(abilities.get(1));
            abilitySlots.add(1);
        }
        if (abilities.containsKey(2)) {
            abilityStrings.add(abilities.get(2));
            abilitySlots.add(2);
        }
        if (abilities.containsKey(3)) {
            abilityStrings.add(abilities.get(3) + " (Hidden)");
            abilitySlots.add(3);
        }

        spinnerAbility.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, abilityStrings));
    }

    private void updateNatureSpinnerSelection(int natureId) {

        if(natureId > 0) {
            spinnerNature.setTag(natureId - 1);
            spinnerNature.setSelection(natureId - 1);
        }
        //spinnerNature.setSelection(getIndex(spinnerNature, PokemonData.getInstance().getNatureName(nature.ordinal())));
    }

    /**
     * Updates the state of the IV checkboxes
     *
     * @param IVs The values that the checkboxes will be set to
     */
    private void updateIVCheckboxes(int[] IVs) {

        //If not initialized (-1): set as unchecked

        for (int i = 0; i < 6; i++) {
            goalIVs[i].setChecked(IVs[i] == 1);
        }
    }

    private void updateDisplayedName(String name) {

        //If not initialized (""): doesn't update

        if(!name.equals(""))
            selectedName.setText(name);
    }

    /**
     * Given an ID, updates the imageView with the appropriate Pokémon icon
     *
     * @param id the ID of the Pokémon to be used
     */
    private void updateDisplayedIcon(int id) {


        //If not initialized (-1): doesn't update

        if(id > 0) { //TODO: fazer 0 < x < limite
            String iconId = "pkmn_big_" + String.format("%03d", id);
            selectedIcon.setBackgroundResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));
        }
    }

    private void updateAbilitySpinnerSelection(int abilitySlot) {
        if(abilitySlot > 0) {

            //TODO: mudar esse abilityslots q ta meio merda
            int position = 0;
            for (int i = 0; i < abilitySlots.size(); i++) {
                if(abilitySlots.get(i) == abilitySlot) {
                    position = i;
                    break;
                }
            }
            spinnerAbility.setTag(position);
            spinnerAbility.setSelection(position);
        }
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


    private Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return new ArrayList<>(0); //Dummy call, this fragment doesn't need to feed this info
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return feederCallback.getBasicPokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return feederCallback.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return feederCallback.getPokemonNames();
    }




    interface OnGoalUpdate {



    }

    interface FeedDataGoalIVs {
        ArrayList<String> getListOfNatures();

        HashMap<Integer, String> getListOfGoalAbilities();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        InterfaceGoalPokemon getInterfaceGoalPokemon();

        ArrayList<Integer> getBasicPokemonList();

        boolean getConsiderNatureStatus();
        boolean getConsiderAbilityStatus();
    }

    interface UpdateGoal {
        void updateGoalId(int id);

        void updateGoalNature(int natureId);

        void updateGoalAbilitySlot(int abilitySlot);

        void updateGoalIVs(int[] IVs);

        void updateAbilityStatus(boolean b);
        void updateNatureStatus(boolean b);


    }
}
