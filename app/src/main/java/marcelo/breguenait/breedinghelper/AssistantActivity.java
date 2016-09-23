package marcelo.breguenait.breedinghelper;

import android.animation.LayoutTransition;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

import breedingmanager.BreedingManager;
import breedingmanager.StoredPokemon;
import butterknife.Bind;
import butterknife.ButterKnife;
import databasemanager.DatabaseConstants;
import databasemanager.MyDatabase;

public class AssistantActivity extends AppCompatActivity implements SelectPokemonFragment.OnPokemonSelectedListener, SelectPokemonFragment.FeedDataSelectPokemon {

    private final BreedingManager breedingManager = new BreedingManager();
    private final Gson gson = new Gson();

    ArrayList<InterfaceNature> interfaceNatures;
    ArrayList<InterfaceAbility> interfaceAbilities;

    @Bind({R.id.checkBoxGoalHP,
            R.id.checkBoxGoalATK,
            R.id.checkBoxGoalDEF,
            R.id.checkBoxGoalSATK,
            R.id.checkBoxGoalSDEF,
            R.id.checkBoxGoalSPD})
    CheckBox[] goalIVs;

    @Bind(R.id.buttonSelector)
    View buttonPokemonSelector;


    @Bind(R.id.imageViewSelectedPokemonIcon)
    ImageView selectedIcon;


    @Bind(R.id.layoutModifiers)
    LinearLayout layoutModifiers;

    @Bind(R.id.buttonAddNature)
    Button buttonAddNature;
    @Bind(R.id.includeNature)
    View includeNaturePicker;
    @Bind(R.id.spinnerNature)
    Spinner spinnerNature;
    @Bind(R.id.buttonRemoveNature)
    ImageButton buttonRemoveNature;

    @Bind(R.id.buttonAddAbility)
    Button buttonAddAbility;
    @Bind(R.id.includeAbility)
    View includeAbilityPicker;
    @Bind(R.id.spinnerAbility)
    Spinner spinnerAbility;
    @Bind(R.id.buttonRemoveAbility)
    ImageButton buttonRemoveAbility;






    private final View.OnClickListener onClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (v == buttonPokemonSelector) {
                openSelectPokemonFragment(v);
            }
            else if (v == buttonAddNature) {
                includeNaturePicker.setVisibility(View.VISIBLE);
                buttonAddNature.setEnabled(false);
            }
            else if (v == buttonRemoveNature) {
                includeNaturePicker.setVisibility(View.GONE);
                buttonAddNature.setEnabled(true);
            }
            else if (v == buttonAddAbility) {
                includeAbilityPicker.setVisibility(View.VISIBLE);
                buttonAddAbility.setEnabled(false);
            }
            else if (v == buttonRemoveAbility) {
                includeAbilityPicker.setVisibility(View.GONE);
                buttonAddAbility.setEnabled(true);
            }

        }
    };


