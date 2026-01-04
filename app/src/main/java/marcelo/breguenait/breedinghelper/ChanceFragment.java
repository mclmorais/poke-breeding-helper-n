package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import breedingmanager.ChancePokemonMatch;
import customviews.ResizeAnimation;
import databasemanager.DatabaseConstants;
import marcelo.breguenait.breedinghelper.databinding.FragmentLuckBinding;

public class ChanceFragment extends Fragment implements ChanceOptionsFragment.OnLuckOptionsChange {


    public static final int SHINY = 0x01;
    public static final int CHARM = 0x02;
    public static final int MASUDA = 0x04;
    private final List<View> interfaceChanceList = new ArrayList<>();
    private boolean showOnlyBestChance = true;
    private PreloadedDrawables preloadedDrawables;
    private UpdateLuckInterface mListener;
    private FeederLuckData feederCallback;
    private FragmentLuckBinding binding;
    private int shinyOptions;

    public ChanceFragment() {
    }

    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);

    }


    public void setCallbacks(Fragment callbacks) {
        this.feederCallback = (FeederLuckData) callbacks;
        this.mListener = (UpdateLuckInterface) callbacks;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentLuckBinding.inflate(inflater, container, false);

        preloadedDrawables = new PreloadedDrawables(requireActivity().getApplicationContext());

        binding.luckFragmentExpandCollapseButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showOnlyBestChance = isChecked;
            updateCurrentChances();
        });

        binding.luckFragmentCheckBoxDestinyKnot.setChecked(feederCallback.isDestinyKnotActive());

        binding.luckFragmentCheckBoxDestinyKnot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String s;
            if (isChecked)
                s = requireActivity().getString(R.string.message_dk_enabled);
            else
                s = requireActivity().getString(R.string.message_dk_disabled);

            int[] pos = new int[2];
            binding.luckFragmentCheckBoxDestinyKnot.getLocationOnScreen(pos);
            Toast t = Toast.makeText(requireActivity().getApplicationContext(), s, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, pos[1] - (int) convertDpToPixel(80, requireActivity().getApplicationContext()));
            t.show();
            mListener.setDestinyKnot(isChecked);
        });

        binding.checkBoxLuckFragmentEverstone.setChecked(mListener.updateEverstoneStatus());

        binding.checkBoxLuckFragmentEverstone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String s;
            if (isChecked)
                s = "Everstone enabled.";
            else
                s = "Everstone disabled.";

            int[] pos = new int[2];
            binding.checkBoxLuckFragmentEverstone.getLocationOnScreen(pos);
            Toast t = Toast.makeText(requireActivity().getApplicationContext(), s, Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, pos[1] - (int) convertDpToPixel(80, requireActivity().getApplicationContext()));
            t.show();
            mListener.setEverstone(isChecked);
        });

        binding.luckFragmentButtonOptions.setOnClickListener(v -> openLuckOptionsFragment());

        shinyOptions = mListener.loadShinyOptions();

        return binding.getRoot();

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
            binding.luckFragmentLayoutChances.removeAllViews();
        }


        if (binding.luckFragmentLayoutChances != null)
            binding.luckFragmentLayoutChances.removeAllViews();
        interfaceChanceList.clear();

        if (chancePokemonMatchList.isEmpty()) {
            int targetHeight = (int) convertDpToPixel(32, requireActivity().getApplicationContext());
            View noMatch = getLayoutInflater().inflate(R.layout.text_no_matches, binding.luckFragmentLayoutChances, false);
            interfaceChanceList.add(noMatch);
            binding.luckFragmentLayoutChances.addView(noMatch);
            ResizeAnimation r = new ResizeAnimation(binding.luckFragmentLayoutChances, targetHeight);
            r.setInterpolator(new AccelerateDecelerateInterpolator());
            r.setDuration(300);
            binding.luckFragmentLayoutChances.startAnimation(r);

            binding.luckFragmentExpandCollapseButton.setEnabled(false);
            binding.luckFragmentExpandCollapseButton.setChecked(true);
            return;


        }

        for (int i = 0; i < chancePokemonMatchList.size(); i++) {
            if (showOnlyBestChance) {
                if (i > 0) break;
            } else {
                if (i > 4) break;
            }


            if (!interfaceChanceList.isEmpty()) {
                View separator = new View(requireActivity().getApplicationContext());
                ViewGroup.LayoutParams viewLp = new ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) convertDpToPixel(1, requireActivity().getApplicationContext()));
                separator.setLayoutParams(viewLp);
                separator.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background_light_gray));
                separator.setVisibility(View.VISIBLE);
                binding.luckFragmentLayoutChances.addView(separator);
            }

            View v = getLayoutInflater().inflate(R.layout.dynamic_view_layout_chance_data, binding.luckFragmentLayoutChances, false);
            binding.luckFragmentLayoutChances.addView(v);
            interfaceChanceList.add(v);
        }

        if (chancePokemonMatchList.size() < 2) {
            binding.luckFragmentExpandCollapseButton.setEnabled(false);
            binding.luckFragmentExpandCollapseButton.setChecked(true);
        } else binding.luckFragmentExpandCollapseButton.setEnabled(true);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.chance_data_height));
        int margin = (int) convertDpToPixel(5, requireActivity().getApplicationContext());
        params.setMargins(margin, margin, margin, margin);

        for (int i = 0; i < interfaceChanceList.size(); i++) {

            InterfaceChancePokemon firstPokemon = feederCallback.getInterfaceChancePokemon(chancePokemonMatchList.get(i).getFirstPokemonUUID());
            InterfaceChancePokemon secondPokemon = feederCallback.getInterfaceChancePokemon(chancePokemonMatchList.get(i).getSecondPokemonUUID());

            interfaceChanceList.get(i).setLayoutParams(params);

            TextView v = interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceFirstNumber);
            v.setText(String.valueOf(feederCallback.getInterfacePokemonPosition(chancePokemonMatchList.get(i).getFirstPokemonUUID()) + 1));

            v = interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceSecondNumber);
            v.setText(String.valueOf(feederCallback.getInterfacePokemonPosition(chancePokemonMatchList.get(i).getSecondPokemonUUID()) + 1));

            double chance = chancePokemonMatchList.get(i).getChance();
            chance = applyShinyChance(chance);
            v = interfaceChanceList.get(i).findViewById(R.id.textDynamicChancePercentage);
            if (chance * 100 > 0.01) {
                String percentChance = String.format("%.2f", chance * 100) + "%";
                v.setText(percentChance);
            } else {
                String percentChance = "<" + String.format("%.2f", 0.01d) + "%";
                v.setText(percentChance);
            }

            v = interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceEggs);
            double eggs = 1 / chance;
            if (eggs > 999)
                v.setText(R.string.label_more_than_999_eggs);
            else {
                String about = requireActivity().getString(R.string.label_about_number);
                String number = String.format("%.0f", 1 / chance);
                String finalString = about + number;
                v.setText(finalString);
            }

            ImageView firstIcon = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstIcon);
            firstIcon.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.pkmn_missingno));

            ImageView secondIcon = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondIcon);
            secondIcon.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.pkmn_missingno));


            ImageView[] firstIVs = new ImageView[6];
            firstIVs[0] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstHP);
            firstIVs[1] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstATK);
            firstIVs[2] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstDEF);
            firstIVs[3] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSATK);
            firstIVs[4] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSDEF);
            firstIVs[5] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSPD);
            for (int j = 0; j < 6; j++) {
                firstIVs[j].setBackground(preloadedDrawables.getIVDrawable(j, firstPokemon.getIVs()[j] != 0));
            }

            ImageView[] secondIVs = new ImageView[6];
            secondIVs[0] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondHP);
            secondIVs[1] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondATK);
            secondIVs[2] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondDEF);
            secondIVs[3] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSATK);
            secondIVs[4] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSDEF);
            secondIVs[5] = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSPD);

            for (int j = 0; j < 6; j++) {
                secondIVs[j].setBackground(preloadedDrawables.getIVDrawable(j, secondPokemon.getIVs()[j] != 0));
            }

            ImageView firstGender = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstGender);
            firstGender.setBackground(preloadedDrawables.getGenderDrawable(firstPokemon.getGenderId()));

            ImageView secondGender = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondGender);
            secondGender.setBackground(preloadedDrawables.getGenderDrawable(secondPokemon.getGenderId()));

            TextView number = interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceNumber);
            number.setText(String.valueOf(i + 1));

            if ((shinyOptions & SHINY) == SHINY) {
                ImageView shiny = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceShinyIndicator);
                shiny.setVisibility(View.VISIBLE);
            }

            if (mListener.careAboutNatures()) {
                ImageView firstItem = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstItem);
                ImageView secondItem = interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondItem);

                if (firstPokemon.hasSameNatureAsGoal())
                    firstItem.setBackgroundResource(R.drawable.ic_everstone_active);
                else if (secondPokemon.hasSameNatureAsGoal())
                    secondItem.setBackgroundResource(R.drawable.ic_everstone_active);
            }

            TextView firstNature = interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceFirstNature);
            String natureName = firstPokemon.getNatureName();

            String abilityName;

            abilityName = firstPokemon.getAbilityName();


            firstNature.setText(natureName + " | " + abilityName);

            TextView secondNature = interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceSecondNature);

            natureName = secondPokemon.getNatureName();
            abilityName = secondPokemon.getAbilityName();

            secondNature.setText(natureName + " | " + abilityName);


        }

        int targetHeight = (int) (interfaceChanceList.size() * getResources().getDimension(R.dimen.chance_data_height));
        targetHeight += interfaceChanceList.size() * convertDpToPixel(11, requireActivity().getApplicationContext());
        ResizeAnimation r = new ResizeAnimation(binding.luckFragmentLayoutChances, targetHeight);
        r.setInterpolator(new DecelerateInterpolator());
        r.setDuration(300);
        binding.luckFragmentLayoutChances.startAnimation(r);
    }

    private void openLuckOptionsFragment() {
        ChanceOptionsFragment chanceOptionsFragment = new ChanceOptionsFragment();
        Bundle b = addPositionAsArguments(binding.luckFragmentButtonOptions);
        chanceOptionsFragment.setArguments(b);
        chanceOptionsFragment.show(getParentFragmentManager(), "luckOptions");
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
    public int getShinyStatus() {
        return shinyOptions;
    }

    private double applyShinyChance(double normalChance) {
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

        int getInterfacePokemonPosition(UUID uuid);

        boolean isDestinyKnotActive();
    }

    public static class InterfaceChancePokemon {
        private final int pokemonId;
        private final int genderId;
        private final int[] IVs;
        private final String natureName;
        private final String abilityName;
        private final boolean hasSameNatureAsGoal;

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

    private class PreloadedDrawables {


        final Drawable maleIcon;
        final Drawable femaleIcon;
        final Drawable genderlessIcon;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];


        private PreloadedDrawables(Context c) {

            maleIcon = ContextCompat.getDrawable(c, R.drawable.symbol_male).getConstantState().newDrawable();
            femaleIcon = ContextCompat.getDrawable(c, R.drawable.symbol_female).getConstantState().newDrawable();
            genderlessIcon = ContextCompat.getDrawable(c, R.drawable.symbol_genderless).getConstantState().newDrawable();

            IVActive[0] = ContextCompat.getDrawable(c, R.drawable.iv_circle_checked).getConstantState().newDrawable();
            IVActive[1] = ContextCompat.getDrawable(c, R.drawable.iv_triangle_checked).getConstantState().newDrawable();
            IVActive[2] = ContextCompat.getDrawable(c, R.drawable.iv_square_checked).getConstantState().newDrawable();
            IVActive[3] = ContextCompat.getDrawable(c, R.drawable.iv_heart_checked).getConstantState().newDrawable();
            IVActive[4] = ContextCompat.getDrawable(c, R.drawable.iv_star_checked).getConstantState().newDrawable();
            IVActive[5] = ContextCompat.getDrawable(c, R.drawable.iv_diamond_checked).getConstantState().newDrawable();

            IVInactive[0] = ContextCompat.getDrawable(c, R.drawable.iv_circle_clear).getConstantState().newDrawable();
            IVInactive[1] = ContextCompat.getDrawable(c, R.drawable.iv_triangle_clear).getConstantState().newDrawable();
            IVInactive[2] = ContextCompat.getDrawable(c, R.drawable.iv_square_clear).getConstantState().newDrawable();
            IVInactive[3] = ContextCompat.getDrawable(c, R.drawable.iv_heart_clear).getConstantState().newDrawable();
            IVInactive[4] = ContextCompat.getDrawable(c, R.drawable.iv_star_clear).getConstantState().newDrawable();
            IVInactive[5] = ContextCompat.getDrawable(c, R.drawable.iv_diamond_clear).getConstantState().newDrawable();
        }

        Drawable getGenderDrawable(int genderId) {
            if (genderId == DatabaseConstants.MALE_ID) return maleIcon;
            else if (genderId == DatabaseConstants.FEMALE_ID) return femaleIcon;
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
