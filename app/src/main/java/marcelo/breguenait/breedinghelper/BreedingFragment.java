package marcelo.breguenait.breedinghelper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import breedingmanager.StorageManager;
import breedingmanager.ChancePokemonMatch;
import breedingmanager.StoredPokemon;
import butterknife.Bind;
import butterknife.ButterKnife;
import databasemanager.MyDatabase;
import de.cketti.library.changelog.ChangeLog;

public class BreedingFragment extends Fragment
        implements
        StoredPokemonFragment.FeedDataStoredPokemon,
        StoredPokemonFragment.UpdateStoredPokemonList,
        ChanceFragment.UpdateLuckInterface,
        GoalPokemonFragment.OnGoalUpdate,
        GoalPokemonFragment.FeedDataGoalIVs,
        GoalPokemonFragment.UpdateGoal,
        ChanceFragment.FeederLuckData {

    private final Gson gson = new Gson();
    GoalPokemonFragment goalPokemonFragment;
    ChanceFragment chanceFragment;
    StoredPokemonFragment storedPokemonFragment;
    @Bind(R.id.main_activity_toolbar)
    Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    private MainActivity mainActivity;
    private StorageManager storageManager;
    private View cardAd;
    private AdView adView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("Lifecycle", "BreedingFragment - onCreateView");
        View v = inflater.inflate(R.layout.activity_main, container, false);

        //setHasOptionsMenu(true);


        mainActivity = (MainActivity) getActivity();

        ButterKnife.bind(this, v);
        storageManager = new StorageManager();

        drawerToggle = setupDrawerToggle();
        mainActivity.mDrawer.setDrawerListener(drawerToggle);


        toolbar.setTitle("Breeding Helper");

        readData();

        createGoalIVsFragment(savedInstanceState, v);
        createPokemonListFragment(savedInstanceState);
        createChanceFragment(savedInstanceState);

        cardAd = v.findViewById(R.id.cardAd);


        ChangeLog cl = new ChangeLog(getContext());
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }

        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdVisibility();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(),
                mainActivity.getDrawer(), toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    private void createAd() {
        adView = new AdView(getContext());
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9350161103739995/6628696664");
        LinearLayout adListLayout = (LinearLayout) getActivity().findViewById(R.id.cardLayoutAd);
        adListLayout.addView(adView);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if (dpWidth < (320 + 32)) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            LinearLayout.LayoutParams currentMargin = (LinearLayout.LayoutParams) cardAd.getLayoutParams();
            params.setMargins(0, currentMargin.topMargin, 0, currentMargin.bottomMargin);
            cardAd.setLayoutParams(params);
        }


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        adView.loadAd(adRequest);

    }

    private void setAdVisibility() {
        if (true) {
            cardAd.setVisibility(View.GONE);
            if (adView != null) {
                adView.setEnabled(false);
                adView.setVisibility(View.GONE);
            }

        } else {
            if (adView == null)
                createAd();
            cardAd.setVisibility(View.VISIBLE);
            adView.setEnabled(true);
            adView.setVisibility(View.VISIBLE);
        }
    }


    private void createGoalIVsFragment(Bundle savedInstanceState, View v) {

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (v.findViewById(R.id.frameGoalIVsFragmentContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            GoalPokemonFragment firstFragment = new GoalPokemonFragment();
            firstFragment.setCallbacks(this);
            firstFragment.setArguments(getActivity().getIntent().getExtras());

            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frameGoalIVsFragmentContainer, firstFragment, "GoalFragment");
            fragmentTransaction.commit();

            goalPokemonFragment = firstFragment;
        }

    }

    private void createChanceFragment(Bundle savedInstanceState) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        ChanceFragment secondFragment = new ChanceFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        secondFragment.setArguments(getActivity().getIntent().getExtras());
        secondFragment.setCallbacks(this);


        // Add the fragment to the 'fragment_container' FrameLayout
        getChildFragmentManager().beginTransaction()
                .add(R.id.frameLuckFragmentContainer, secondFragment).commit();
        chanceFragment = secondFragment;
    }

    private void createPokemonListFragment(Bundle savedInstanceState) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        StoredPokemonFragment thirdFragment = new StoredPokemonFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        thirdFragment.setArguments(getActivity().getIntent().getExtras());

        thirdFragment.setCallbacks(this);

        // Add the fragment to the 'fragment_container' FrameLayout
        getChildFragmentManager().beginTransaction()
                .add(R.id.framePokemonListFragmentContainer, thirdFragment).commit();

        storedPokemonFragment = thirdFragment;


    }


    private void updatePokemonListFragment() {
        StoredPokemonFragment frag = (StoredPokemonFragment) getChildFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        frag.setHatchAdapter(storageManager.getInterfaceStoredPokemonList(), getContext());
    }


    private void updateLuckFragment() {
        ChanceFragment frag = (ChanceFragment) getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        frag.updateCurrentChances();
    }

    private void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

//        jsonString = gson.toJson(ivManager.getStoredPokemonList());
//        prefEditor.putString("jsonPokemonList", jsonString);

