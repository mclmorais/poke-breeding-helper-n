package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static marcelo.breguenait.breedinghelper.R.id.imageDynamicChanceSecondItem;


class InterfaceChancePokemon {
    private int pokemonId;
    private int genderId;
    private int[] IVs;
    private String natureName;
    private String abilityName;
    private boolean hasSameNatureAsGoal;

    public InterfaceChancePokemon(int pokemonId, int genderId, int[] IVs, String natureName, String abilityName, boolean hasSameNatureAsGoal) {
        this.pokemonId = pokemonId;
        this.genderId = genderId;
        this.IVs = IVs;
        this.natureName = natureName;
        this.abilityName = abilityName;
        this.hasSameNatureAsGoal = hasSameNatureAsGoal;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public int getGenderId() {
        return genderId;
    }

    public int[] getIVs() {
        return IVs;
    }

    public String getNatureName() {
        return natureName;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public boolean hasSameNatureAsGoal() {
        return hasSameNatureAsGoal;
    }
}

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link marcelo.breguenait.breedinghelper.LuckFragment.UpdateLuckInterface} interface
 * to handle interaction events.
 */
public class LuckFragment extends Fragment implements LuckOptionsFragment.OnLuckOptionsChange {
    public static final int FEMALE = 1;
    public static final int MALE = 2;
    public static final int GENDERLESS = 3;
    public static final int SHINY = 0x01;
    public static final int CHARM = 0x02;
    public static final int MASUDA = 0x04;
    private final List<View> interfaceChanceList = new ArrayList<>();
    private boolean showOnlyBestChance = true;
    private PreloadedDrawables preloadedDrawables;
    private UpdateLuckInterface mListener;
    private LinearLayout layoutChances;
    private LayoutInflater inflater2;
    private ToggleButton buttonExpandChances;
    private CheckBox checkBoxDestinyKnot, checkBoxEverstone;
    private ImageButton buttonOptions;
    private int shinyOptions;
    private FeederLuckData feederCallback;

    public LuckFragment() {
        // Required empty public constructor
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                mListener = (UpdateLuckInterface) activity;
            else
                mListener = (UpdateLuckInterface) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement UpdateLuckInterface");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeederLuckData) activity;
            else
                feederCallback = (FeederLuckData) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeederLuckData");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_luck, container, false);

        preloadedDrawables = new PreloadedDrawables(getActivity().getApplicationContext());
        layoutChances = (LinearLayout) v.findViewById(R.id.luckFragmentLayoutChances);

        inflater2 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        buttonExpandChances = (ToggleButton) v.findViewById(R.id.luckFragmentExpandCollapseButton);


        buttonExpandChances.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showOnlyBestChance = isChecked;
                updateCurrentChances();
            }
        });

        checkBoxDestinyKnot = (CheckBox) v.findViewById(R.id.luckFragmentCheckBoxDestinyKnot);
        checkBoxDestinyKnot.setChecked(feederCallback.isDestinyKnotActive());

        checkBoxDestinyKnot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String end = isChecked ? "enabled" : "disabled";
                int pos[] = new int[2];
                checkBoxDestinyKnot.getLocationOnScreen(pos);
                Toast t = Toast.makeText(getActivity().getApplicationContext(), "Destiny Knot " + end + ".", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, pos[1] - (int) convertDpToPixel(80, getActivity().getApplicationContext()));
                t.show();
                mListener.setDestinyKnot(isChecked);
            }
        });

        checkBoxEverstone = (CheckBox) v.findViewById(R.id.checkBoxLuckFragmentEverstone);

        checkBoxEverstone.setChecked(mListener.updateEverstoneStatus());

        checkBoxEverstone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String end = isChecked ? "enabled" : "disabled";
                int pos[] = new int[2];
                checkBoxEverstone.getLocationOnScreen(pos);
                Toast t = Toast.makeText(getActivity().getApplicationContext(), "Everstone " + end + ".", Toast.LENGTH_SHORT);
                t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, pos[1] - (int) convertDpToPixel(80, getActivity().getApplicationContext()));
                t.show();
                mListener.setEverstone(isChecked);
            }
        });

        buttonOptions = (ImageButton) v.findViewById(R.id.luckFragmentButtonOptions);
        buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLuckOptionsFragment();
            }
        });

        shinyOptions = mListener.loadShinyOptions();

        return v;

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateCurrentChances();
    }

    public void updateCurrentChances() {

        List<ChancePokemonMatch> chancePokemonMatchList = feederCallback.getChancesList();
        if (chancePokemonMatchList.isEmpty()) {
            layoutChances.removeAllViews();
        }


        if (layoutChances != null)
            layoutChances.removeAllViews();
        interfaceChanceList.clear();

        if (chancePokemonMatchList.isEmpty()) {
            int targetHeight = (int) convertDpToPixel(32, getActivity().getApplicationContext());
            View noMatch = inflater2.inflate(R.layout.text_no_matches, layoutChances, false);
            interfaceChanceList.add(noMatch);
            layoutChances.addView(noMatch);
            ResizeAnimation r = new ResizeAnimation(layoutChances, targetHeight);
            r.setInterpolator(new AccelerateDecelerateInterpolator());
            r.setDuration(300);
            layoutChances.startAnimation(r);

            buttonExpandChances.setEnabled(false);
            buttonExpandChances.setChecked(true);
            return;


        }

        /*Inflates generic chance views based on how many chances (up to a maximum)*/
        for (int i = 0; i < chancePokemonMatchList.size(); i++) {
            if (showOnlyBestChance) {
                if (i > 0) break;
            } else {
                if (i > 4) break;
            }


            /*Adds separators in between views*/
            if (!interfaceChanceList.isEmpty()) {
                View separator = new View(getActivity().getApplicationContext());
                ViewGroup.LayoutParams viewLp = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(1, getActivity().getApplicationContext()));
                separator.setLayoutParams(viewLp);
                separator.setBackgroundColor(getResources().getColor(R.color.background_light_gray));
                separator.setVisibility(View.VISIBLE);
                layoutChances.addView(separator);
            }

            View v = inflater2.inflate(R.layout.dynamic_view_layout_chance_data, layoutChances, false);
            layoutChances.addView(v);
            interfaceChanceList.add(v);
        }

        /*Decides on the expand/collapse button behavior based on how many chances there are*/
        if (chancePokemonMatchList.size() < 2) {
            buttonExpandChances.setEnabled(false);
            buttonExpandChances.setChecked(true);
        } else buttonExpandChances.setEnabled(true);

        //  LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)convertDpToPixel(72,getActivity().getApplicationContext()));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.chance_data_height));
        int margin = (int) convertDpToPixel(5, getActivity().getApplicationContext());
        params.setMargins(margin, margin, margin, margin);

        for (int i = 0; i < interfaceChanceList.size(); i++) {

            InterfaceChancePokemon firstPokemon = feederCallback.getInterfaceChancePokemon(chancePokemonMatchList.get(i).getFirstPokemonUUID());
            InterfaceChancePokemon secondPokemon = feederCallback.getInterfaceChancePokemon(chancePokemonMatchList.get(i).getSecondPokemonUUID());

            interfaceChanceList.get(i).setLayoutParams(params);

            TextView v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceFirstNumber);
            v.setText("0"); //TODO: fazer pegar o numero do storedpokemon fragment
