package marcelo.breguenait.breedinghelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.cketti.library.changelog.ChangeLog;

class Constants {
    public static final int DITTO_ID = 132;
}

public class IvCalculatorFragment extends Fragment
        implements
        StoredPokemonFragment.FeedDataStoredPokemon,
        StoredPokemonFragment.UpdateStoredPokemonList,
        LuckFragment.UpdateLuckInterface,
        GoalIVsFragment.OnGoalUpdate,
        GoalIVsFragment.FeedDataGoalIVs,
        GoalIVsFragment.UpdateGoal,
        LuckFragment.FeederLuckData {

    private final Gson gson = new Gson();
    @Bind(R.id.main_activity_toolbar)
    Toolbar toolbar;
    ActionBarDrawerToggle drawerToggle;

    InitialActivity initialActivity;
    BreedingManager breedingManager;
    private View cardAd;
    private AdView adView;
    private IvManager ivManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_main, container, false);

        initialActivity = (InitialActivity) getActivity();

        ButterKnife.bind(this, v);
        ivManager = new IvManager();
        breedingManager = new BreedingManager();

        drawerToggle = setupDrawerToggle();
        initialActivity.mDrawer.setDrawerListener(drawerToggle);


        toolbar.setTitle("Breeding Helper");

        readData();


        ivManager.updateBestCombination();

        createGoalIVsFragment(savedInstanceState, v);
        createPokemonListFragment(savedInstanceState, v);
        createChanceFragment(savedInstanceState, v);


//        cardMainIVs.refreshInterface();
//        cardChance.updateGoalIvChance();
//        cardChance.updateItems();
//        cardPokemonGrid.refreshItemsInterface();

        cardAd = v.findViewById(R.id.cardAd);
