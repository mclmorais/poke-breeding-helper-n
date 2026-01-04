package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import breedingmanager.AbilityManager;
import breedingmanager.MoveManager;
import breedingmanager.MoveVerbose;
import breedingmanager.NatureManager;
import breedingmanager.StorageManager;
import breedingmanager.StoredPokemon;
import databasemanager.DatabaseConstants;
import databasemanager.SqlDatabase;
import marcelo.breguenait.breedinghelper.databinding.ActivityAssistantBinding;

public class AssistantActivity extends AppCompatActivity implements SelectPokemonFragment.OnPokemonSelectedListener, SelectPokemonFragment.FeedDataSelectPokemon {

    private ActivityAssistantBinding binding;
    private final StorageManager storageManager = new StorageManager();
    private final NatureManager natureManager = new NatureManager();
    private final AbilityManager abilityManager = new AbilityManager();
    private final MoveManager moveManager = new MoveManager();
    private final Gson gson = new Gson();

    ArrayList<NatureManager.NatureVerbose> natureVerboses;
    ArrayList<MoveVerbose> moves;
    ArrayList<InterfaceAbility> interfaceAbilities;

    private final CompoundButton.OnCheckedChangeListener onCheckBoxCheckHandler = (buttonView, isChecked) -> {
        for (CheckBox goalIVsCheckBox : getGoalIVs()) {
            if (buttonView == goalIVsCheckBox) {
                updateGoalIVs();
                return;
            }
        }
    };