//            v.setText(String.valueOf(chancePokemonMatchList.get(i).firstPokemonNumber+1));
//
            v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceSecondNumber);
//            v.setText(String.valueOf(chancePokemonMatchList.get(i).secondPokemonNumber+1));
            v.setText("0"); //TODO: fazer pegar o numero do storedpokemon fragment

            double chance = chancePokemonMatchList.get(i).getChance();
            chance = applyShinyChance(chance);
            v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChancePercentage);
            if (chance * 100 > 0.01)
                v.setText(String.format("%.2f", chance * 100) + "%");
            else
                v.setText("<0.01%");

            v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceEggs);
            double eggs = 1 / chance;
            if (eggs > 999)
                v.setText(">999 eggs");
            else
                v.setText("~" + String.format("%.0f", 1 / chance) + " eggs");


            ImageView firstIcon = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstIcon);
            //String iconId = "pkmn_big_" + String.format("%03d", chancePokemonMatchList.get(i).firstPokemon.id);
            // firstIcon.setBackgroundResource(getResources().getIdentifier(iconId,"drawable",getActivity().getPackageName()));


            firstIcon.setBackground(CachedPokemonIcons.getInstance().getIcon(firstPokemon.getPokemonId()).getConstantState().newDrawable());
            //firstIcon.setBackground(PokemonData.getInstance().getDrawableFromId(firstPokemon.getPokemonId()).getConstantState().newDrawable());

            ImageView secondIcon = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondIcon);
            //iconId = "pkmn_big_" + String.format("%03d", chancePokemonMatchList.get(i).secondPokemon.id);
            // secondIcon.setBackgroundResource(getResources().getIdentifier(iconId,"drawable",getActivity().getPackageName()));
            secondIcon.setBackground(CachedPokemonIcons.getInstance().getIcon(secondPokemon.getPokemonId()).getConstantState().newDrawable());
            //secondIcon.setBackground(PokemonData.getInstance().getDrawableFromId(secondPokemon.getPokemonId()).getConstantState().newDrawable());


            ImageView firstIVs[] = new ImageView[6];
            firstIVs[0] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstHP);
            firstIVs[1] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstATK);
            firstIVs[2] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstDEF);
            firstIVs[3] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSATK);
            firstIVs[4] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSDEF);
            firstIVs[5] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSPD);
            for (int j = 0; j < 6; j++) {
                firstIVs[j].setBackground(preloadedDrawables.getIVDrawable(j, firstPokemon.getIVs()[j] != 0));
            }

            ImageView secondIVs[] = new ImageView[6];
            secondIVs[0] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondHP);
            secondIVs[1] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondATK);
            secondIVs[2] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondDEF);
            secondIVs[3] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSATK);
            secondIVs[4] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSDEF);
            secondIVs[5] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSPD);

            for (int j = 0; j < 6; j++) {
                secondIVs[j].setBackground(preloadedDrawables.getIVDrawable(j, secondPokemon.getIVs()[j] != 0));
            }

            ImageView firstGender = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstGender);
            firstGender.setBackground(preloadedDrawables.getGenderDrawable(firstPokemon.getGenderId()));

            ImageView secondGender = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondGender);
            secondGender.setBackground(preloadedDrawables.getGenderDrawable(secondPokemon.getGenderId()));

            TextView number = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceNumber);
            number.setText(String.valueOf(i + 1));

            if ((shinyOptions & SHINY) == SHINY) {
                ImageView shiny = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceShinyIndicator);
                shiny.setVisibility(View.VISIBLE);
            }

            if (mListener.careAboutNatures()) {
                ImageView firstItem = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstItem);
                ImageView secondItem = (ImageView) interfaceChanceList.get(i).findViewById(imageDynamicChanceSecondItem);

                if (firstPokemon.hasSameNatureAsGoal())
                    firstItem.setBackgroundResource(R.drawable.ic_everstone_active);
                else if (secondPokemon.hasSameNatureAsGoal())
                    secondItem.setBackgroundResource(R.drawable.ic_everstone_active);
            }

            TextView firstNature = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceFirstNature);
            String natureName = firstPokemon.getNatureName();

            String abilityName;

            abilityName = firstPokemon.getAbilityName();


