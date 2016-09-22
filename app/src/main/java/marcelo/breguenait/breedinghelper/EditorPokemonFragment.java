package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import databasemanager.DatabaseConstants;


public class EditorPokemonFragment extends PopupDialogFragment implements
        SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon {

    protected final CheckBox[] checkBoxInputIVs = new CheckBox[6];
    protected Button confirmButton;
    protected Spinner spinnerNature, spinnerAbility;
    protected ArrayList<InterfaceNature> interfaceNatures;
    protected ArrayList<InterfaceAbility> interfaceAbilities;
    int selectedPokemonId = -1;
    int selectedGenderId = 2;
    int selectedNatureId = -1;
    int selectedAbilitySlot = -1;
    private final Spinner.OnItemSelectedListener onSpinnerItemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent == spinnerNature) {
                if (spinnerNature.getTag() != null && !spinnerNature.getTag().equals(position)) {
                    spinnerNature.setTag(-1);
                    InterfaceNature interfaceNature = (InterfaceNature) spinnerNature.getSelectedItem();
                    selectedNatureId = interfaceNature.id;
                }
            } else if (parent == spinnerAbility) {
                if (spinnerAbility.getTag() != null && !spinnerAbility.getTag().equals(position)) {
                    spinnerAbility.setTag(-1);
                    selectedAbilitySlot = interfaceAbilities.get(position).abilitySlot;
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private TextView selectedName;
    private ImageView selectedIcon;
    private ToggleButton togglePokemonGender;
    private Button cancelButton;
    private FloatingActionButton buttonEdit;
    private View buttonPokemonSelector;
    private boolean showOnlyCompatible;
    private FeedDataCreatePokemon feederCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataCreatePokemon) activity;
            else
                feederCallback = (FeedDataCreatePokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeedDataCreatePokemon");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_edit_pokemon, container, false);


        buttonPokemonSelector = view.findViewById(R.id.buttonSelectPokemon);

        selectedName = (TextView) view.findViewById(R.id.textViewSelectedName);
        selectedIcon = (ImageView) view.findViewById(R.id.imageViewSelectedIcon);

        togglePokemonGender = (ToggleButton) view.findViewById(R.id.toggleButtonInputGender);

        confirmButton = (Button) view.findViewById(R.id.confirmIVsButton);
        cancelButton = (Button) view.findViewById(R.id.fragmentAddPokemonButtonCancel);

        checkBoxInputIVs[0] = (CheckBox) view.findViewById(R.id.checkBoxInputHP);
        checkBoxInputIVs[1] = (CheckBox) view.findViewById(R.id.checkBoxInputATK);
        checkBoxInputIVs[2] = (CheckBox) view.findViewById(R.id.checkBoxInputDEF);
        checkBoxInputIVs[3] = (CheckBox) view.findViewById(R.id.checkBoxInputSATK);
        checkBoxInputIVs[4] = (CheckBox) view.findViewById(R.id.checkBoxInputSDEF);
        checkBoxInputIVs[5] = (CheckBox) view.findViewById(R.id.checkBoxInputSPD);

        spinnerNature = (Spinner) view.findViewById(R.id.spinnerAddPokemonNature);
        spinnerNature.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        spinnerAbility = (Spinner) view.findViewById(R.id.spinnerAddPokemonAbility);
        spinnerAbility.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        buttonEdit = (FloatingActionButton) view.findViewById(R.id.buttonAddPokemonEdit);


        showOnlyCompatible = getArguments().getBoolean("showOnlyCompatible", false);
        //setGenderDisplay();
        setListeners();

//        int receivedId = getArguments().getInt("defaultPokemon", 0);
//        if (receivedId != 0) {
//            updateInterfacePokemon(receivedId);
//            updateInterfaceGender(receivedId);
//            feedAbilities(receivedId);
//        }


        updateNameButton();

        setDialogPosition();

        initialize();
        feedInterface();
        return view;
    }

    void setListeners() {

        buttonPokemonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectPokemonFragment(view);
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSelectPokemonFragment(view);
            }
        });

