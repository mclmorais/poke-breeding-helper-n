package marcelo.breguenait.breedinghelper;
//TODO: fazer o showtotalchance ser opçao com "..." no card
//TODO: mudar card de chance para 99.99% | 1 in 9 eggs <-- Separator view
//TODO: "x item is hindering your chance!"

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
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
import java.util.List;

import de.cketti.library.changelog.ChangeLog;

class Constants {
    public static final int DITTO_ID = 132;
}

public class MainActivity extends ActionBarActivity
        implements
        StoredPokemonsFragment.OnPokemonListChanged,
        LuckFragment.TemporaryLuckInterface,
        GoalIVsFragment.OnGoalUpdate{

    private View cardAd;
    private AdView adView;

    private IvManager ivManager;
    private final Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivManager = new IvManager();

        setSupportActionBar((Toolbar) findViewById(R.id.main_activity_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readData();

        ivManager.updateBestCombination();

        createGoalIVsFragment(savedInstanceState);
        createPokemonListFragment(savedInstanceState);
        createChanceFragment(savedInstanceState);

//        cardMainIVs.refreshInterface();
//        cardChance.updateGoalIvChance();
//        cardChance.updateItems();
//        cardPokemonGrid.refreshItemsInterface();

        cardAd = findViewById(R.id.cardAd);
//        if(!sharedPref.getBoolean("hasSeenDittoTutorial",false)) {
//            dittoTutorial();
//        }

        ChangeLog cl = new ChangeLog(this);
        if (cl.isFirstRun()) {
            cl.getLogDialog().show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        StoredPokemonsFragment fragList = (StoredPokemonsFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        //fragList.setHatchAdapter(ivManager.getHatchesList(), getApplicationContext());
        fragList.setHatchAdapter(ivManager.getStoredPokemonList(), getApplicationContext());
        fragList.updateGridView();
        LuckFragment luckFragment = (LuckFragment) getFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);

        updateGoalIVsFragment();
        updateLuckFragment(ivManager.getBestCombinations());


    }
    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        setAdVisibility(sharedPref.getBoolean("adDisabled",false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
        Intent intent = new Intent(this,SettingsActivity.class);
        startActivity(intent);
    }

    void createAd() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-9350161103739995/6628696664");
        LinearLayout adListLayout = (LinearLayout) findViewById(R.id.cardLayoutAd);
        adListLayout.addView(adView);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        if(dpWidth < (320+32))
        {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            LinearLayout.LayoutParams currentMargin = (LinearLayout.LayoutParams) cardAd.getLayoutParams();
            params.setMargins(0,currentMargin.topMargin,0,currentMargin.bottomMargin);
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
                .build();

        adView.loadAd(adRequest);

    }
    void setAdVisibility(boolean disabled) {
        if(disabled) {
            cardAd.setVisibility(View.GONE);
            if(adView != null) {
                adView.setEnabled(false);
                adView.setVisibility(View.GONE);
            }

        }
        else {
            if(adView == null)
                createAd();
            cardAd.setVisibility(View.VISIBLE);
            adView.setEnabled(true);
            adView.setVisibility(View.VISIBLE);
        }
    }

    void sendBugReport() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"marcelofernandesmorais+bhbug@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "[Breeding Helper Bug Report]");

        String body = "Android version: " + Build.VERSION.RELEASE + " (" + Integer.toString(Build.VERSION.SDK_INT) + ") " + Build.PRODUCT + System.getProperty("line.separator") ;
        body += "Phone Model: " + Build.BRAND + " " +  Build.MODEL + System.getProperty("line.separator");
        body += "Bug Description: ";

        i.putExtra(Intent.EXTRA_TEXT   , body);
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


    public PokemonInfo         getGoal() {
        return ivManager.getGoalPokemon();
    }
    public boolean goalExists() {return ivManager.getGoalPokemon() != null;}

    void    saveBoolean(String key, Boolean value) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPref.edit();
        prefEditor.putBoolean(key,value);
        prefEditor.apply();
    }
    Boolean readBoolean(String key, Boolean assumedValue) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPref.getBoolean(key, assumedValue);

    }

    void createGoalIVsFragment(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if(findViewById(R.id.frameGoalIVsFragmentContainer) != null) {

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
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.frameGoalIVsFragmentContainer, firstFragment).commit();

        }
    }
    void createPokemonListFragment(Bundle savedInstanceState) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        StoredPokemonsFragment storedPokemonsFragment = new StoredPokemonsFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        storedPokemonsFragment.setArguments(getIntent().getExtras());



        // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                .add(R.id.framePokemonListFragmentContainer, storedPokemonsFragment).commit();


    }
    void createChanceFragment(Bundle savedInstanceState) {
        // However, if we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState != null) {
            return;
        }
        LuckFragment luckFragment = new LuckFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        luckFragment.setArguments(getIntent().getExtras());



        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.frameLuckFragmentContainer, luckFragment).commit();

    }

       // cardChance.updateGoalIvChance();
       // cardChance.updateEggChance();

    void updatePokemonListFragment() {
        StoredPokemonsFragment frag = (StoredPokemonsFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        frag.updateGridView();
    }
    void updateLuckFragment(List<ChanceData> c) {

        if(c == null) return;

        LuckFragment frag = (LuckFragment) getFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        frag.updateCurrentChances(c);

    }
    void updateGoalIVsFragment() {
        GoalIVsFragment frag = (GoalIVsFragment) getFragmentManager().findFragmentById(R.id.frameGoalIVsFragmentContainer);

        frag.refreshGoal(ivManager.getGoalPokemon());
    }


    public void addPokemonToList(PokemonInfo pokemon) {
        ivManager.storePokemon(pokemon);
        updateLuckFragment(ivManager.getBestCombinations());
        updatePokemonListFragment();
    }
    @Override
    public void removePokemon(int position) {
        ivManager.removePokemon(position);
    }

    @Deprecated
    void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

        jsonString = gson.toJson(ivManager.getStoredPokemonList());
        prefEditor.putString("jsonEggList", jsonString);

//        jsonString = gson.toJson(ivManager.getDittosList());
//        prefEditor.putString("jsonDittoList",jsonString);

//        jsonString = gson.toJson(ivManager.getMainIVs());
//        prefEditor.putString("jsonCurrentMainIVs",jsonString);

//        jsonString = gson.toJson(ivManager.getMaleIVs());
//        prefEditor.putString("jsonCurrentMaleIVs",jsonString);
//
//        jsonString = gson.toJson(ivManager.getFemaleIVs());
//        prefEditor.putString("jsonCurrentFemaleIVs",jsonString);
//
//        jsonString = gson.toJson(ivManager.getGoalIVs());
//        prefEditor.putString("jsonCurrentGoalIVs",jsonString);

//        jsonString = gson.toJson(ivManager.getActivePokemons());
//        prefEditor.putString("jsonCurrentActivePokemons",jsonString);

        jsonString = gson.toJson(ivManager.getGoalPokemon());
        prefEditor.putString("jsonCurrentGoal",jsonString);

//        jsonString = gson.toJson(ivManager.getMaleItem());
//        prefEditor.putString("jsonCurrentMaleItem",jsonString);
//
//        jsonString = gson.toJson(ivManager.getFemaleItem());
//        prefEditor.putString("jsonCurrentFemaleItem",jsonString);
//
//        jsonString = gson.toJson(ivManager.getShinyOptions());
//        prefEditor.putString("jsonCurrentShinyOptions",jsonString);

        prefEditor.apply();


    }
    @Deprecated
    void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //Map<String,?> keys = sharedPref.getAll();
        String jsonString;

        jsonString = sharedPref.getString("jsonEggList",null);
        if(jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>(){}.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ivManager.setStoredPokemonList(eggList);
        }