//        jsonString = gson.toJson(ivManager.getGoalPokemon());
//        prefEditor.putString("jsonCurrentGoal", jsonString);


        jsonString = gson.toJson(storageManager.hasEverstone());
        prefEditor.putString("jsonHasEverstone", jsonString);

        jsonString = gson.toJson(storageManager.considerNature());
        prefEditor.putString("jsonConsiderNature", jsonString);

        jsonString = gson.toJson(storageManager.considerAbility());
        prefEditor.putString("jsonConsiderAbility", jsonString);

        jsonString = gson.toJson(storageManager.hasDestinyKnot());
        prefEditor.putString("jsonMaleItem", jsonString);

        ChanceFragment l = (ChanceFragment) getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        jsonString = gson.toJson(l.getShinyOptions());
        prefEditor.putString("jsonShinyOptions", jsonString);

        //-------Breeding Manager---------------

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


    private void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonString;


        jsonString = sharedPref.getString("jsonCurrentGoal", null);
        if (jsonString != null) {
            PokemonInfo oldGoal = gson.fromJson(jsonString, PokemonInfo.class);
            if(oldGoal != null) {
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
            Type type = new TypeToken<List<PokemonInfo>>() {}.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ArrayList<StoredPokemon> newList = new ArrayList<>(eggList.size());
            for (PokemonInfo pokemonInfo : eggList) {
                if(pokemonInfo != null)
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
            storageManager.setConsiderNature(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderAbility", null);
        if (jsonString != null) {
            storageManager.setConsiderAbility(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonMaleItem", null); //POR ENQUANTO ARMAZENA O DESTINY KNOT!
        if (jsonString != null) {
            storageManager.setDestinyKnot(gson.fromJson(jsonString, Boolean.class));
        }

        int gameLanguage = Integer.valueOf(sharedPref.getString("gameLanguage", "9"));
        storageManager.setLanguageId(gameLanguage);

    }


    @Override
    public void setDestinyKnot(boolean b) {
        storageManager.setDestinyKnot(b);
        updateLuckFragment();
    }

    @Override
    public boolean isDestinyKnotActive() {
        //return ivManager.getMaleItem() == Item.DESTINY_KNOT;
        return storageManager.hasDestinyKnot();
    }

    @Override
    public int loadShinyOptions() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonString;

        jsonString = sharedPref.getString("jsonShinyOptions", null);
        if (jsonString != null) {
            return (gson.fromJson(jsonString, Integer.class));
        } else return 0;
    }

    @Override
    public void updateNatureStatus(boolean b) {
        storageManager.setConsiderNature(b);
        updateLuckFragment();
    }

    @Override
    public boolean careAboutNatures() {
        return (storageManager.hasEverstone() && storageManager.considerNature());
    }

    @Override
    public void setEverstone(boolean b) {
        storageManager.setEverstone(b);
        updateLuckFragment();
    }

    @Override
    public boolean getConsiderNatureStatus() {
        return storageManager.considerNature();
    }

    @Override
    public boolean updateEverstoneStatus() {
        return storageManager.hasEverstone();
    }

    @Override
    public void updateAbilityStatus(boolean b) {
        storageManager.setConsiderAbility(b);
        updateLuckFragment();
    }

    @Override
    public boolean getConsiderAbilityStatus() {
        return storageManager.considerAbility();
    }


    @Override
    public LinkedHashMap<Integer, String> getListOfGoalAbilities() {
        return storageManager.getListOfGoalAbilities();
    }


    @Override
    public void updateGoalId(int id) {
        storageManager.setGoalId(id);
        updateLuckFragment();
    }

    @Override
    public void updateGoalNature(int natureId) {
        storageManager.setGoalNature(natureId);
        updateLuckFragment();
    }

    @Override
    public void updateGoalAbilitySlot(int abilitySlot) {
        storageManager.setGoalAbilitySlot(abilitySlot);
        updateLuckFragment();
    }

    @Override
    public void updateGoalIVs(int[] IVs) {
        storageManager.setGoalIVs(IVs);
        updateLuckFragment();
    }


    @Override
    public LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return storageManager.getListOfAbilities(pokemonId);
    }

    @Override
    public int getGenderRate(int pokemonId) {
        return storageManager.getGenderRate(pokemonId);
    }

    @Override
    public ArrayList<StoredPokemonFragment.InterfaceStoredPokemon> getInterfaceStoredPokemonList() {
        return storageManager.getInterfaceStoredPokemonList();
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
    public ArrayList<Integer> getPokemonIds() {
        return storageManager.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return storageManager.getPokemonNames();
    }

    @Override
    public StoredPokemonViewerFragment.InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {
        return storageManager.getInterfaceViewerPokemon(uuid);
    }

    @Override
    public String getPokemonName(int pokemonId) {
        return storageManager.getPokemonName(pokemonId);
    }

    @Override
    public InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
        return storageManager.getInterfaceModifierPokemon(uuid);
    }

    @Override
    public void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        storageManager.storePokemon(pokemonId, genderId, IVs, natureId, abilitySlot);
        updatePokemonListFragment();
        updateLuckFragment();
    }

    @Override
    public void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        storageManager.updateStoredPokemon(uuid, pokemonId, genderId, IVs, natureId, abilitySlot);
        updatePokemonListFragment();
        updateLuckFragment();
    }

    @Override
    public void removeStoredPokemon(UUID uuid) {
        storageManager.removePokemon(uuid);
        updateLuckFragment();
    }

    @Override
    public ArrayList<ChancePokemonMatch> getChancesList() {
        return storageManager.calculateBestMatches();
    }

    @Override
    public ChanceFragment.InterfaceChancePokemon getInterfaceChancePokemon(UUID uuid) {
        return storageManager.getInterfaceChancePokemon(uuid);
    }

    @Override
    public AssistantActivity.InterfaceGoalPokemon getInterfaceGoalPokemon() {
        return storageManager.getInterfaceGoalPokemon();
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return storageManager.getPokemonFamilyList(storageManager.getGoalId());
    }

    @Override
    public int getInterfacePokemonPosition(UUID uuid) {
        StoredPokemonFragment frag = (StoredPokemonFragment) getChildFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        return frag.getInterfacePokemonPosition(uuid);
    }

    @Override
    public ArrayList<InterfaceNature> getInterfaceNatures() {
        return storageManager.getInterfaceNatures();
    }
}
