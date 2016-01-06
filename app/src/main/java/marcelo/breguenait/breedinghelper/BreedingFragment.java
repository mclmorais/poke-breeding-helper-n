package marcelo.breguenait.breedinghelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
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

import breedingmanager.BreedingManager;
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
    @Bind(R.id.main_activity_toolbar)
    Toolbar toolbar;
    GoalPokemonFragment goalPokemonFragment;
    ChanceFragment chanceFragment;
    StoredPokemonFragment storedPokemonFragment;
    private ActionBarDrawerToggle drawerToggle;
    private MainActivity mainActivity;
    private BreedingManager breedingManager;
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
        breedingManager = new BreedingManager();

        drawerToggle = setupDrawerToggle();
        mainActivity.mDrawer.setDrawerListener(drawerToggle);


        toolbar.setTitle("Breeding Helper");
        //toolbar.inflateMenu(R.menu.main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_settings:
                        openSettings();
                        return true;
                    case R.id.action_report_bug:
                        sendBugReport();
                        return true;
                    default:
                        return false;
                }
            }
        });

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
        setAdVisibility(true);
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(getActivity(),
                mainActivity.getDrawer(), toolbar,
                R.string.drawer_open,
                R.string.drawer_close);
    }

    private void openSettings() {
//        Intent intent = new Intent(getContext(), SettingsActivity.class);
//        startActivity(intent);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferencesFragment())
                .commit();
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

    private void setAdVisibility(boolean disabled) {
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

    private void sendBugReport() {
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
        frag.setHatchAdapter(breedingManager.getInterfaceStoredPokemonList(), getContext());
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


        jsonString = gson.toJson(breedingManager.hasEverstone());
        prefEditor.putString("jsonHasEverstone", jsonString);

        jsonString = gson.toJson(breedingManager.considerNature());
        prefEditor.putString("jsonConsiderNature", jsonString);

        jsonString = gson.toJson(breedingManager.considerAbility());
        prefEditor.putString("jsonConsiderAbility", jsonString);

        jsonString = gson.toJson(breedingManager.hasDestinyKnot());
        prefEditor.putString("jsonMaleItem", jsonString);

        ChanceFragment l = (ChanceFragment) getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        jsonString = gson.toJson(l.getShinyOptions());
        prefEditor.putString("jsonShinyOptions", jsonString);

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
            breedingManager.replaceGoalObject(convertCompatPokemon(gson.fromJson(jsonString, PokemonInfo.class)));
            sharedPref.edit().remove("jsonCurrentGoal").apply();
        } else {
            jsonString = sharedPref.getString("jsonBreedingManagerGoal", null);
            if (jsonString != null) {
                breedingManager.replaceGoalObject(gson.fromJson(jsonString, StoredPokemon.class));
            }
        }

        //TODO: fazer opçao neutra de nature e ability pra nao foder compatibilidade
        jsonString = sharedPref.getString("jsonPokemonList", null);
        if (jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>() {
            }.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ArrayList<StoredPokemon> newList = new ArrayList<>(eggList.size());
            for (PokemonInfo pokemonInfo : eggList) {
                newList.add(convertCompatPokemon(pokemonInfo));
            }
            breedingManager.replaceStoredPokemonObjects(newList);
            //        sharedPref.edit().remove("jsonPokemonList").apply();
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


    @Override
    public void setDestinyKnot(boolean b) {
        breedingManager.setDestinyKnot(b);
        updateLuckFragment();
    }

    @Override
    public boolean isDestinyKnotActive() {
        //return ivManager.getMaleItem() == Item.DESTINY_KNOT;
        return breedingManager.hasDestinyKnot();
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
        breedingManager.setConsiderNature(b);
        updateLuckFragment();
    }

    @Override
    public boolean careAboutNatures() {
        return (breedingManager.hasEverstone() && breedingManager.considerNature());
    }

    @Override
    public void setEverstone(boolean b) {
        breedingManager.setEverstone(b);
        updateLuckFragment();
    }

    @Override
    public boolean getConsiderNatureStatus() {
        return breedingManager.considerNature();
    }

    @Override
    public boolean updateEverstoneStatus() {
        return breedingManager.hasEverstone();
    }

    @Override
    public void updateAbilityStatus(boolean b) {
        breedingManager.setConsiderAbility(b);
        updateLuckFragment();
    }

    @Override
    public boolean getConsiderAbilityStatus() {
        return breedingManager.considerAbility();
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
    public ArrayList<StoredPokemonFragment.InterfaceStoredPokemon> getInterfaceStoredPokemonList() {
        return breedingManager.getInterfaceStoredPokemonList();
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
    public ArrayList<Integer> getPokemonIds() {
        return breedingManager.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return breedingManager.getPokemonNames();
    }

    @Override
    public StoredPokemonViewerFragment.InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {
        return breedingManager.getInterfaceViewerPokemon(uuid);
    }

    @Override
    public String getPokemonName(int pokemonId) {
        return breedingManager.getPokemonName(pokemonId);
    }

    @Override
    public ModifierPokemonFragment.InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
        return breedingManager.getInterfaceModifierPokemon(uuid);
    }

    @Override
    public void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        breedingManager.storePokemon(pokemonId, genderId, IVs, natureId, abilitySlot);
        updatePokemonListFragment();
        updateLuckFragment();
    }

    @Override
    public void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        breedingManager.updateStoredPokemon(uuid, pokemonId, genderId, IVs, natureId, abilitySlot);
        updatePokemonListFragment();
        updateLuckFragment();
    }

    @Override
    public void removeStoredPokemon(UUID uuid) {
        breedingManager.removePokemon(uuid);
        //TODO: ver pq nao precisa de updatePokemonListFragment(); aqui
        updateLuckFragment();
    }

    @Override
    public ArrayList<ChancePokemonMatch> getChancesList() {
        return breedingManager.calculateBestMatches();
    }

    @Override
    public ChanceFragment.InterfaceChancePokemon getInterfaceChancePokemon(UUID uuid) {
        return breedingManager.getInterfaceChancePokemon(uuid);
    }

    @Override
    public GoalPokemonFragment.InterfaceGoalPokemon getInterfaceGoalPokemon() {
        return breedingManager.getInterfaceGoalPokemon();
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return breedingManager.getPokemonFamilyList(breedingManager.getGoalId());
    }

    @Override
    public int getInterfacePokemonPosition(UUID uuid) {
        StoredPokemonFragment frag = (StoredPokemonFragment) getChildFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        return frag.getInterfacePokemonPosition(uuid);
    }

    @Override
    public ArrayList<GoalPokemonFragment.NatureSpinnerAdapter.InterfaceNature> getInterfaceNatures() {
        return breedingManager.getInterfaceNatures();
    }
}