//    @Bind(R.id.spinnerGoalIVsNatures)
//    Spinner spinnerNature;
//    @Bind(R.id.spinnerGoalIVsAbilities)
//    Spinner spinnerAbility;
//    @Bind(R.id.checkBoxGoalIVsActivateNatures)
//    SwitchCompat checkBoxActivateNatures;
//    @Bind(R.id.checkBoxGoalIVsActivateAbilities)
//    SwitchCompat checkBoxActivateAbilities;
    @Bind(R.id.textViewPokemonName)
    TextView selectedName;

    private final CheckBox.OnCheckedChangeListener onCheckBoxCheckHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (buttonView == checkBoxActivateNatures) {
//                //updaterCallback.updateNatureStatus(isChecked);
//                String s;
//                if (isChecked)
//                    s = getString(R.string.message_nature_considered);
//                else
//                    s = getString(R.string.message_nature_ignored);
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//            } else if (buttonView == checkBoxActivateAbilities) {
//                //updaterCallback.updateAbilityStatus(isChecked);
//                String s;
//                if (isChecked)
//                    s = getString(R.string.message_ability_considered);
//                else
//                    s = getString(R.string.message_ability_ignored);
//
//                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//            }

            for (CheckBox goalIVsCheckBox : goalIVs) {
                if (buttonView == goalIVsCheckBox) {
                    updateGoalIVs();
                    return;
                }
            }
        }
    };

    private final Spinner.OnItemSelectedListener onSpinnerItemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent == spinnerNature) {
                if (spinnerNature.getTag() != null && !spinnerNature.getTag().equals(position)) {
                    spinnerNature.setTag(-1);
                    updateGoalNature();
                }
            } else if (parent == spinnerAbility) {
                if (spinnerAbility.getTag() != null && !spinnerAbility.getTag().equals(position)) {
                    spinnerAbility.setTag(-1);
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
        setContentView(R.layout.activity_assistant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Custom content
        readData();
        buttonPokemonSelector.setOnClickListener(onClickHandler);
        buttonAddNature.setOnClickListener(onClickHandler);
        buttonRemoveNature.setOnClickListener(onClickHandler);
        buttonAddAbility.setOnClickListener(onClickHandler);
        buttonRemoveAbility.setOnClickListener(onClickHandler);


        for (int i = 0; i < goalIVs.length; i++) {
            removeRippleEffectFromCheckBox(goalIVs[i]);
            goalIVs[i].setChecked(breedingManager.getGoalObject().getIVs()[i] == 1);
            goalIVs[i].setOnCheckedChangeListener(onCheckBoxCheckHandler);
        }

        feedInterface(breedingManager.getInterfaceGoalPokemon());
        spinnerNature.setOnItemSelectedListener(onSpinnerItemSelectedHandler);
        spinnerAbility.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

//        ViewGroup layout = (ViewGroup) findViewById(R.id.layoutModifiers);
//        LayoutTransition layoutTransition = layout.getLayoutTransition();
//        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
//
//        layout = (ViewGroup) findViewById(R.id.goalCardLayout);
//        layoutTransition = layout.getLayoutTransition();
//        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
//
//        layout = (ViewGroup) findViewById(R.id.goalCardLayout);
//        layoutTransition = layout.getLayoutTransition();
//        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    private void feedInterface(GoalPokemonFragment.InterfaceGoalPokemon interfaceGoalPokemon) {
        feedDisplayedName(interfaceGoalPokemon.getPokemonName());
        feedDisplayedIcon(interfaceGoalPokemon.getPokemonId());

        interfaceNatures = breedingManager.getInterfaceNatures();
        spinnerNature.setAdapter(new NatureSpinnerAdapter(interfaceNatures, this));
        feedNatureSpinnerSelection(interfaceGoalPokemon.getNatureId());

        feedAbilitySpinner();
        feedAbilitySpinnerSelection(interfaceGoalPokemon.getAbilitySlot());
    }

    private void feedDisplayedName(String name) {

        //If not initialized (""): doesn't update

        if (!name.equals(""))
            selectedName.setText(name);
    }

    private void feedDisplayedIcon(int id) {

        if (DatabaseConstants.pokemonIdIsValid(id)) { //TODO: fazer 0 < x < limite
            String iconId = "pkmn_big_" + String.format("%03d", id);
            selectedIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getPackageName()));
        }
    }

    private void feedNatureSpinnerSelection(int natureId) {


        for (int i = 0; i < interfaceNatures.size(); i++) {

            if (interfaceNatures.get(i).id == natureId) {
                spinnerNature.setTag(i);
                spinnerNature.setSelection(i);
                return;
            }

        }
        Log.d("GoalFragment", "Received a pokemon with invalid nature");

    }

    private void feedAbilitySpinner() {
        if (spinnerAbility == null) return;

        LinkedHashMap<Integer, String> abilities = breedingManager.getListOfGoalAbilities();

        interfaceAbilities = new ArrayList<>();

        //Transforms the slot -> name HashMap into a InterfaceAbility to be used as a list
        for (HashMap.Entry<Integer, String> entry : abilities.entrySet())
            interfaceAbilities.add(new InterfaceAbility(entry.getValue(), entry.getKey()));

        spinnerAbility.setAdapter(new GoalPokemonFragment.AbilitySpinnerAdapter(interfaceAbilities, this));
    }

    private void feedAbilitySpinnerSelection(int abilitySlot) {

        //If the slot is valid
        if (DatabaseConstants.abilitySlotIsValid(abilitySlot)) {
            //Searches the interfaceAbilities for a one that corresponds to the goal slot
            int position = -1;
            for (int i = 0; i < interfaceAbilities.size(); i++) {
                if (interfaceAbilities.get(i).abilitySlot == abilitySlot) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                //If it has been found, sets the spinner to that position
                spinnerAbility.setTag(position);
                spinnerAbility.setSelection(position);
            } else {
                Log.d("GOAL", "AbilitySlot " + String.valueOf(abilitySlot) +
                        " wasn't found in InterfaceAbilities.");
            }
        }
    }

    private void openSelectPokemonFragment(View view) {
        FragmentManager fm = getSupportFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);;
        selectPokemonFragment.show(fm, "");
    }


    private void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonString;


        jsonString = sharedPref.getString("jsonCurrentGoal", null);
        if (jsonString != null) {
            PokemonInfo oldGoal = gson.fromJson(jsonString, PokemonInfo.class);
            if (oldGoal != null) {
                breedingManager.replaceGoalObject(convertCompatPokemon(oldGoal));
            }
            sharedPref.edit().remove("jsonCurrentGoal").apply();
        } else {
            jsonString = sharedPref.getString("jsonBreedingManagerGoal", null);
            if (jsonString != null) {
                breedingManager.replaceGoalObject(gson.fromJson(jsonString, StoredPokemon.class));
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
            breedingManager.replaceStoredPokemonObjects(newList);
            sharedPref.edit().remove("jsonPokemonList").apply();
        } else {
            jsonString = sharedPref.getString("jsonBreedingManagerStoredList", null);
            if (jsonString != null) {
                Type type = new TypeToken<ArrayList<StoredPokemon>>() {
                }.getType();
                ArrayList<StoredPokemon> objectsList = gson.fromJson(jsonString, type);
                breedingManager.replaceStoredPokemonObjects(objectsList);
            }
        }


        jsonString = sharedPref.getString("jsonHasEverstone", null);
        if (jsonString != null) {
            breedingManager.setEverstone(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderNature", null);
        if (jsonString != null) {
            breedingManager.setConsiderNature(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderAbility", null);
        if (jsonString != null) {
            breedingManager.setConsiderAbility(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonMaleItem", null); //POR ENQUANTO ARMAZENA O DESTINY KNOT!
        if (jsonString != null) {
            breedingManager.setDestinyKnot(gson.fromJson(jsonString, Boolean.class));
        }

        int gameLanguage = Integer.valueOf(sharedPref.getString("gameLanguage", "9"));
        breedingManager.setLanguageId(gameLanguage);

    }

    private void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

//        jsonString = gson.toJson(ivManager.getStoredPokemonList());
//        prefEditor.putString("jsonPokemonList", jsonString);

//        jsonString = gson.toJson(ivManager.getGoalPokemon());
//        prefEditor.putString("jsonCurrentGoal", jsonString);


        jsonString = gson.toJson(breedingManager.hasEverstone());
        prefEditor.putString("jsonHasEverstone", jsonString);

        jsonString = gson.toJson(breedingManager.considerNature());
        prefEditor.putString("jsonConsiderNature", jsonString);

        jsonString = gson.toJson(breedingManager.considerAbility());
        prefEditor.putString("jsonConsiderAbility", jsonString);

        jsonString = gson.toJson(breedingManager.hasDestinyKnot());
        prefEditor.putString("jsonMaleItem", jsonString);

//        ChanceFragment l = (ChanceFragment) getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
//        jsonString = gson.toJson(l.getShinyOptions());
//        prefEditor.putString("jsonShinyOptions", jsonString);

        //-------Breeding Manager---------------

        jsonString = gson.toJson(breedingManager.getStoredPokemonObjects());
        prefEditor.putString("jsonBreedingManagerStoredList", jsonString);

        jsonString = gson.toJson(breedingManager.getGoalObject());
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

        int abilitySlot = MyDatabase.getInstance().getAbilitySlot(pokemonId, compatPokemon.ability);
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
            IVs[i] = goalIVs[i].isChecked() ? 1 : 0;
        }

        breedingManager.setGoalIVs(IVs);
        //updateLuckFragment();
    }

    private void updateGoalNature() {
        InterfaceNature interfaceNature = (InterfaceNature) spinnerNature.getSelectedItem();
        breedingManager.setGoalNature(interfaceNature.id);
    }

    private void updateGoalAbility() {

        int spinnerPosition = spinnerAbility.getSelectedItemPosition();

        int abilitySlot = interfaceAbilities.get(spinnerPosition).abilitySlot;

        breedingManager.setGoalAbilitySlot(abilitySlot);

    }

    private void removeRippleEffectFromCheckBox(CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = checkBox.getBackground();
            if (drawable instanceof RippleDrawable) {
                drawable = ((RippleDrawable) drawable).findDrawableByLayerId(0);
                checkBox.setBackground(drawable);
            }
        }
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
    public void onPokemonSelected(int id) {
        breedingManager.setGoalId(id);
        feedInterface(breedingManager.getInterfaceGoalPokemon());
    }

    @Override
    public boolean showEggGroupFilter() {
        return false;
    }

    @Override
    public boolean showOnlyBasic() {
        return true;
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return breedingManager.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return breedingManager.getPokemonNames();
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return breedingManager.getCompatiblePokemonList(breedingManager.getGoalId());
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return breedingManager.getBasicPokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return breedingManager.getPokemonFamilyList(breedingManager.getGoalId());
    }

}