//            if (natureName.equals("Unset") && abilityName.equals("Unset")) {
//                firstNature.setText("Nat. & Abl. unset");
//            } else if (!natureName.equals("Unset")) {
//                String text = natureName;
//                if (!abilityName.equals("Unset")) {
//                    text += " | ";
//                    text += abilityName;
//
//                }
//                firstNature.setText(text);
//            } else {
//                firstNature.setText(abilityName);
//            }

            firstNature.setText(natureName + " | " + abilityName);

            TextView secondNature = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceSecondNature);
//            nature = chancePokemonMatchList.get(i).secondPokemon.nature;
//            if (nature == null) nature = Nature.UNSET;

//            natureName = "Unset";
//            for (int j = 0; j < Nature.values().length; j++) {//TODO: fazer isso em tudo q eh lugar
//                if (Nature.values()[j] == nature) {
//                    natureName = PokemonData.getInstance().getNatureName(j);
//                    break;
//                }
//            }
//            if (chancePokemonMatchList.get(i).secondPokemon.ability == PokemonData.getInstance().getFirstAbilityId(chancePokemonMatchList.get(i).secondPokemon.id))
//                abilityName = (PokemonData.getInstance().getFirstAbility(chancePokemonMatchList.get(i).secondPokemon.id));
//            else if (chancePokemonMatchList.get(i).secondPokemon.ability == PokemonData.getInstance().getSecondAbilityId(chancePokemonMatchList.get(i).secondPokemon.id))
//                abilityName = (PokemonData.getInstance().getSecondAbility(chancePokemonMatchList.get(i).secondPokemon.id));
//            else if (chancePokemonMatchList.get(i).secondPokemon.ability == PokemonData.getInstance().getHiddenAbilityId(chancePokemonMatchList.get(i).secondPokemon.id))
//                abilityName = (PokemonData.getInstance().getHiddenAbility(chancePokemonMatchList.get(i).secondPokemon.id));
//            else
//                abilityName = ("Unset");
//
//            if (natureName.equals("Unset") && abilityName.equals("Unset")) {
//                secondNature.setText("Nat. & Abl. unset");
//            } else if (!natureName.equals("Unset")) {
//                String text = natureName;
//                if (!abilityName.equals("Unset")) {
//                    text += " | ";
//                    text += abilityName;
//
//                }
//                secondNature.setText(text);
//            } else {
//                secondNature.setText(abilityName);
//            }

            natureName = secondPokemon.getNatureName();
            abilityName = secondPokemon.getAbilityName();

            secondNature.setText(natureName + " | " + abilityName);


