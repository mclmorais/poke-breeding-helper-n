package marcelo.breguenait.breedinghelper;
//TODO: fazer o showtotalchance ser opçao com "..." no card
//TODO: mudar card de chance para 99.99% | 1 in 9 eggs <-- Separator view
//TODO: "x item is hindering your chance!"

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class MainActivity extends FragmentActivity
        implements
        MainIVsFragment.UpdateActivePokemons,
        StoredPokemonsFragment.TempInterface{

    private View cardAd;
    private AdView adView;

    protected final CardChance  cardChance = new CardChance(); //TODO: Only one left to transform into a fragment
    private IvManager ivManager;
    private NewIvManager newIvManager;
    private final Gson gson = new Gson();


    class CardChance {
        IvManager.ChanceData chanceData;

        TextView        textViewChanceGoalIVs;

        View            expandableLayoutChanceEgg;
        ExpandAnimation chanceExpander;

        Button          buttonSwitchParentWithEggs;

        View            buttonLuck;
        ImageView       imageShinyStar, imageShinyCharm, imageMasudaMethod;
        View            layoutMaleSwitchInfo, layoutFemaleSwitchInfo;
        TextView        textNewSwitchMaleInfo, textNewSwitchFemaleInfo;
        TextView        textSwitchLuckInfo;
        ImageView       imageSwitchMaleIcon, imageSwitchFemaleIcon;
        View    frameMaleSwitchOutline, frameFemaleSwitchOutline;
        private void initialize() {

            /*Layout that tells the user what he should switch the parents with*/
            layoutMaleSwitchInfo = findViewById(R.id.layoutMaleSwitchInfo);
            layoutFemaleSwitchInfo = findViewById(R.id.layoutFemaleSwitchInfo);
            textNewSwitchMaleInfo = (TextView) findViewById(R.id.textNewSwitchMaleIcon);
            textNewSwitchFemaleInfo = (TextView) findViewById(R.id.textNewSwitchFemaleIcon);
            textSwitchLuckInfo = (TextView) findViewById(R.id.textLuckSwitchInfo);
            imageSwitchMaleIcon = (ImageView) findViewById(R.id.imageMaleSwitchIcon);
            imageSwitchFemaleIcon = (ImageView) findViewById(R.id.imageFemaleSwitchIcon);
            frameMaleSwitchOutline = findViewById(R.id.frameNewSwitchMaleIcon);
            frameFemaleSwitchOutline = findViewById(R.id.frameNewSwitchFemaleIcon);



            /*Finds the layout views for the on screen shinyOptionsStrings*/
            textViewChanceGoalIVs       = (TextView) findViewById(R.id.textViewChanceGoalIVs);

            buttonSwitchParentWithEggs  = (Button)   findViewById(R.id.buttonSwitchEggs);

            expandableLayoutChanceEgg = findViewById(R.id.expandableLayoutChanceEgg);

            /*Button (actually a frameLayout) which opens the shiny and m/f options*/
            buttonLuck = findViewById(R.id.frameLayoutLuckIcon);

            imageShinyStar = (ImageView) findViewById(R.id.imageViewShinyStar);
            imageShinyStar.setVisibility(View.INVISIBLE);
            imageShinyCharm = (ImageView) findViewById(R.id.imageViewShinyCharm);
            imageShinyCharm.setVisibility(View.INVISIBLE);
            imageMasudaMethod = (ImageView) findViewById(R.id.imageViewMasudaMethod);
            imageMasudaMethod.setVisibility(View.INVISIBLE);

            /*Links the expandable layout view to the animation*/
            chanceExpander = new ExpandAnimation(expandableLayoutChanceEgg);

            /*Sets the listener to switch the current IVs with the ones on the selected eggs*/
            buttonSwitchParentWithEggs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cardChance.switchEggsToMainIVs();

                    chanceExpander.collapse();
                }
            });

            /*Opens the luck and m/f options*/
            buttonLuck.setClickable(true);
            buttonLuck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openLuckOptionsFragment(v);
                }
            });

            /*Disables both swith info views until they're needed*/
            layoutMaleSwitchInfo.setVisibility(View.GONE);
            layoutFemaleSwitchInfo.setVisibility(View.GONE);


        }

        void updateGoalIvChance() {

            /*Doesn't show chance until at least one of the goal IVs is checked. Only exception is
            * when user wants shiny chance.*/
//            int count = 0;
//            for(int i = 0; i < 6; i++)
//                count += cardMainIVs.checkBoxGoalIVs[i].isChecked()?1:0;
//            if((count == 0) && !isShiny())
//            {
//                textViewChanceGoalIVs.setText("Select the goal IVs above");
//                return;
//            }

            boolean compatible = ivManager.checkParentsCompatibility();

            if(!compatible) {
                textViewChanceGoalIVs.setText("Parents are incompatible to goal (egg group)");
                return;
            }

            if(ivManager.getActivePokemons().getGoal() == null) {
                textViewChanceGoalIVs.setText("Please select a goal pokemon.");
                return;
            }

            if(ivManager.getActivePokemons().getMaleParent() == null || ivManager.getActivePokemons().getFemaleParent() == null) {
                textViewChanceGoalIVs.setText("Please select the parents.");
                return;
            }

            double chance = ivManager.getParentsChance();

            String chanceInPercent, chanceInEggs;
            if(chance > 0) {
                if (chance > 0.0001) {
                    chanceInPercent = String.format("%.2f", chance * 100);
                    chanceInEggs = String.format("%s", Math.round(1.0 / chance));
                } else {
                    chanceInPercent = "<0.01";
                    chanceInEggs = ">100000";
                }

                String chanceString = chanceInPercent + "% or 1 in " + chanceInEggs + " eggs";
                textViewChanceGoalIVs.setText(chanceString);
            }
            else
            {
                textViewChanceGoalIVs.setText("Impossible to get goal IVs with current parents");
            }
        }

        void updateEggChance() {
            /*Gets a set of information regarding the chance of the eggs when compared to
            * the current parents*/
            chanceData = ivManager.getBestCombinationChance();

            if(chanceData.status == IvManager.chanceStatus.ERROR) {
                chanceExpander.collapse();
                return;
            }

            textNewSwitchFemaleInfo.setTextColor(0xFF000000);
            textNewSwitchMaleInfo.setTextColor(0xFF000000);

            if(chanceData.status != IvManager.chanceStatus.ALL_WORSE_LUCK) {

                if (chanceData.status == IvManager.chanceStatus.EGG_WITH_PARENT) {
                /*If only one parent should be switched, updates the interface with the data and
                * expands the extra layout*/
                    int n = chanceData.firstNumber + 1;
                    Gender eggGender = ivManager.getHatch(chanceData.firstNumber).gender;
                    if (eggGender == Gender.MALE) {
                        layoutMaleSwitchInfo.setVisibility(View.VISIBLE);
                        layoutFemaleSwitchInfo.setVisibility(View.GONE);
                        textNewSwitchMaleInfo.setText(Integer.toString(n));
                        frameMaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);


                    } else {
                        layoutFemaleSwitchInfo.setVisibility(View.VISIBLE);
                        layoutMaleSwitchInfo.setVisibility(View.GONE);
                        textNewSwitchFemaleInfo.setText(Integer.toString(n));
                        frameFemaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                    }
                } else if (chanceData.status == IvManager.chanceStatus.EGG_WITH_ANOTHER_EGG) {
                /*If both parents should be switched, updates the interface with the data and
                * expands the extra layout*/
                    int first = chanceData.firstNumber + 1;
                    int second = chanceData.secondNumber + 1;

                    /*The values do not correspond to the genders - checking has to be done here*/
                    if(ivManager.getHatch(first - 1).gender == Gender.MALE) {
                        textNewSwitchMaleInfo.setText(Integer.toString(first));
                        textNewSwitchFemaleInfo.setText(Integer.toString(second));
                    }
                    else {
                        textNewSwitchMaleInfo.setText(Integer.toString(second));
                        textNewSwitchFemaleInfo.setText(Integer.toString(first));
                    }
                    frameMaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                    frameFemaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                    layoutMaleSwitchInfo.setVisibility(View.VISIBLE);
                    layoutFemaleSwitchInfo.setVisibility(View.VISIBLE);
                }
                else if (chanceData.status == IvManager.chanceStatus.DITTO_WITH_MALE_PARENT) {
                    int dittoNumber = chanceData.firstNumber + 1;

                    frameFemaleSwitchOutline.setBackgroundResource(R.drawable.ic_ditto_outline);

                    layoutFemaleSwitchInfo.setVisibility(View.VISIBLE);
                    layoutMaleSwitchInfo.setVisibility(View.GONE);
                    textNewSwitchFemaleInfo.setText(Integer.toString(dittoNumber));

                }
                else if (chanceData.status == IvManager.chanceStatus.DITTO_WITH_FEMALE_PARENT) {
                    int dittoNumber = chanceData.firstNumber + 1;

                    frameMaleSwitchOutline.setBackgroundResource(R.drawable.ic_ditto_outline);

                    layoutMaleSwitchInfo.setVisibility(View.VISIBLE);
                    layoutFemaleSwitchInfo.setVisibility(View.GONE);
                    textNewSwitchMaleInfo.setText(Integer.toString(dittoNumber));
                }
                else if (chanceData.status == IvManager.chanceStatus.DITTO_WITH_MALE_EGG) {
                    int dittoNumber = chanceData.firstNumber + 1;
                    int eggNumber = chanceData.secondNumber + 1;


                    textNewSwitchMaleInfo.setText(Integer.toString(eggNumber));
                    textNewSwitchFemaleInfo.setText(Integer.toString(dittoNumber));

                    frameMaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                    frameFemaleSwitchOutline.setBackgroundResource(R.drawable.ic_ditto_outline);
                    layoutMaleSwitchInfo.setVisibility(View.VISIBLE);
                    layoutFemaleSwitchInfo.setVisibility(View.VISIBLE);
                }
                else if (chanceData.status == IvManager.chanceStatus.DITTO_WITH_FEMALE_EGG) {
                    int dittoNumber = chanceData.firstNumber + 1;
                    int eggNumber = chanceData.secondNumber + 1;


                    textNewSwitchMaleInfo.setText(Integer.toString(dittoNumber));
                    textNewSwitchFemaleInfo.setText(Integer.toString(eggNumber));

                    frameMaleSwitchOutline.setBackgroundResource(R.drawable.ic_ditto_outline);
                    frameFemaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                    layoutMaleSwitchInfo.setVisibility(View.VISIBLE);
                    layoutFemaleSwitchInfo.setVisibility(View.VISIBLE);

                }
                else if (chanceData.status == IvManager.chanceStatus.EGG_SWAP_DITTO) {
                    int eggNumber = chanceData.firstNumber + 1;

                    if(ivManager.getHatch(eggNumber - 1).gender == Gender.MALE) {
                        //Put egg on male, swap male ditto for female slot
                        textNewSwitchMaleInfo.setText(Integer.toString(eggNumber));
                        textNewSwitchFemaleInfo.setTextColor(0xFFFFFFFF);
                        textNewSwitchFemaleInfo.setText("P");

                        frameMaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                        frameFemaleSwitchOutline.setBackgroundResource(R.drawable.symbol_ditto);

                        imageSwitchMaleIcon.setBackgroundResource(R.drawable.symbol_male);
                    }
                    else {
                        //Put egg on female, swap female ditto for male slot
                        textNewSwitchFemaleInfo.setText(Integer.toString(eggNumber));
                        textNewSwitchMaleInfo.setTextColor(0xFFFFFFFF);
                        textNewSwitchMaleInfo.setText("P");

                        frameFemaleSwitchOutline.setBackgroundResource(R.drawable.ic_egg_outline);
                        frameMaleSwitchOutline.setBackgroundResource(R.drawable.symbol_ditto);

                        imageSwitchFemaleIcon.setBackgroundResource(R.drawable.symbol_female);
                    }

                    layoutMaleSwitchInfo.setVisibility(View.VISIBLE);
                    layoutFemaleSwitchInfo.setVisibility(View.VISIBLE);
                }
                else if (chanceData.status == IvManager.chanceStatus.GENDERLESS_WITH_MALE_DITTO) {
                    //TODO: fazer!
                }
                else if (chanceData.status == IvManager.chanceStatus.GENDERLESS_WITH_FEMALE_DITTO) {
                    //TODO: fazer!
                }




                if (readBoolean("showBothPercentagesOnSwitch", false))
                    textSwitchLuckInfo.setText(String.format("%.2f", (chanceData.chance) * 100) + "%" + " (+" + String.format("%.2f", (chanceData.chance - ivManager.getParentsChance()) * 100) + "%)");
                else
                    textSwitchLuckInfo.setText("+" + String.format("%.2f", (chanceData.chance - ivManager.getParentsChance()) * 100) + "%");

                if(chanceExpander.isCollapsed()) {
                    chanceExpander.stopAnimation();
                    chanceExpander.expand();
                }

            }
            else {
                /*If no egg has a better chance than the current parents, collapses the expandable
                * layout*/
                chanceExpander.stopAnimation();
                chanceExpander.collapse();
            }
        }

        void updateItems() {
            if(ivManager.isShiny()) {

                imageShinyStar.setVisibility(View.VISIBLE);

                imageShinyCharm.setVisibility(isShinyCharmActive()?View.VISIBLE:View.INVISIBLE);
                imageMasudaMethod.setVisibility(isMasudaMethodActive()?View.VISIBLE:View.INVISIBLE);
            }
            else {
                imageShinyStar.setVisibility(View.INVISIBLE);
                imageShinyCharm.setVisibility(View.INVISIBLE);
                imageMasudaMethod.setVisibility(View.INVISIBLE);
            }

            updateGoalIvChance();
        }

        void switchEggsToMainIVs() {
            //TODO: passar esta lógica para IVmanager
            if(cardChance.chanceData == null) cardChance.chanceData = ivManager.getBestCombinationChance();

            if(cardChance.chanceData.status == IvManager.chanceStatus.EGG_WITH_PARENT) {
                HatchInfo egg = ivManager.getHatch(chanceData.firstNumber);
                ivManager.switchParent(egg.gender, chanceData.firstNumber);
            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.EGG_WITH_ANOTHER_EGG) {
                int firstEggNumber = chanceData.firstNumber;
                int secondEggNumber = chanceData.secondNumber;
                HatchInfo firstEgg = ivManager.getHatch(firstEggNumber);
                HatchInfo secondEgg = ivManager.getHatch(secondEggNumber);

                if(firstEgg.gender == Gender.MALE)
                    ivManager.switchBothParents(firstEggNumber, secondEggNumber);
                else
                    ivManager.switchBothParents(secondEggNumber,firstEggNumber);
//                ivManager.switchParent(firstEgg.gender,chanceData.firstNumber);
//                ivManager.switchParent(secondEgg.gender,chanceData.secondNumber);

            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.DITTO_WITH_MALE_PARENT) {

                HatchInfo ditto = ivManager.getHatch(chanceData.firstNumber);
                ivManager.switchParent(Gender.FEMALE,chanceData.firstNumber);
            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.DITTO_WITH_FEMALE_PARENT) {
                HatchInfo ditto = ivManager.getHatch(chanceData.firstNumber);
                ivManager.switchParent(Gender.MALE,chanceData.firstNumber);
            }
            else if (chanceData.status == IvManager.chanceStatus.DITTO_WITH_MALE_EGG) {

                int dittoNumber     = chanceData.firstNumber;
                int eggNumber       = chanceData.secondNumber;

                HatchInfo ditto   = ivManager.getHatch(dittoNumber);
                HatchInfo egg     = ivManager.getHatch(eggNumber);

                ivManager.switchBothParents(eggNumber, dittoNumber);
//                ivManager.switchParent(Gender.MALE,eggNumber);
//                ivManager.switchParent(Gender.FEMALE,dittoNumber);
            }
            else if (chanceData.status == IvManager.chanceStatus.DITTO_WITH_FEMALE_EGG) {

                int dittoNumber     = chanceData.firstNumber;
                int eggNumber       = chanceData.secondNumber;

                HatchInfo ditto   = ivManager.getHatch(dittoNumber);
                HatchInfo egg     = ivManager.getHatch(eggNumber);

                ivManager.switchBothParents(dittoNumber, eggNumber);
//                ivManager.switchParent(Gender.FEMALE,eggNumber);
//                ivManager.switchParent(Gender.MALE,dittoNumber);

            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.EGG_SWAP_DITTO) {

                int eggNumber    = chanceData.firstNumber;
                HatchInfo egg  = ivManager.getHatch(eggNumber);

                if(egg.gender == Gender.MALE) {
                    //Male Ditto -> Female
                    //Egg -> Male (Stores female back)
                    ivManager.swapParentsAndSwitch(Gender.MALE,eggNumber);
                }
                else if (egg.gender == Gender.FEMALE) {
                    /*Female Ditto -> Male
                    * Egg -> Female (Stores male back)*/
                    ivManager.swapParentsAndSwitch(Gender.FEMALE,eggNumber);
                }
                else {
                    throw new IllegalArgumentException("Egg to be switched should be either MALE or FEMALE");
                }
            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.GENDERLESS_WITH_MALE_DITTO) {
                ivManager.switchParent(Gender.FEMALE, chanceData.firstNumber);
            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.GENDERLESS_WITH_FEMALE_DITTO) {
                ivManager.switchParent(Gender.MALE, chanceData.firstNumber);
            }
            else if (cardChance.chanceData.status == IvManager.chanceStatus.GENDERLESS_AND_DITTO) {
                ivManager.switchBothParents(chanceData.firstNumber,chanceData.secondNumber);
            }

            cardChance.updateGoalIvChance();
            cardChance.updateEggChance();
            updateMainIVsFragment();
            updatePokemonListFragment();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivManager = new IvManager();
        newIvManager = new NewIvManager();

        cardChance.initialize();

        readData();

        createMainIVsFragment(savedInstanceState);
        createPokemonListFragment(savedInstanceState);

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
        updateMainIVsFragment();
        StoredPokemonsFragment fragList = (StoredPokemonsFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        //fragList.setHatchAdapter(ivManager.getHatchesList(), getApplicationContext());
        fragList.setHatchAdapter(newIvManager.getStoredPokemonList(), getApplicationContext());
        fragList.updateGridView();
//        cardChance.updateEggChance();

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

    void saveData() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();

        String jsonString;

        jsonString = gson.toJson(ivManager.getHatchesList());
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

        jsonString = gson.toJson(ivManager.getActivePokemons());
        prefEditor.putString("jsonCurrentActivePokemons",jsonString);

        jsonString = gson.toJson(ivManager.getMaleItem());
        prefEditor.putString("jsonCurrentMaleItem",jsonString);

        jsonString = gson.toJson(ivManager.getFemaleItem());
        prefEditor.putString("jsonCurrentFemaleItem",jsonString);

        jsonString = gson.toJson(ivManager.getShinyOptions());
        prefEditor.putString("jsonCurrentShinyOptions",jsonString);

        prefEditor.apply();


    }
    void readData() {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
       //Map<String,?> keys = sharedPref.getAll();
        String jsonString;

        jsonString = sharedPref.getString("jsonEggList",null);
        if(jsonString != null) {
            Type type = new TypeToken<List<HatchInfo>>(){}.getType();
            List<HatchInfo> eggList = gson.fromJson(jsonString, type);
            ivManager.setHatchesList(eggList);
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

        jsonString = sharedPref.getString("jsonCurrentActivePokemons",null);
       if(jsonString != null) {
           ivManager.setActivePokemons(gson.fromJson(jsonString,ActivePokemons.class));
       }

        jsonString = sharedPref.getString("jsonCurrentMaleItem",null);
        if(jsonString != null) {
            ivManager.setMaleItem(gson.fromJson(jsonString,IvManager.item.class));
        }

        jsonString = sharedPref.getString("jsonCurrentFemaleItem",null);
        if(jsonString != null) {
            ivManager.setFemaleItem(gson.fromJson(jsonString,IvManager.item.class));
        }

        jsonString = sharedPref.getString("jsonCurrentShinyOptions",null);
        if(jsonString != null) {
            ivManager.setShinyOptions(gson.fromJson(jsonString,IvManager.ShinyOptions.class));
        }

    }

    void openLuckOptionsFragment(View callerView) {
        FragmentManager fm = getFragmentManager();
        LuckOptionsFragment luckOptionsFragment = new LuckOptionsFragment();
        Bundle b = addPositionAsArguments(callerView);
        boolean shinyStatus[] = {isShiny(),isShinyCharmActive(),isMasudaMethodActive()};
        b.putBooleanArray("shinyStatus",shinyStatus);

        luckOptionsFragment.setArguments(b);

        luckOptionsFragment.show(fm,"");
    }
    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }

    public IvManager.item   getItem(Gender g) {
        IvManager.item it = null;
        if(g == Gender.MALE){
            it =  ivManager.getMaleItem();
        }
        if (g == Gender.FEMALE){
            it =  ivManager.getFemaleItem();
        }

        return it;
    }
    public void             setShinyCharmActive(boolean b) {
        ivManager.setShinyCharmActive(b);
        cardChance.updateItems();
    }
    public boolean          isShinyCharmActive() {return ivManager.isShinyCharmActive();}
    public void             setMasudaMethodActive(boolean b) {
        ivManager.setMasudaMethodActive(b);
        cardChance.updateItems();
    }
    public boolean          isMasudaMethodActive() {return ivManager.isMasudaMethodActive();
    }
    public void             setShiny(boolean b) {
        ivManager.setShiny(b);
        cardChance.updateItems();
    }
    public boolean          isShiny() {return ivManager.isShiny();}
    public HatchInfo         getGoal() {
        return ivManager.getActivePokemons().getGoal();
    }
    public boolean goalExists() {return ivManager.getActivePokemons().getGoal() != null;}

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

    void createMainIVsFragment(Bundle savedInstanceState) {
        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if(findViewById(R.id.frameMainIVsFragmentContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create a new Fragment to be placed in the activity layout
            MainIVsFragment firstFragment = new MainIVsFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getFragmentManager().beginTransaction()
                    .add(R.id.frameMainIVsFragmentContainer, firstFragment).commit();

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

    void updateMainIVsFragment() {
        MainIVsFragment frag = (MainIVsFragment) getFragmentManager().findFragmentById(R.id.frameMainIVsFragmentContainer);
        if(frag != null) {
            frag.refreshInterface(newIvManager.getGoalPokemon(), ivManager.getMaleItem(), ivManager.getFemaleItem());
        }

        cardChance.updateGoalIvChance();
        cardChance.updateEggChance();
    }
    void updatePokemonListFragment() {
        StoredPokemonsFragment frag = (StoredPokemonsFragment) getFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
        frag.updateGridView();
    }

    @Override
    public void updateMaleParent(PokemonInfo updatedMale) {
   //     ivManager.setMaleParent(updatedMale);
        updateMainIVsFragment();
    }
    @Override
    public void updateFemaleParent(PokemonInfo updatedFemale) {
     //   ivManager.setFemaleParent(updatedFemale);
        updateMainIVsFragment();
    }
    @Override
    public void updateGoal(PokemonInfo updatedGoal) {
        //ivManager.setGoal(updatedGoal);
        newIvManager.setGoalPokemon(updatedGoal);
        updateMainIVsFragment();
    }
    @Override
    public void removeMaleParent() {
        ivManager.removeMaleParent();
        updateMainIVsFragment();
        updatePokemonListFragment();
        Toast.makeText(this, "Male parent added back to the stored list.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void removeFemaleParent() {
        ivManager.removeFemaleParent();
        updateMainIVsFragment();
        updatePokemonListFragment();
        Toast.makeText(this, "Female parent added back to the stored list.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void removeGoal() {
        ivManager.removeGoal();
        updateMainIVsFragment();
        updatePokemonListFragment();
        Toast.makeText(this, "Goal pokemon removed.", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void updateParentItem(Gender gender, IvManager.item item) {
        if(gender == Gender.MALE)
            ivManager.setMaleItem(item);
        else if (gender == Gender.FEMALE)
            ivManager.setFemaleItem(item);
        else
            throw new IllegalArgumentException("Tried to change item without defining a valid parent.");
        updateMainIVsFragment();
    }
    @Override
    public void addPokemonTolist(PokemonInfo pokemon) {
        //ivManager.addHatch(pokemon);
        newIvManager.storePokemon(pokemon);
        ChanceData a = newIvManager.getBestCombination();
        updatePokemonListFragment();
        cardChance.updateEggChance();
    }
    @Override
    public void removePokemon(int position) {
        ivManager.removeHatch(position);
        cardChance.updateEggChance();
    }
}