//        confirmButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finishFragment();
//            }
//        });

        togglePokemonGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedGenderId = isChecked ? DatabaseConstants.MALE_ID : DatabaseConstants.FEMALE_ID;
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
    }

    void initialize() {
        populateNatureSpinner();
    }

    void populateNatureSpinner() {
        interfaceNatures = feederCallback.getInterfaceNatures();
        spinnerNature.setAdapter(new NatureSpinnerAdapter(interfaceNatures, getContext()));
    }

    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        b.putBoolean("showOnlyCompatible", showOnlyCompatible);
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

    void showToast(String string) {
        Toast.makeText(getActivity().getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void setDialogPosition() {
        if (getArguments() == null) {
            return;
        }

        int sourceY = getArguments().getInt("y");

        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        WindowManager.LayoutParams params = window.getAttributes();

        params.y = sourceY - dpToPx(24);

        window.setAttributes(params);
    }

    @Override
    public boolean showEggGroupFilter() {
        return true;
    }

    @Override
    public boolean showOnlyBasic() {
        return false;
    }

    @Override
    public void onPokemonSelected(int id) {
        selectedPokemonId = id;
        feedInterface();
    }

    void feedInterface() {
        feedAbilities(selectedPokemonId);
        updateInterfacePokemon(selectedPokemonId);
        updateInterfaceGender(selectedPokemonId);
        updateNameButton();
    }

    void feedAbilities(int id) {
        if (spinnerAbility == null) return;


        interfaceAbilities = new ArrayList<>();

        LinkedHashMap<Integer, String> abilities = feederCallback.getListOfAbilities(id);

        //Transforms the slot -> name HashMap into a InterfaceAbility to be used as a list
        for (HashMap.Entry<Integer, String> entry : abilities.entrySet())
            interfaceAbilities.add(new InterfaceAbility(entry.getValue(), entry.getKey()));

//        if (selectedPokemonId < 0) {
//            abilities = new HashMap<>();
//            abilities.put(-1, "No Ability");
//        } else {
//            abilities = feederCallback.getListOfAbilitiesNames(id);
//        }
//        abilityIds = new ArrayList<>();
//        abilitySlots = new ArrayList<>();
//
//        ArrayList<String> abilityStrings = new ArrayList<>();
//
//        if (abilities.containsKey(1)) {
//            abilityStrings.add(abilities.get(1));
//            abilitySlots.add(1);
//        }
//        if (abilities.containsKey(2)) {
//            abilityStrings.add(abilities.get(2));
//            abilitySlots.add(2);
//        }
//        if (abilities.containsKey(3)) {
//            abilityStrings.add(abilities.get(3) + " (Hidden)");
//            abilitySlots.add(3);
//        }


//        if (id != 0) {
//
//            String s = PokemonData.getInstance().getFirstAbility(id);
//            int d = PokemonData.getInstance().getFirstAbilityId(id);
//
//            abilityIds.add(d);
//
//            d = PokemonData.getInstance().getSecondAbilityId(id);
//            if (d != -1) {
//                s = PokemonData.getInstance().getSecondAbility(id);
//                abilityIds.add(d);
//            }
//
//            d = PokemonData.getInstance().getHiddenAbilityId(id);
//            if (d != -1) {
//                s = PokemonData.getInstance().getHiddenAbility(id) + " (Hidden)";
//                abilityIds.add(d);
//            }
//        }

        spinnerAbility.setAdapter(new GoalPokemonFragment.AbilitySpinnerAdapter(interfaceAbilities, getContext()));
        //spinnerAbility.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, abilityStrings));

    }

    void updateInterfaceGender(int pokemonId) {

        if (pokemonId <= 0) return;

        int genderRate = feederCallback.getGenderRate(pokemonId);


        if (genderRate == DatabaseConstants.GENDERLESS_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_genderless);
            togglePokemonGender.setClickable(false);
            selectedGenderId = DatabaseConstants.GENDERLESS_ID;
        } else if (genderRate == DatabaseConstants.MALE_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_male);
            togglePokemonGender.setClickable(false);
            selectedGenderId = DatabaseConstants.MALE_ID;
        } else if (genderRate == DatabaseConstants.FEMALE_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_female);
            togglePokemonGender.setClickable(false);
            selectedGenderId = DatabaseConstants.FEMALE_ID;
        } else {
            togglePokemonGender.setBackgroundResource(R.drawable.ic_toggle_gender_selector);
            togglePokemonGender.setClickable(true);
            togglePokemonGender.setChecked(selectedGenderId == DatabaseConstants.MALE_ID);
        }
        togglePokemonGender.invalidate();
    }

    void updateInterfacePokemon(int id) {


        if (DatabaseConstants.pokemonIdIsValid(id)) {
            String name = feederCallback.getPokemonName(id);
            selectedName.setText(name);

            String iconId = "pkmn_big_" + String.format("%03d", id);
            selectedIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));
        } else {
            selectedName.setText(R.string.label_pokemon_missing);
            selectedIcon.setImageResource(R.drawable.pkmn_big_000);
        }
    }

    void updateNameButton() {
        if (DatabaseConstants.pokemonIdIsValid(selectedPokemonId)) {
            buttonPokemonSelector.setVisibility(View.INVISIBLE);
            selectedName.setVisibility(View.VISIBLE);
            buttonEdit.setVisibility(View.VISIBLE);
        } else {
            buttonPokemonSelector.setVisibility(View.VISIBLE);
            selectedName.setVisibility(View.INVISIBLE);
            buttonEdit.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return feederCallback.getCompatiblePokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return feederCallback.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return feederCallback.getPokemonNames();
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return new ArrayList<>(0); //This fragment will never ask for basic only pokemon, so there is just a dummy call
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return feederCallback.getPokemonFamilyList();
    }

    public interface FeedDataCreatePokemon {

        LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId);

        int getGenderRate(int pokemonId);

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        String getPokemonName(int pokemonId);

        ArrayList<Integer> getPokemonFamilyList();

        ArrayList<InterfaceNature> getInterfaceNatures();
    }
}