//        jsonString = sharedPref.getString("jsonDittoList",null);
//        if(jsonString != null) {
//            Type type = new TypeToken<List<HatchInfo>>(){}.getType();
//            List<HatchInfo> dittoList = gson.fromJson(jsonString, type);
//            ivManager.setDittosList(dittoList);
//        }

//        jsonString = sharedPref.getString("jsonCurrentMainIVs",null);
//        if(jsonString != null) {
//            ivManager.setMainIVs(gson.fromJson(jsonString,IvManager.MainIVs.class));
//        }

        jsonString = sharedPref.getString("jsonCurrentGoal",null);
        if(jsonString != null) {
            ivManager.setGoalPokemon(gson.fromJson(jsonString, PokemonInfo.class));
        }


//        jsonString = sharedPref.getString("jsonCurrentActivePokemons",null);
//        if(jsonString != null) {
//            ivManager.setActivePokemons(gson.fromJson(jsonString,ActivePokemons.class));
//        }
//
//        jsonString = sharedPref.getString("jsonCurrentMaleItem",null);
//        if(jsonString != null) {
//            ivManager.setMaleItem(gson.fromJson(jsonString,IvManager.item.class));
//        }
//
//        jsonString = sharedPref.getString("jsonCurrentFemaleItem",null);
//        if(jsonString != null) {
//            ivManager.setFemaleItem(gson.fromJson(jsonString,IvManager.item.class));
//        }
//
//        jsonString = sharedPref.getString("jsonCurrentShinyOptions",null);
//        if(jsonString != null) {
//            ivManager.setShinyOptions(gson.fromJson(jsonString,IvManager.ShinyOptions.class));
//        }

    }


    @Override
    public void updateGoal(PokemonInfo p) {
        ivManager.setGoalPokemon(p);
        updateLuckFragment(ivManager.getBestCombinations());
    }
}