    private final AdapterView.OnItemSelectedListener onSpinnerItemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Spinner natureSpinner = binding.contentInclude.goalInclude.includeNature.spinnerNature;
            Spinner abilitySpinner = binding.contentInclude.goalInclude.includeAbility.spinnerAbility;
            if (parent == natureSpinner) {
                if (natureSpinner.getTag() == null || !natureSpinner.getTag().equals(position)) {
                    natureSpinner.setTag(-1);
                    updateGoalNature();
                }
            } else if (parent == abilitySpinner) {
                if (abilitySpinner.getTag() == null || !abilitySpinner.getTag().equals(position)) {
                    abilitySpinner.setTag(-1);
                    updateGoalAbility();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssistantBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        readData();

        for (int i = 0; i < getGoalIVs().length; i++) {
            removeRippleEffectFromCheckBox(getGoalIVs()[i]);
            getGoalIVs()[i].setChecked(storageManager.getGoalObject().getIVs()[i] == 1);
            getGoalIVs()[i].setOnCheckedChangeListener(onCheckBoxCheckHandler);
        }

        for (int i = 0; i < getIncludeEggMoves().length; i++) {
            View eggMoveView = getIncludeEggMoves()[i];
            TextView label = eggMoveView.findViewById(R.id.eggMoveLabel);
            label.setText("Egg Move " + (i + 1));
            ImageButton buttonRemoveEggMove = eggMoveView.findViewById(R.id.buttonRemoveEggMove);
            buttonRemoveEggMove.setOnClickListener(v -> {
                    for (View eggMove : getIncludeEggMoves()) {
                    if (v.getParent() == eggMove) {
                        eggMove.setVisibility(View.GONE);
                        binding.contentInclude.goalInclude.buttonAddEggMove.setEnabled(true);
                    }
                }
            });
        }

        feedInterface();
        binding.contentInclude.goalInclude.includeNature.spinnerNature.setOnItemSelectedListener(onSpinnerItemSelectedHandler);
        binding.contentInclude.goalInclude.includeAbility.spinnerAbility.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        setModifierNatureActive(natureManager.getNatureModifier());
        setModifierAbilityActive(abilityManager.getAbilityModifier());

        binding.contentInclude.goalInclude.buttonAddAbility.setOnClickListener(v -> setModifierAbilityActive(true));
        binding.contentInclude.goalInclude.buttonAddNature.setOnClickListener(v -> setModifierNatureActive(true));
        binding.contentInclude.goalInclude.includeAbility.buttonRemoveAbility.setOnClickListener(v -> setModifierAbilityActive(false));
        binding.contentInclude.goalInclude.includeNature.buttonRemoveNature.setOnClickListener(v -> setModifierNatureActive(false));
        binding.contentInclude.goalInclude.buttonSelector.setOnClickListener(this::openSelectPokemonFragment);
        binding.contentInclude.goalInclude.buttonAddEggMove.setOnClickListener(v -> addModifierEggMove());
    }

    private CheckBox[] getGoalIVs() {
        return new CheckBox[]{
            binding.contentInclude.goalInclude.checkBoxGoalHP,
            binding.contentInclude.goalInclude.checkBoxGoalATK,
            binding.contentInclude.goalInclude.checkBoxGoalDEF,
            binding.contentInclude.goalInclude.checkBoxGoalSATK,
            binding.contentInclude.goalInclude.checkBoxGoalSDEF,
            binding.contentInclude.goalInclude.checkBoxGoalSPD
        };
    }

    private View[] getIncludeEggMoves() {
        return new View[]{
            binding.contentInclude.goalInclude.includeEggMove1.getRoot(),
            binding.contentInclude.goalInclude.includeEggMove2.getRoot(),
            binding.contentInclude.goalInclude.includeEggMove3.getRoot(),
            binding.contentInclude.goalInclude.includeEggMove4.getRoot()
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    private void feedInterface() {

        StoredPokemon goalPokemon = storageManager.getGoalObject();
        int pokemonId = goalPokemon.getPokemonId();

        if (DatabaseConstants.pokemonIdIsValid(pokemonId)) {
            feedDisplayedName(storageManager.getPokemonName(pokemonId));
        } else {
            feedDisplayedName("");
        }
        feedDisplayedIcon(pokemonId);

        feedNatureSpinner();
        feedNatureSpinnerSelection(goalPokemon.getNatureId());

        feedAbilitySpinner();
        feedAbilitySpinnerSelection(goalPokemon.getAbilitySlot());

        feedEggMoveSpinners();
    }

    private void feedDisplayedName(String name) {
        if (!name.equals(""))
            binding.contentInclude.goalInclude.textViewPokemonName.setText(name);
    }

    private void feedDisplayedIcon(int id) {

        if (DatabaseConstants.pokemonIdIsValid(id)) { //TODO: fazer 0 < x < limite
            String iconId = "pkmn_big_" + String.format("%03d", id);
            binding.contentInclude.goalInclude.imageViewSelectedPokemonIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getPackageName()));
        }
    }

    private void feedNatureSpinner() {
        natureVerboses = natureManager.getInterfaceNatures();
        binding.contentInclude.goalInclude.includeNature.spinnerNature.setAdapter(new NatureSpinnerAdapter(natureVerboses, this));
    }

    private void feedNatureSpinnerSelection(int natureId) {


        for (int i = 0; i < natureVerboses.size(); i++) {

            if (natureVerboses.get(i).id == natureId) {
                binding.contentInclude.goalInclude.includeNature.spinnerNature.setTag(i);
                binding.contentInclude.goalInclude.includeNature.spinnerNature.setSelection(i);
                return;
            }

        }
        Log.d("GoalFragment", "Received a pokemon with invalid nature");

    }

    private void feedAbilitySpinner() {
        Spinner abilitySpinner = binding.contentInclude.goalInclude.includeAbility.spinnerAbility;
        if (abilitySpinner == null) return;

        int goalId = storageManager.getGoalId();
        if (!DatabaseConstants.pokemonIdIsValid(goalId)) return;

        LinkedHashMap<Integer, String> abilities = abilityManager.getListOfAbilities(goalId);

        interfaceAbilities = new ArrayList<>();

        for (HashMap.Entry<Integer, String> entry : abilities.entrySet())
            interfaceAbilities.add(new InterfaceAbility(entry.getValue(), entry.getKey()));

        abilitySpinner.setAdapter(new GoalPokemonFragment.AbilitySpinnerAdapter(interfaceAbilities, this));
    }

    private void feedAbilitySpinnerSelection(int abilitySlot) {

        if (DatabaseConstants.abilitySlotIsValid(abilitySlot)) {
            int position = -1;
            for (int i = 0; i < interfaceAbilities.size(); i++) {
                if (interfaceAbilities.get(i).abilitySlot == abilitySlot) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                binding.contentInclude.goalInclude.includeAbility.spinnerAbility.setTag(position);
                binding.contentInclude.goalInclude.includeAbility.spinnerAbility.setSelection(position);
            } else {
                Log.d("GOAL", "AbilitySlot " + String.valueOf(abilitySlot) +
                        " wasn\'t found in InterfaceAbilities.");
            }
        }
    }

    private void feedEggMoveSpinners() {
        int goalId = storageManager.getGoalId();
        if (!DatabaseConstants.pokemonIdIsValid(goalId)) return;

        for (View eggMoveView : getIncludeEggMoves()) {
            Spinner eggMoveSpinner = eggMoveView.findViewById(R.id.spinnerEggMove);
            eggMoveSpinner.setAdapter(new EggMoveSpinnerAdapter(moveManager.getEggMoves(goalId), this));
        }


    }

    private void openSelectPokemonFragment(View view) {
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.show(getSupportFragmentManager(), "");
    }

    private void setModifierNatureActive(Boolean choice) {
        binding.contentInclude.goalInclude.includeNature.getRoot().setVisibility(choice ? View.VISIBLE : View.GONE);
        binding.contentInclude.goalInclude.buttonAddNature.setEnabled(!choice);
        natureManager.setNatureModifier(choice);
    }

    private void setModifierAbilityActive(Boolean choice) {
        binding.contentInclude.goalInclude.includeAbility.getRoot().setVisibility(choice ? View.VISIBLE : View.GONE);
        binding.contentInclude.goalInclude.buttonAddAbility.setEnabled(!choice);
        abilityManager.setAbilityModifier(choice);
    }

    private void addModifierEggMove() {
        int total = 0;
        for (View eggMove : getIncludeEggMoves()) {
            total++;
            if (eggMove.getVisibility() == View.GONE) {
                eggMove.setVisibility(View.VISIBLE);
                break;
            }
        }
        if (total >= 4)
            binding.contentInclude.goalInclude.buttonAddEggMove.setEnabled(false);
    }

    private void readData() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String jsonString;


        jsonString = sharedPref.getString("jsonCurrentGoal", null);
        if (jsonString != null) {
            PokemonInfo oldGoal = gson.fromJson(jsonString, PokemonInfo.class);
            if (oldGoal != null) {
                storageManager.replaceGoalObject(convertCompatPokemon(oldGoal));
            }
            sharedPref.edit().remove("jsonCurrentGoal").apply();
        } else {
            jsonString = sharedPref.getString("jsonBreedingManagerGoal", null);
            if (jsonString != null) {
                storageManager.replaceGoalObject(gson.fromJson(jsonString, StoredPokemon.class));
            }
        }

        jsonString = sharedPref.getString("jsonPokemonList", null);
        if (jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>() {
            }.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ArrayList<StoredPokemon> newList = new ArrayList<>(eggList.size());
            for (PokemonInfo pokemonInfo : eggList) {
                if (pokemonInfo != null)
                    newList.add(convertCompatPokemon(pokemonInfo));
            }
            storageManager.replaceStoredPokemonObjects(newList);
            sharedPref.edit().remove("jsonPokemonList").apply();
        } else {
            jsonString = sharedPref.getString("jsonBreedingManagerStoredList", null);
            if (jsonString != null) {
                Type type = new TypeToken<ArrayList<StoredPokemon>>() {
                }.getType();
                ArrayList<StoredPokemon> objectsList = gson.fromJson(jsonString, type);
                storageManager.replaceStoredPokemonObjects(objectsList);
            }
        }


        jsonString = sharedPref.getString("jsonHasEverstone", null);
        if (jsonString != null) {
            storageManager.setEverstone(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderNature", null);
        if (jsonString != null) {
            natureManager.setNatureModifier(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderAbility", null);
        if (jsonString != null) {
            abilityManager.setAbilityModifier(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonMaleItem", null); //POR ENQUANTO ARMAZENA O DESTINY KNOT!
        if (jsonString != null) {
            storageManager.setDestinyKnot(gson.fromJson(jsonString, Boolean.class));
        }

        int gameLanguage = Integer.parseInt(sharedPref.getString("gameLanguage", "9"));
        storageManager.setLanguageId(gameLanguage);

    }

    private void saveData() {

        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

        jsonString = gson.toJson(storageManager.hasEverstone());
        prefEditor.putString("jsonHasEverstone", jsonString);

        jsonString = gson.toJson(natureManager.getNatureModifier());
        prefEditor.putString("jsonConsiderNature", jsonString);

        jsonString = gson.toJson(abilityManager.getAbilityModifier());
        prefEditor.putString("jsonConsiderAbility", jsonString);

        jsonString = gson.toJson(storageManager.hasDestinyKnot());
        prefEditor.putString("jsonMaleItem", jsonString);

        jsonString = gson.toJson(storageManager.getStoredPokemonObjects());
        prefEditor.putString("jsonBreedingManagerStoredList", jsonString);

        jsonString = gson.toJson(storageManager.getGoalObject());
        prefEditor.putString("jsonBreedingManagerGoal", jsonString);

        prefEditor.apply();


    }

    private StoredPokemon convertCompatPokemon(final PokemonInfo compatPokemon) {


        int pokemonId = compatPokemon.id;
        int genderId;
        switch (compatPokemon.gender) {
            case FEMALE:
                genderId = 1;
                break;
            case MALE:
                genderId = 2;
                break;
            case GENDERLESS:
                genderId = 3;
                break;
            default:
                genderId = -1;
                break;
        }
        int[] IVs = compatPokemon.IVs;
        int natureId;
        switch (compatPokemon.nature) {
            case HARDY:
                natureId = 1;
                break;
            case BOLD:
                natureId = 2;
                break;
            case MODEST:
                natureId = 3;
                break;
            case CALM:
                natureId = 4;
                break;
            case TIMID:
                natureId = 5;
                break;
            case LONELY:
                natureId = 6;
                break;
            case DOCILE:
                natureId = 7;
                break;
            case MILD:
                natureId = 8;
                break;
            case GENTLE:
                natureId = 9;
                break;
            case HASTY:
                natureId = 10;
                break;
            case ADAMANT:
                natureId = 11;
                break;
            case IMPISH:
                natureId = 12;
                break;
            case BASHFUL:
                natureId = 13;
                break;
            case CAREFUL:
                natureId = 14;
                break;
            case RASH:
                natureId = 15;
                break;
            case JOLLY:
                natureId = 16;
                break;
            case NAUGHTY:
                natureId = 17;
                break;
            case LAX:
                natureId = 18;
                break;
            case QUIRKY:
                natureId = 19;
                break;
            case NAIVE:
                natureId = 20;
                break;
            case BRAVE:
                natureId = 21;
                break;
            case RELAXED:
                natureId = 22;
                break;
            case QUIET:
                natureId = 23;
                break;
            case SASSY:
                natureId = 24;
                break;
            case SERIOUS:
                natureId = 25;
                break;
            default:
                natureId = 1;
                break;
        }

        int abilitySlot = SqlDatabase.getInstance().getAbilitySlot(pokemonId, compatPokemon.ability);
        if (abilitySlot <= 0) abilitySlot = 1;


        return new StoredPokemon.Builder()
                .setPokemonId(pokemonId)
                .setGenderId(genderId)
                .setIVs(IVs)
                .setNatureId(natureId)
                .setAbilitySlot(abilitySlot)
                .createStoredPokemon();

    }

    public void updateGoalIVs() {
        int[] IVs = new int[6];

        for (int i = 0; i < 6; i++) {
            IVs[i] = getGoalIVs()[i].isChecked() ? 1 : 0;
        }

        storageManager.setGoalIVs(IVs);
    }

    private void updateGoalNature() {
        NatureManager.NatureVerbose natureVerbose = (NatureManager.NatureVerbose) binding.contentInclude.goalInclude.includeNature.spinnerNature.getSelectedItem();
        storageManager.setGoalNature(natureVerbose.id);
        natureManager.setNatureModifier(true);
    }

    private void updateGoalAbility() {
        int spinnerPosition = binding.contentInclude.goalInclude.includeAbility.spinnerAbility.getSelectedItemPosition();
        int abilitySlot = interfaceAbilities.get(spinnerPosition).abilitySlot;
        storageManager.setGoalAbilitySlot(abilitySlot);
    }

    private void removeRippleEffectFromCheckBox(CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable background = checkBox.getBackground();
            if (background instanceof android.graphics.drawable.RippleDrawable) {
                checkBox.setBackground(((android.graphics.drawable.RippleDrawable) background).findDrawableByLayerId(0));
            }
        }
    }

    private Bundle addPositionAsArguments(View v) {
        int[] callerViewPosition = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }

    @Override
    public void onPokemonSelected(int id) {
        storageManager.setGoalId(id);
        feedInterface();
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
    public ArrayList<Integer> getPokemonIds() {
        return storageManager.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return storageManager.getPokemonNames();
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return storageManager.getCompatiblePokemonList(storageManager.getGoalId());
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return storageManager.getBasicPokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return storageManager.getPokemonFamilyList(storageManager.getGoalId());
    }
}
