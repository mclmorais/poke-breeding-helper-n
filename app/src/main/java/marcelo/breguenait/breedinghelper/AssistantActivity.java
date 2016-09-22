package marcelo.breguenait.breedinghelper;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import breedingmanager.BreedingManager;
import breedingmanager.StoredPokemon;
import butterknife.Bind;
import butterknife.ButterKnife;
import databasemanager.MyDatabase;

public class AssistantActivity extends AppCompatActivity {

    private final BreedingManager breedingManager = new BreedingManager();
    private final Gson gson = new Gson();

    @Bind({R.id.checkBoxGoalHP,
            R.id.checkBoxGoalATK,
            R.id.checkBoxGoalDEF,
            R.id.checkBoxGoalSATK,
            R.id.checkBoxGoalSDEF,
            R.id.checkBoxGoalSPD})
    CheckBox[] goalIVs;

    @Bind(R.id.frameLayoutPokemonSelectorButton)
    View buttonPokemonSelector;
    private final View.OnClickListener onClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonPokemonSelector) {
                //openSelectPokemonFragment(v);
            }

        }
    };
    @Bind(R.id.imageViewSelectedPokemonIcon)
    ImageView selectedIcon;
    @Bind(R.id.spinnerGoalIVsNatures)
    Spinner spinnerNature;
    @Bind(R.id.spinnerGoalIVsAbilities)
    Spinner spinnerAbility;
    @Bind(R.id.checkBoxGoalIVsActivateNatures)
    SwitchCompat checkBoxActivateNatures;
    @Bind(R.id.checkBoxGoalIVsActivateAbilities)
    SwitchCompat checkBoxActivateAbilities;
    @Bind(R.id.textViewPokemonName)
    TextView selectedName;

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










    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    private void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String jsonString;


        jsonString = sharedPref.getString("jsonCurrentGoal", null);
        if (jsonString != null) {
            PokemonInfo oldGoal = gson.fromJson(jsonString, PokemonInfo.class);
            if(oldGoal != null) {
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
            Type type = new TypeToken<List<PokemonInfo>>() {}.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ArrayList<StoredPokemon> newList = new ArrayList<>(eggList.size());
            for (PokemonInfo pokemonInfo : eggList) {
                if(pokemonInfo != null)
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

    private void removeRippleEffectFromCheckBox(CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = checkBox.getBackground();
            if (drawable instanceof RippleDrawable) {
                drawable = ((RippleDrawable) drawable).findDrawableByLayerId(0);
                checkBox.setBackground(drawable);
            }
        }
    }
}