//            TextView secondNature = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceSecondNature);
//            nature = chancePokemonMatchList.get(i).secondPokemon.nature;
//            if(nature == null) nature = Nature.UNSET;
//            for(int j = 0; j < Nature.values().length; j++) //TODO: fazer isso em tudo q eh lugar
//                if(Nature.values()[j] == nature) {
//                    String name = PokemonData.getInstance().getNatureName(j);
//                    if(!name.equals("Unset"))
//                        secondNature.setText(name);
//                    else
//                        secondNature.setText("");
//                    break;
//                }


        }
//        int targetHeight = (int) (interfaceChanceList.size()*convertDpToPixel(200,getActivity().getApplicationContext()));
        int targetHeight = (int) (interfaceChanceList.size() * getResources().getDimension(R.dimen.chance_data_height));
        targetHeight += interfaceChanceList.size() * convertDpToPixel(11, getActivity().getApplicationContext());
        ResizeAnimation r = new ResizeAnimation(layoutChances, targetHeight);
        r.setInterpolator(new DecelerateInterpolator());
        r.setDuration(300);
        layoutChances.startAnimation(r);
    }

    void openLuckOptionsFragment() {
        FragmentManager fm = getFragmentManager();
        LuckOptionsFragment luckOptionsFragment = new LuckOptionsFragment();
        Bundle b = addPositionAsArguments(buttonOptions);
        luckOptionsFragment.setArguments(b);
        luckOptionsFragment.setTargetFragment(this, 0);
        luckOptionsFragment.show(fm, "luckOptions");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }

    @Override
    public int getShinyStatus() {
        return shinyOptions;
    }

    double applyShinyChance(double normalChance) {
        if ((shinyOptions & SHINY) == SHINY) {
            if ((shinyOptions & MASUDA) == MASUDA) {
                if ((shinyOptions & CHARM) == CHARM)
                    normalChance /= 512.0d;
                else
                    normalChance /= 1638.0d;
            } else if ((shinyOptions & CHARM) == CHARM) {
                normalChance *= (3.0d / 4096.0d);
            } else {
                normalChance /= 4096.0d;
            }
        }

        return normalChance;


    }

    @Override
    public void changeShinyStatus(int bit, boolean add) {
        if (add)
            shinyOptions |= bit;
        else
            shinyOptions &= ~bit;

        updateCurrentChances();
    }

    int getShinyOptions() {
        return shinyOptions;
    }

    public interface UpdateLuckInterface {
        void setDestinyKnot(boolean b);



        int loadShinyOptions();

        boolean careAboutNatures();

        void setEverstone(boolean b);

        boolean updateEverstoneStatus();
    }

    public interface FeederLuckData {
        ArrayList<ChancePokemonMatch> getChancesList();

        InterfaceChancePokemon getInterfaceChancePokemon(UUID uuid);

        boolean isDestinyKnotActive();
    }

    private class PreloadedDrawables {


        final Drawable maleIcon;
        final Drawable femaleIcon;
        final Drawable genderlessIcon;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];


        private PreloadedDrawables(Context c) {

            maleIcon = c.getResources().getDrawable(R.drawable.symbol_male).getConstantState().newDrawable();
            femaleIcon = c.getResources().getDrawable(R.drawable.symbol_female).getConstantState().newDrawable();
            genderlessIcon = c.getResources().getDrawable(R.drawable.symbol_genderless);

            IVActive[0] = c.getResources().getDrawable(R.drawable.iv_circle_checked);
            IVActive[1] = c.getResources().getDrawable(R.drawable.iv_triangle_checked);
            IVActive[2] = c.getResources().getDrawable(R.drawable.iv_square_checked);
            IVActive[3] = c.getResources().getDrawable(R.drawable.iv_heart_checked);
            IVActive[4] = c.getResources().getDrawable(R.drawable.iv_star_checked);
            IVActive[5] = c.getResources().getDrawable(R.drawable.iv_diamond_checked);

            IVInactive[0] = c.getResources().getDrawable(R.drawable.iv_circle_clear);
            IVInactive[1] = c.getResources().getDrawable(R.drawable.iv_triangle_clear);
            IVInactive[2] = c.getResources().getDrawable(R.drawable.iv_square_clear);
            IVInactive[3] = c.getResources().getDrawable(R.drawable.iv_heart_clear);
            IVInactive[4] = c.getResources().getDrawable(R.drawable.iv_star_clear);
            IVInactive[5] = c.getResources().getDrawable(R.drawable.iv_diamond_clear);
        }

        Drawable getGenderDrawable(int genderId) {
            if (genderId == MALE) return maleIcon;
            else if (genderId == FEMALE) return femaleIcon;
            else return genderlessIcon;
        }

        Drawable getIVDrawable(int position, boolean active) {
            if (active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }

}
