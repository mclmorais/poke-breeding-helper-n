package marcelo.breguenait.breedinghelper;

//TODO: "x item is hindering your chance!"

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;
import com.nhaarman.supertooltips.ToolTipView;

import java.lang.reflect.Type;
import java.util.List;

import de.cketti.library.changelog.ChangeLog;

class Constants {
    public static final int DITTO_ID = 132;
}

public class MainActivity extends ActionBarActivity
        implements
        StoredPokemonFragment.OnPokemonListChanged,
        LuckFragment.UpdateLuckInterface,
        GoalIVsFragment.OnGoalUpdate,
        ToolTipView.OnToolTipViewClickedListener{

    private View cardAd;
    private AdView adView;

    private IvManager ivManager;
    private final Gson gson = new Gson();

    ToolTipView viewGoalIVsTooltip;
    ToolTipView viewAddPokemonsTooltip;
    ToolTipView viewEditPokemonTooltip;

    ToolTipRelativeLayout toolTipRelativeLayout;

    @Override
    public void onToolTipViewClicked(ToolTipView toolTipView) {
        if(toolTipView == viewGoalIVsTooltip) {
            saveBoolean("hasSeenTooltipGoalIVs", true);
            tooltipAddPokemons();
        }
        else if (toolTipView == viewAddPokemonsTooltip) {
            saveBoolean("hasSeenTooltipAddPokemons", true);
            tooltipEditPokemon();
        }
        else if (toolTipView == viewEditPokemonTooltip)
            saveBoolean("hasSeenTooltipEditPokemon",true);

    }

    void setTooltips() {
        toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.tooltipLayout);
        tooltipGoalIVs();

        if(ivManager.getStoredPokemonList().size() > 0)
            tooltipEditPokemon();

        if(ivManager.getGoalPokemon() != null) {
            int counter = 0;
            for (int i = 0; i < 6; i++) {
                counter += ivManager.getGoalPokemon().IVs[i];
            }
            if (counter > 0 && ivManager.getGoalPokemon().id > 0) {
                if (viewGoalIVsTooltip != null) {
                    viewGoalIVsTooltip.remove();
                    viewGoalIVsTooltip = null;
                }
                tooltipAddPokemons();
                saveBoolean("hasSeenTooltipGoalIVs", true);
            }
        }

        if(ivManager.getBestCombinations() != null && ivManager.getBestCombinations().size() > 0) {
            saveBoolean("hasSeenTooltipAddPokemons",true);
            if(viewAddPokemonsTooltip != null) {
                viewAddPokemonsTooltip.remove();
                viewAddPokemonsTooltip = null;
            }
        }


    }

    void tooltipGoalIVs() {

        if(readBoolean("hasSeenTooltipGoalIVs",false)){
            tooltipAddPokemons();
            return;
        }

        if(viewGoalIVsTooltip != null)
            return;

        ToolTip toolTip = new ToolTip()
                .withText("Select the Pokémon and the IVs " + System.getProperty("line.separator") + "you want as a goal below")
                .withColor(getResources().getColor(R.color.accent))
                .withShadow()
                .withTextColor(Color.WHITE)
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);


        GoalIVsFragment f = (GoalIVsFragment) getFragmentManager().findFragmentById(R.id.frameGoalIVsFragmentContainer);


        View v = f.getView();
        if(v != null) {
            CardView c = (CardView) v.findViewById(R.id.goalCardView);

            viewGoalIVsTooltip = toolTipRelativeLayout.showToolTipForView(toolTip, c);
            viewGoalIVsTooltip.setOnToolTipViewClickedListener(this);
        }
    }

    void tooltipAddPokemons() {
        if(readBoolean("hasSeenTooltipAddPokemons",false)) {
            tooltipEditPokemon();
            return;
        }

        if(viewAddPokemonsTooltip != null) return;

        ToolTip toolTip = new ToolTip()
                .withText("Add potential parents here and" + System.getProperty("line.separator") + "the app will tell you when" + System.getProperty("line.separator") + "a match is found")
                .withColor(getResources().getColor(R.color.accent))
                .withShadow()
                .withTextColor(Color.WHITE)
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        StoredPokemonFragment f = (StoredPokemonFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        View v = f.getView();
        if(v != null) {
            Button b = (Button) v.findViewById(R.id.buttonFragmentPokemonListAdd);
            viewAddPokemonsTooltip = toolTipRelativeLayout.showToolTipForView(toolTip,b);
            viewAddPokemonsTooltip.setOnToolTipViewClickedListener(this);


        }
    }

    void tooltipEditPokemon() {
        if(readBoolean("hasSeenTooltipEditPokemon",false)) {
            return;
        }

        if(viewEditPokemonTooltip != null) return;

        if(ivManager.getStoredPokemonList().isEmpty()) return;

        ToolTip toolTip = new ToolTip()
                .withText("Click on your stored Pokémon" + System.getProperty("line.separator") + "to see or edit its information")
                .withColor(getResources().getColor(R.color.accent))
                .withShadow()
                .withTextColor(Color.WHITE)
                .withAnimationType(ToolTip.AnimationType.FROM_TOP);

        StoredPokemonFragment f = (StoredPokemonFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        View v = f.getView();
        if(v != null) {
            ExpandableGridView b = (ExpandableGridView) v.findViewById(R.id.gridViewPokemonsList);
            viewEditPokemonTooltip = toolTipRelativeLayout.showToolTipForView(toolTip,b);
            viewEditPokemonTooltip.setOnToolTipViewClickedListener(this);


        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivManager = new IvManager();

        setSupportActionBar((Toolbar) findViewById(R.id.main_activity_toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
        StoredPokemonFragment fragList = (StoredPokemonFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        //fragList.setHatchAdapter(ivManager.getHatchesList(), getApplicationContext());
        fragList.setHatchAdapter(ivManager.getStoredPokemonList(), getApplicationContext());
        fragList.updateGridView();

        updateGoalIVsFragment();
        updateLuckFragment(ivManager.getBestCombinations());

        setTooltips();


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
                .addTestDevice("815CB1AC3DD5926E21AE260FC94A4D6A")
                .addTestDevice("8C7BA5C848E50217D49DACC26972F1B9")
                .addTestDevice("C4BE91EE53C4D54137B99B336B805908")
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
        StoredPokemonFragment storedStoredPokemonFragment = new StoredPokemonFragment();

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        storedStoredPokemonFragment.setArguments(getIntent().getExtras());



        // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                .add(R.id.framePokemonListFragmentContainer, storedStoredPokemonFragment).commit();


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
        StoredPokemonFragment frag = (StoredPokemonFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        frag.updateGridView();
    }
    void updateLuckFragment(List<ChanceData> c) {

        if(c == null) return;

        if(c.size() > 0) {
            saveBoolean("hasSeenTooltipAddPokemons",true);
            if(viewAddPokemonsTooltip != null) {
                viewAddPokemonsTooltip.remove();
                viewAddPokemonsTooltip = null;
            }
        }

        LuckFragment frag = (LuckFragment) getFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        frag.updateCurrentChances(c);

    }
    void updateGoalIVsFragment() {
        GoalIVsFragment frag = (GoalIVsFragment) getFragmentManager().findFragmentById(R.id.frameGoalIVsFragmentContainer);

        frag.refreshGoal(ivManager.getGoalPokemon());
    }


    public void addPokemonToList(PokemonInfo pokemon) {
        ivManager.storePokemon(pokemon);
        tooltipEditPokemon();
        updateLuckFragment(ivManager.getBestCombinations());
        updatePokemonListFragment();
    }

    @Override
    public void removePokemon(int position) {
        ivManager.removePokemon(position);
        updateLuckFragment(ivManager.getBestCombinations());
    }

    void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

        jsonString = gson.toJson(ivManager.getStoredPokemonList());
        prefEditor.putString("jsonPokemonList", jsonString);

        jsonString = gson.toJson(ivManager.hasEverstone());
        prefEditor.putString("jsonHasEverstone", jsonString);

        jsonString = gson.toJson(ivManager.considerNature());
        prefEditor.putString("jsonConsiderNature", jsonString);

        jsonString = gson.toJson(ivManager.getGoalPokemon());
        prefEditor.putString("jsonCurrentGoal",jsonString);

        jsonString = gson.toJson(ivManager.getMaleItem());
        prefEditor.putString("jsonMaleItem",jsonString);

        LuckFragment l = (LuckFragment) getFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
        jsonString = gson.toJson(l.getShinyOptions());
        prefEditor.putString("jsonShinyOptions",jsonString);

        prefEditor.apply();


    }
    void readData() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonString;

        jsonString = sharedPref.getString("jsonPokemonList",null);
        if(jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>(){}.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            ivManager.setStoredPokemonList(eggList);
        }

        jsonString = sharedPref.getString("jsonHasEverstone",null);
        if(jsonString != null) {
            ivManager.setEverstone(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonConsiderNature",null);
        if(jsonString != null) {
            ivManager.setConsiderNature(gson.fromJson(jsonString, Boolean.class));
        }

        jsonString = sharedPref.getString("jsonCurrentGoal",null);
        if(jsonString != null) {
            ivManager.setGoalPokemon(gson.fromJson(jsonString, PokemonInfo.class));
        }

        jsonString = sharedPref.getString("jsonCurrentGoal",null);
        if(jsonString != null) {
            ivManager.setGoalPokemon(gson.fromJson(jsonString, PokemonInfo.class));
        }

        jsonString = sharedPref.getString("jsonMaleItem",null);
        if(jsonString != null) {
            ivManager.setMaleItem(gson.fromJson(jsonString, Item.class));
        }

        jsonString = sharedPref.getString("jsonDittoList",null);
        if(jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>(){}.getType();
            List<PokemonInfo> dittoList = gson.fromJson(jsonString, type);
            for(int i = 0; i < dittoList.size(); i++) {
                PokemonInfo p = new PokemonInfo.Builder()
                        .id(Constants.DITTO_ID)
                        .gender(Gender.DITTO)
                        .IVs(dittoList.get(i).IVs)
                        .build();
                ivManager.storePokemon(p);
            }
            sharedPref.edit().remove("jsonDittoList").apply();
        }

        jsonString = sharedPref.getString("jsonEggList",null);
        if(jsonString != null) {
            Type type = new TypeToken<List<PokemonInfo>>(){}.getType();
            List<PokemonInfo> eggList = gson.fromJson(jsonString, type);
            for(int i = 0; i < eggList.size(); i++) {
                PokemonInfo p = new PokemonInfo.Builder()
                        .id(0)
                        .gender(eggList.get(i).gender)
                        .IVs(eggList.get(i).IVs)
                        .build();
                ivManager.storePokemon(p);
            }
            sharedPref.edit().remove("jsonEggList").apply();
        }
    }


    @Override
    public void updateGoal(PokemonInfo p) {
        ivManager.setGoalPokemon(p);

        int counter = 0;
        for(int i = 0; i < 6; i++) {
            counter += p.IVs[i];
        }
        if(counter > 0 && p.id > 0) {
            if(viewGoalIVsTooltip != null) {
                viewGoalIVsTooltip.remove();
                viewGoalIVsTooltip = null;
            }
            tooltipAddPokemons();
            saveBoolean("hasSeenTooltipGoalIVs",true);
        }
        updateLuckFragment(ivManager.getBestCombinations());
    }


    @Override
    public void setDestinyKnot(boolean b) {
        ivManager.setMaleItem(b?Item.DESTINY_KNOT:Item.NO_ITEM);
        updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean updateDestinyKnotChance() {
        return ivManager.getMaleItem()==Item.DESTINY_KNOT;
    }

    @Override
    public int loadShinyOptions() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String jsonString;

        jsonString = sharedPref.getString("jsonShinyOptions",null);
        if(jsonString != null) {
            return (gson.fromJson(jsonString, Integer.class));
        }
        else return 0;
    }


    @Override
    public PokemonInfo getGoalData() {
        return getGoal();
    }

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

    @Override
    public PokemonInfo getSelectedPokemonData(int position) {
        return ivManager.getStoredPokemon(position);
    }

    @Override
    public void editPokemon(PokemonInfo pokemon, int position) {
        ivManager.editPokemon(pokemon,position);
        updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public void updateNatureStatus(boolean b) {
        ivManager.setConsiderNature(b);
        updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean careAboutNatures() {
        return (ivManager.hasEverstone() && ivManager.considerNature());
    }

    @Override
    public void setEverstone(boolean b) {
        ivManager.setEverstone(b);
        updateLuckFragment(ivManager.getBestCombinations());
    }

    @Override
    public boolean getConsiderNatureStatus() {
        return ivManager.considerNature();
    }

    @Override
    public boolean updateEverstoneStatus() {
        return ivManager.hasEverstone();
    }
}
