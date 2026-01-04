package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import breedingmanager.NatureManager;
import databasemanager.DatabaseConstants;
import marcelo.breguenait.breedinghelper.R;


public class EditorPokemonFragment extends PopupDialogFragment implements
        SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon {

    protected final CheckBox[] checkBoxInputIVs = new CheckBox[6];
    protected Button confirmButton;
    protected Spinner spinnerNature, spinnerAbility;
    protected ArrayList<NatureManager.NatureVerbose> natureVerboses;
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
                    NatureManager.NatureVerbose natureVerbose = (NatureManager.NatureVerbose) spinnerNature.getSelectedItem();
                    selectedNatureId = natureVerbose.id;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            Fragment targetFragment = getParentFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataCreatePokemon) context;
            else
                feederCallback = (FeedDataCreatePokemon) targetFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement FeedDataCreatePokemon");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(requireActivity(), R.style.AppTheme);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_edit_pokemon, container, false);


        buttonPokemonSelector = view.findViewById(R.id.buttonSelectPokemon);

        selectedName = view.findViewById(R.id.textViewSelectedName);
        selectedIcon = view.findViewById(R.id.imageViewSelectedIcon);

        togglePokemonGender = view.findViewById(R.id.toggleButtonInputGender);

        confirmButton = view.findViewById(R.id.confirmIVsButton);
        cancelButton = view.findViewById(R.id.fragmentAddPokemonButtonCancel);

        checkBoxInputIVs[0] = view.findViewById(R.id.checkBoxInputHP);
        checkBoxInputIVs[1] = view.findViewById(R.id.checkBoxInputATK);
        checkBoxInputIVs[2] = view.findViewById(R.id.checkBoxInputDEF);
        checkBoxInputIVs[3] = view.findViewById(R.id.checkBoxInputSATK);
        checkBoxInputIVs[4] = view.findViewById(R.id.checkBoxInputSDEF);
        checkBoxInputIVs[5] = view.findViewById(R.id.checkBoxInputSPD);

        spinnerNature = view.findViewById(R.id.spinnerAddPokemonNature);
        spinnerNature.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        spinnerAbility = view.findViewById(R.id.spinnerAddPokemonAbility);
        spinnerAbility.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        buttonEdit = view.findViewById(R.id.buttonAddPokemonEdit);

        if(getArguments() != null) {
            showOnlyCompatible = getArguments().getBoolean("showOnlyCompatible", false);
        }
        setListeners();

        updateNameButton();

        setDialogPosition();

        initialize();
        feedInterface();
        return view;
    }

    void setListeners() {

        buttonPokemonSelector.setOnClickListener(this::openSelectPokemonFragment);

        buttonEdit.setOnClickListener(this::openSelectPokemonFragment);

        togglePokemonGender.setOnCheckedChangeListener((buttonView, isChecked) -> selectedGenderId = isChecked ? DatabaseConstants.MALE_ID : DatabaseConstants.FEMALE_ID);

        cancelButton.setOnClickListener(v -> closeFragment());
    }

    void initialize() {
        populateNatureSpinner();
    }

    void populateNatureSpinner() {
        natureVerboses = feederCallback.getInterfaceNatures();
        spinnerNature.setAdapter(new NatureSpinnerAdapter(natureVerboses, getContext()));
    }

    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getParentFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        b.putBoolean("showOnlyCompatible", showOnlyCompatible);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.show(fm, "SelectPokemonFragment");
    }

    Bundle addPositionAsArguments(View v) {
        int[] callerViewPosition = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }

    void showToast(String string) {
        Toast.makeText(requireActivity().getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void setDialogPosition() {
        if (getArguments() == null || getDialog() == null || getDialog().getWindow() == null) {
            return;
        }

        int sourceY = getArguments().getInt("y");

        Window window = getDialog().getWindow();

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

        for (HashMap.Entry<Integer, String> entry : abilities.entrySet())
            interfaceAbilities.add(new InterfaceAbility(entry.getValue(), entry.getKey()));

        ArrayList<marcelo.breguenait.breedinghelper.InterfaceAbility> sharedAbilities = new ArrayList<>();
        for (EditorPokemonFragment.InterfaceAbility ability : interfaceAbilities) {
            sharedAbilities.add(new marcelo.breguenait.breedinghelper.InterfaceAbility(ability.abilityName, ability.abilitySlot));
        }
        spinnerAbility.setAdapter(new GoalPokemonFragment.AbilitySpinnerAdapter(sharedAbilities, getContext()));

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
            selectedIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", requireActivity().getPackageName()));
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

        ArrayList<NatureManager.NatureVerbose> getInterfaceNatures();
    }
    public static class InterfaceAbility {
        public String abilityName;
        public int abilitySlot;

        InterfaceAbility(String abilityName, int abilitySlot) {
            this.abilityName = abilityName;
            this.abilitySlot = abilitySlot;
        }
    }
}