//        if(!sharedPref.getBoolean("hasSeenDittoTutorial",false)) {
//            dittoTutorial();
//        }

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
    public void onStart() {
        super.onStart();
          //updateLuckFragment();


    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        setAdVisibility(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(),
                initialActivity.getDrawer(), toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_report_bug:
                sendBugReport();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void openSettings() {
        Intent intent = new Intent(getContext(), SettingsActivity.class);
        startActivity(intent);
    }

    void createAd() {
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
                .addTestDevice("267EF55A4F5EC1C9C51E1CFE97F4ECB2")
                .addTestDevice("13294F32A78405C5913ABE707DFDBA19")
                .addTestDevice("91A24715E4A4374919B327DF3077F7F1")
                .addTestDevice("1F046724752EDE67A5E450DC2A244643")
                .addTestDevice("25A7191AABFFE286F36D039D86AB7D11")
                .addTestDevice("0787F1B6D26E3657D6C7F11CE39DFB1F")
                .addTestDevice("AB68924514A4CDD20D5D115C7174D722")
                .addTestDevice("EE498B7BD93FDB4D08CD04DE6C09F09A")
                .addTestDevice("815CB1AC3DD5926E21AE260FC94A4D6A")
                .addTestDevice("8C7BA5C848E50217D49DACC26972F1B9")
                .addTestDevice("C4BE91EE53C4D54137B99B336B805908")
                .build();

        adView.loadAd(adRequest);

    }

    void setAdVisibility(boolean disabled) {
        if (disabled) {
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

    void sendBugReport() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"marcelofernandesmorais+bhbug@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "[Breeding Helper Bug Report]");

        String body = "Android version: " + Build.VERSION.RELEASE + " (" + Integer.toString(Build.VERSION.SDK_INT) + ") " + Build.PRODUCT + System.getProperty("line.separator");
        body += "Phone Model: " + Build.BRAND + " " + Build.MODEL + System.getProperty("line.separator");
        body += "Bug Description: ";

        i.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


    void createGoalIVsFragment(Bundle savedInstanceState, View v) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (v.findViewById(R.id.frameGoalIVsFragmentContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            GoalIVsFragment firstFragment = new GoalIVsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getActivity().getIntent().getExtras());
            firstFragment.setTargetFragment(this, 0);

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.frameGoalIVsFragmentContainer, firstFragment).commit();


        }


    }

    void createPokemonListFragment(Bundle savedInstanceState, View v) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        StoredPokemonFragment storedPokemonFragment = new StoredPokemonFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        storedPokemonFragment.setArguments(getActivity().getIntent().getExtras());

        storedPokemonFragment.setTargetFragment(this, 0);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.framePokemonListFragmentContainer, storedPokemonFragment).commit();


    }

    void createChanceFragment(Bundle savedInstanceState, View v) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        LuckFragment luckFragment = new LuckFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        luckFragment.setArguments(getActivity().getIntent().getExtras());
        luckFragment.setTargetFragment(this, 0);


        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.frameLuckFragmentContainer, luckFragment).commit();

    }

    @Deprecated
    void updatePokemonListFragment() {
        //TODO: fazer por callback!
        StoredPokemonFragment frag = (StoredPokemonFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        frag.updateGridView();
    }

    void updateLuckFragment() {
        LuckFragment frag = (LuckFragment) getFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        frag.updateCurrentChances();
    }

    void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

        jsonString = gson.toJson(ivManager.getStoredPokemonList());
        prefEditor.putString("jsonPokemonList", jsonString);

        jsonString = gson.toJson(ivManager.hasEverstone());
        prefEditor.putString("jsonHasEverstone", jsonString);

        jsonString = gson.toJson(ivManager.considerNature());
        prefEditor.putString("jsonConsiderNature", jsonString);

        jsonString = gson.toJson(ivManager.considerAbility());
        prefEditor.putString("jsonConsiderAbility", jsonString);

        jsonString = gson.toJson(ivManager.getGoalPokemon());
        prefEditor.putString("jsonCurrentGoal", jsonString);

        jsonString = gson.toJson(ivManager.getMaleItem());
        prefEditor.putString("jsonMaleItem", jsonString);

        LuckFragment l = (LuckFragment) getFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        jsonString = gson.toJson(l.getShinyOptions());
        prefEditor.putString("jsonShinyOptions", jsonString);

        //-------Breeding Manager---------------

        jsonString = gson.toJson(breedingManager.getStoredPokemonObjects());
        prefEditor.putString("jsonBreedingManagerStoredList", jsonString);

        jsonString = gson.toJson(breedingManager.getGoalObject());
        prefEditor.putString("jsonBreedingManagerGoal", jsonString);

        prefEditor.apply();


    }

    void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String jsonString;

        jsonString = sharedPref.getString("jsonPokemonList", null);
        if (jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>() {}.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ivManager.setStoredPokemonList(eggList);
        }


        jsonString = sharedPref.getString("jsonHasEverstone", null);
        if (jsonString != null) {
            ivManager.setEverstone(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderNature", null);
        if (jsonString != null) {
            ivManager.setConsiderNature(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderAbility", null);
        if (jsonString != null) {
            ivManager.setConsiderAbility(gson.fromJson(jsonString, Boolean.class));
        }

//        jsonString = sharedPref.getString("jsonCurrentGoal", null);
//        if (jsonString != null) {
//            ivManager.setGoalPokemon(gson.fromJson(jsonString, PokemonInfo.class));
//        }

        jsonString = sharedPref.getString("jsonCurrentGoal", null);
        if (jsonString != null) {
            ivManager.setGoalPokemon(gson.fromJson(jsonString, PokemonInfo.class));
        } else {
            ivManager.setEmptyGoalPokemon();
        }

        jsonString = sharedPref.getString("jsonMaleItem", null);
        if (jsonString != null) {
            ivManager.setMaleItem(gson.fromJson(jsonString, Item.class));
        }

        jsonString = sharedPref.getString("jsonDittoList", null);
        if (jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>() {
            }.getType();
            List<PokemonInfo> dittoList = gson.fromJson(jsonString, type);
            for (int i = 0; i < dittoList.size(); i++) {
                PokemonInfo p = new PokemonInfo.Builder()
                        .id(Constants.DITTO_ID)
                        .gender(Gender.DITTO)
                        .IVs(dittoList.get(i).IVs)
                        .build();
                ivManager.storePokemon(p);
            }
            sharedPref.edit().remove("jsonDittoList").apply();
        }

        jsonString = sharedPref.getString("jsonEggList", null);
        if (jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>() {
            }.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            for (int i = 0; i < eggList.size(); i++) {
                PokemonInfo p = new PokemonInfo.Builder()
                        .id(0)
                        .gender(eggList.get(i).gender)
                        .IVs(eggList.get(i).IVs)
                        .build();
                ivManager.storePokemon(p);
            }
            sharedPref.edit().remove("jsonEggList").apply();
        }

        jsonString = sharedPref.getString("jsonBreedingManagerStoredList", null);
        if (jsonString != null) {
            Type type = new TypeToken<ArrayList<StoredPokemon>>() {}.getType();
            ArrayList<StoredPokemon> objectsList = gson.fromJson(jsonString, type);
            breedingManager.replaceStoredPokemonObjects(objectsList);
        }

        jsonString = sharedPref.getString("jsonBreedingManagerGoal", null);
        if (jsonString != null) {
            breedingManager.replaceGoalObject(gson.fromJson(jsonString, StoredPokemon.class));
        }

    }


    @Override
    public void setDestinyKnot(boolean b) {
        ivManager.setMaleItem(b ? Item.DESTINY_KNOT : Item.NO_ITEM);
        //updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean updateDestinyKnotChance() {
        return ivManager.getMaleItem() == Item.DESTINY_KNOT;
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


    void saveBoolean(String key, Boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(key, value);
        prefEditor.apply();
    }

    Boolean readBoolean(String key, Boolean assumedValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        return sharedPref.getBoolean(key, assumedValue);

    }


    @Override
    public void updateNatureStatus(boolean b) {
        ivManager.setConsiderNature(b);
        //updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean careAboutNatures() {
        return (ivManager.hasEverstone() && ivManager.considerNature());
    }

    @Override
    public void setEverstone(boolean b) {
        ivManager.setEverstone(b);
        //updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean getConsiderNatureStatus() {
        return ivManager.considerNature();
    }

    @Override
    public boolean updateEverstoneStatus() {
        return ivManager.hasEverstone();
    }

    @Override
    public void updateAbilityStatus(boolean b) {
        ivManager.setConsiderAbility(b);
        //updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean getConsiderAbilityStatus() {
        return ivManager.considerAbility();
    }

    @Override
    public ArrayList<String> getListOfNatures() {
        return breedingManager.getListOfNatures();
    }

    @Override
    public HashMap<Integer, String> getListOfGoalAbilities() {
        return breedingManager.getListOfGoalAbilities();
    }


    @Override
    public void updateGoalId(int id) {
        breedingManager.setGoalId(id);
        updateLuckFragment();
    }

    @Override
    public void updateGoalNature(int natureId) {
        breedingManager.setGoalNature(natureId);
        updateLuckFragment();
    }

    @Override
    public void updateGoalAbilitySlot(int abilitySlot) {
        breedingManager.setGoalAbilitySlot(abilitySlot);
        updateLuckFragment();
    }

    @Override
    public void updateGoalIVs(int[] IVs) {
        breedingManager.setGoalIVs(IVs);
        updateLuckFragment();
    }


    @Override
    public HashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return breedingManager.getListOfAbilities(pokemonId);
    }

    @Override
    public int getGenderRate(int pokemonId) {
        return breedingManager.getGenderRate(pokemonId);
    }



    @Override
    public ArrayList<InterfaceStoredPokemon> getInterfaceStoredPokemonList() {
        return breedingManager.getInterfaceStoredPokemonList();
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return breedingManager.getCompatiblePokemonList(breedingManager.getGoalId());
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
    public InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {
        return breedingManager.getInterfaceViewerPokemon(uuid);
    }

    @Override
    public String getPokemonName(int pokemonId) {
        return breedingManager.getPokemonName(pokemonId);
    }

    @Override
    public InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
        return breedingManager.getInterfaceModifierPokemon(uuid);
    }

    @Override
    public void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        breedingManager.storePokemon(pokemonId, genderId, IVs, natureId, abilitySlot);
        updatePokemonListFragment(); //TODO: remover daqui
        updateLuckFragment();
    }

    @Override
    public void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        breedingManager.updateStoredPokemon(uuid, pokemonId, genderId, IVs, natureId, abilitySlot);
        updatePokemonListFragment(); //TODO: remover daqui
        updateLuckFragment();
    }

    @Override
    public void removeStoredPokemon(UUID uuid) {
        breedingManager.removePokemon(uuid);
        updateLuckFragment();
    }

    @Override
    public ArrayList<ChancePokemonMatch> getChancesList() {
        return breedingManager.calculateBestMatches();
    }

    @Override
    public InterfaceChancePokemon getInterfaceChancePokemon(UUID uuid) {
        return breedingManager.getInterfaceChancePokemon(uuid);
    }

    @Override
    public InterfaceGoalPokemon getInterfaceGoalPokemon() {
        return breedingManager.getInterfaceGoalPokemon();
    }
}
