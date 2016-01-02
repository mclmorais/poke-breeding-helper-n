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
import android.widget.ArrayAdapter;
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

public class EditorPokemonFragment extends PopupDialogFragment implements
        SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon {


    public static int FEMALE = 1;
    public static int MALE = 2;
    public static int GENDERLESS = 3;
    public static int GENDERLESS_ONLY = -1;
    public static int MALE_ONLY = 0;


    //--------------------------------------------
    public static int FEMALE_ONLY = 8;
    //-----------
    protected final CheckBox[] checkBoxInputIVs = new CheckBox[6];

    int selectedPokemonId = -1;
    int selectedGenderId = 2;
    int[] selectedIVs = {-1, -1, -1, -1, -1, -1};
    int selectedNatureId = -1;
    int selectedAbilitySlot = -1;

    private TextView selectedName;
    private ImageView selectedIcon;

    private ToggleButton togglePokemonGender;

    protected Button confirmButton;
    private Button cancelButton;

    private FloatingActionButton buttonEdit;
    private View buttonPokemonSelector;

    private boolean showOnlyCompatible;
    protected Spinner spinnerNature, spinnerAbility;
    protected ArrayList<Integer> abilityIds;
    protected ArrayList<Integer> abilitySlots;

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
        setRetainInstance(true);
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

        spinnerAbility = (Spinner) view.findViewById(R.id.spinnerAddPokemonAbility);

        buttonEdit = (FloatingActionButton) view.findViewById(R.id.buttonAddPokemonEdit);


        showOnlyCompatible = getArguments().getBoolean("showOnlyCompatible", false);
        //setGenderDisplay();
        setListeners();

//        int receivedId = getArguments().getInt("defaultPokemon", 0);
//        if (receivedId != 0) {
//            updateInterfacePokemon(receivedId);
//            updateInterfaceGender(receivedId);
//            updateAbilities(receivedId);
//        }


        updateNameButton();

        setDialogPosition();

        initialize();
        updateInterface();
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
                selectedGenderId = isChecked ? MALE : FEMALE;
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
        ArrayList<String> natureNames = feederCallback.getListOfNatures();
        spinnerNature.setAdapter(new ArrayAdapter<>(
                getActivity().getApplicationContext(),
                R.layout.spinner_item,
                natureNames));
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
    public void onPokemonSelected(int id) {
        selectedPokemonId = id;
        updateInterface();
    }

    void updateInterface() {
        updateAbilities(selectedPokemonId);
        updateInterfacePokemon(selectedPokemonId);
        updateInterfaceGender(selectedPokemonId);
        updateNameButton();

    }

    void updateAbilities(int id) {
        if (spinnerAbility == null) return;

        HashMap<Integer, String> abilities = feederCallback.getListOfAbilities(id);
        abilityIds = new ArrayList<>();
        abilitySlots = new ArrayList<>();

        ArrayList<String> abilityStrings = new ArrayList<>();

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

        spinnerAbility.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, abilityStrings));

    }

    void updateInterfaceGender(int pokemonId) {

        if(pokemonId <= 0) return;

        int genderRate = feederCallback.getGenderRate(pokemonId);


        if (genderRate == GENDERLESS_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_genderless);
            togglePokemonGender.setClickable(false);
            selectedGenderId = GENDERLESS;
        } else if (genderRate == MALE_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_male);
            togglePokemonGender.setClickable(false);
            selectedGenderId = MALE;
        } else if (genderRate == FEMALE_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_female);
            togglePokemonGender.setClickable(false);
            selectedGenderId = FEMALE;
        } else {
            togglePokemonGender.setBackgroundResource(R.drawable.ic_toggle_gender_selector);
            togglePokemonGender.setClickable(true);
            togglePokemonGender.setChecked(selectedGenderId==MALE); //TODO: HMM
        }
        togglePokemonGender.invalidate();
    }

    void updateInterfacePokemon(int id) {
        //selectedPokemonId = id;
        String name = feederCallback.getPokemonName(id);
               selectedName.setText(name);

        if(id >= 0) {
            String iconId = "pkmn_big_" + String.format("%03d", id);
            selectedIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));
        }
    }

    void updateNameButton() {
        if (selectedPokemonId > 0) {
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
        ArrayList<String> getListOfNatures();

        HashMap<Integer, String> getListOfAbilities(int pokemonId);

        int getGenderRate(int pokemonId);

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        String getPokemonName(int pokemonId);

        ArrayList<Integer> getPokemonFamilyList();
    }
}
