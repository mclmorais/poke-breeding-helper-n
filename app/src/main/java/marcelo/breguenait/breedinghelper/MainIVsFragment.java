package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Map;


//TODO: filtro "show compatible"

public class MainIVsFragment extends Fragment
        implements AddPokemonPopupFragment.BuildPokemon,
        ParentItemsFragment.UpdateItem
{

    private class PreloadedDrawables {
        Drawable maleIcon;
        Drawable femaleIcon;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];

        private PreloadedDrawables(Context c) {

            maleIcon = c.getResources().getDrawable(R.drawable.symbol_male);
            femaleIcon = c.getResources().getDrawable(R.drawable.symbol_female);

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

        Drawable getGenderDrawable(Gender gender) {
            if (gender == Gender.MALE)          return maleIcon;
            else if (gender == Gender.FEMALE)   return femaleIcon;
            else                                return maleIcon; //TODO: fazer genderless
        }

        Drawable getIVDrawable(int position, boolean active) {
            if(active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }
    View includeMaleIVs;
    View includeFemaleIVs;
    View includeGoalIVs;

    PreloadedDrawables preloadedDrawables;

    ImageView maleSymbol, femaleSymbol, goalSymbol;

    ImageView maleIcon;
    ImageView femaleIcon;
    ImageView goalIcon;
    ImageView[] maleIVs = new ImageView[6];
    ImageView[] femaleIVs = new ImageView[6];
    ImageView[] goalIVs = new ImageView[6];
    Button buttonAddMale;
    Button buttonAddFemale;
    Button buttonAddGoal;

    TextView labelNoMalePokemon;
    TextView labelMaleNature;
    View layoutMaleIVs;

    TextView labelNoFemalePokemon;
    TextView labelFemaleNature;
    View layoutFemaleIVs;

    TextView labelNoGoalPokemon;
    TextView labelGoalNature;
    View layoutGoalIVs;

    View buttonMaleItem, buttonFemaleItem, buttonGoalItem;

    View buttonRemoveMale;
    View buttonRemoveFemale;
    View buttonRemoveGoal;

    ImageView maleItemIcon, femaleItemIcon;

    Map<IvManager.item,Drawable> itemIcons = new Hashtable<IvManager.item,Drawable>();


    enum Waiting { MALEPARENT, FEMALEPARENT, GOAL, NONE}
    Waiting waiting = Waiting.NONE;
    Waiting waitingItem = Waiting.NONE;


    UpdateActivePokemons mCallback;

    public void refreshInterface(PokemonInfo goalPokemon, IvManager.item maleItem, IvManager.item femaleItem) {

        maleItemIcon.setBackground(itemIcons.get(maleItem));
        femaleItemIcon.setBackground(itemIcons.get(femaleItem));

/*        
            if(activePokemons.getMaleParent() != null) {
            labelNoMalePokemon.setVisibility(View.GONE);
            buttonAddMale.setVisibility(View.GONE);
            layoutMaleIVs.setVisibility(View.VISIBLE);
            labelMaleNature.setVisibility(View.VISIBLE);
            buttonRemoveMale.setVisibility(View.VISIBLE);

            buttonMaleItem.setVisibility(View.VISIBLE);


            maleIcon.setBackground(PokemonData.getInstance().getDrawableIdFromId(activePokemons.getMaleParent().id));
            for (int i = 0; i < 6; i++) {
                maleIVs[i].setBackground(preloadedDrawables.getIVDrawable(i, activePokemons.getMaleParent().IVs[i] != 0));
            }
        }
        else {
            labelNoMalePokemon.setVisibility(View.VISIBLE);
            buttonAddMale.setVisibility(View.VISIBLE);
            layoutMaleIVs.setVisibility(View.GONE);
            labelMaleNature.setVisibility(View.GONE);
            buttonRemoveMale.setVisibility(View.GONE);
         //   buttonMaleItem.setVisibility(View.GONE);
            maleIcon.setBackgroundResource(0);
        }

        if(activePokemons.getFemaleParent() != null) {
            labelNoFemalePokemon.setVisibility(View.GONE);
            buttonAddFemale.setVisibility(View.GONE);
            layoutFemaleIVs.setVisibility(View.VISIBLE);
            labelFemaleNature.setVisibility(View.VISIBLE);
            buttonRemoveFemale.setVisibility(View.VISIBLE);

            buttonFemaleItem.setVisibility(View.VISIBLE);


            femaleIcon.setBackground(PokemonData.getInstance().getDrawableIdFromId(activePokemons.getFemaleParent().id));
            for (int i = 0; i < 6; i++) {
                femaleIVs[i].setBackground(preloadedDrawables.getIVDrawable(i, activePokemons.getFemaleParent().IVs[i] != 0));
            }
        }
        else {
            labelNoFemalePokemon.setVisibility(View.VISIBLE);
            buttonAddFemale.setVisibility(View.VISIBLE);
            layoutFemaleIVs.setVisibility(View.GONE);
            labelFemaleNature.setVisibility(View.GONE);
            buttonRemoveFemale.setVisibility(View.GONE);
        //    buttonFemaleItem.setVisibility(View.GONE);
            femaleIcon.setBackgroundResource(0);
        }
*/

        buttonGoalItem.setVisibility(View.GONE);

        if(goalPokemon != null) {
            labelNoGoalPokemon.setVisibility(View.GONE);
            buttonAddGoal.setVisibility(View.GONE);
            layoutGoalIVs.setVisibility(View.VISIBLE);
            labelGoalNature.setVisibility(View.VISIBLE);
            buttonRemoveGoal.setVisibility(View.VISIBLE);

            goalIcon.setBackground(PokemonData.getInstance().getDrawableIdFromId(goalPokemon.id));
            for (int i = 0; i < 6; i++) {
                goalIVs[i].setBackground(preloadedDrawables.getIVDrawable(i, goalPokemon.IVs[i] != 0));
            }
        }
        else {
            labelNoGoalPokemon.setVisibility(View.VISIBLE);
            buttonAddGoal.setVisibility(View.VISIBLE);
            layoutGoalIVs.setVisibility(View.GONE);
            labelGoalNature.setVisibility(View.GONE);
            buttonRemoveGoal.setVisibility(View.GONE);
            goalIcon.setBackgroundResource(0);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (UpdateActivePokemons) activity;
            else
                mCallback = (UpdateActivePokemons) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        preloadedDrawables = new PreloadedDrawables(getActivity().getApplicationContext());

        itemIcons.put(IvManager.item.DESTINY_KNOT,getResources().getDrawable(R.drawable.ic_destiny_knot_active));
        itemIcons.put(IvManager.item.POWER_HP,getResources().getDrawable(R.drawable.ic_power_hp_active));
        itemIcons.put(IvManager.item.POWER_ATK,getResources().getDrawable(R.drawable.ic_power_atk_active));
        itemIcons.put(IvManager.item.POWER_DEF,getResources().getDrawable(R.drawable.ic_power_def_active));
        itemIcons.put(IvManager.item.POWER_SATK,getResources().getDrawable(R.drawable.ic_power_satk_active));
        itemIcons.put(IvManager.item.POWER_SDEF,getResources().getDrawable(R.drawable.ic_power_sdef_active));
        itemIcons.put(IvManager.item.POWER_SPD,getResources().getDrawable(R.drawable.ic_power_spd_active));
        itemIcons.put(IvManager.item.NO_ITEM,getResources().getDrawable(R.drawable.ic_held_icon));

        View view = inflater.inflate(R.layout.fragment_main_ivs, container, false);

        includeMaleIVs = view.findViewById(R.id.expandableMaleIVs);
        labelNoMalePokemon = (TextView) includeMaleIVs.findViewById(R.id.expandableMainIVsSelectedLabel);
        labelMaleNature = (TextView) includeMaleIVs.findViewById(R.id.expandableMainIVsNature);
        layoutMaleIVs = includeMaleIVs.findViewById(R.id.expandableMainIVsLayoutIVs);

        maleIcon = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsPokemonIcon);
        buttonAddMale = (Button) includeMaleIVs.findViewById(R.id.expandableMainIVsAdd);

        buttonRemoveMale = includeMaleIVs.findViewById(R.id.expandableMainIVsRemove);

        maleIVs[0] = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsHP);
        maleIVs[1] = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsATK);
        maleIVs[2] = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsDEF);
        maleIVs[3] = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsSATK);
        maleIVs[4] = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsSDEF);
        maleIVs[5] = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsSPD);

        maleSymbol = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsGenderIcon);

        buttonMaleItem = includeMaleIVs.findViewById(R.id.expandableMainIVsFrameItem);
        maleItemIcon = (ImageView) includeMaleIVs.findViewById(R.id.expandableMainIVsItemIcon);


        //---

        includeFemaleIVs = view.findViewById(R.id.expandableFemaleIVs);

        labelNoFemalePokemon = (TextView) includeFemaleIVs.findViewById(R.id.expandableMainIVsSelectedLabel);
        labelFemaleNature = (TextView) includeFemaleIVs.findViewById(R.id.expandableMainIVsNature);
        layoutFemaleIVs = includeFemaleIVs.findViewById(R.id.expandableMainIVsLayoutIVs);

        femaleIcon = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsPokemonIcon);

        buttonAddFemale = (Button) includeFemaleIVs.findViewById(R.id.expandableMainIVsAdd);

        buttonRemoveFemale = includeFemaleIVs.findViewById(R.id.expandableMainIVsRemove);

        femaleIVs[0] = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsHP);
        femaleIVs[1] = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsATK);
        femaleIVs[2] = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsDEF);
        femaleIVs[3] = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsSATK);
        femaleIVs[4] = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsSDEF);
        femaleIVs[5] = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsSPD);

        femaleSymbol = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsGenderIcon);
        femaleSymbol.setBackgroundResource(R.drawable.symbol_female);

        buttonFemaleItem = includeFemaleIVs.findViewById(R.id.expandableMainIVsFrameItem);
        femaleItemIcon = (ImageView) includeFemaleIVs.findViewById(R.id.expandableMainIVsItemIcon);
        
        //---

        includeGoalIVs = view.findViewById(R.id.expandableGoalIVs);

        labelNoGoalPokemon = (TextView) includeGoalIVs.findViewById(R.id.expandableMainIVsSelectedLabel);
        labelGoalNature = (TextView) includeGoalIVs.findViewById(R.id.expandableMainIVsNature);
        layoutGoalIVs = includeGoalIVs.findViewById(R.id.expandableMainIVsLayoutIVs);

        goalIcon = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsPokemonIcon);
        buttonAddGoal = (Button) includeGoalIVs.findViewById(R.id.expandableMainIVsAdd);
        buttonRemoveGoal = includeGoalIVs.findViewById(R.id.expandableMainIVsRemove);

        goalSymbol = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsGenderIcon);
        goalSymbol.setBackgroundResource(R.drawable.symbol_goal);
        goalIVs[0] = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsHP);
        goalIVs[1] = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsATK);
        goalIVs[2] = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsDEF);
        goalIVs[3] = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsSATK);
        goalIVs[4] = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsSDEF);
        goalIVs[5] = (ImageView) includeGoalIVs.findViewById(R.id.expandableMainIVsSPD);

        buttonGoalItem = includeGoalIVs.findViewById(R.id.expandableMainIVsFrameItem);









//        layoutFemaleActive = view.findViewById(R.id.layoutFemaleIVsActive);
//        layoutFemaleInactive = view.findViewById(R.id.layoutFemaleIVsInactive);
//
//        femaleIcon = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleIcon);
//        buttonAddFemale = (Button) view.findViewById(R.id.buttonAddFemale);
//
//        femaleIVs[0] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleHP);
//        femaleIVs[1] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleATK);
//        femaleIVs[2] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleDEF);
//        femaleIVs[3] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleSATK);
//        femaleIVs[4] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleSDEF);
//        femaleIVs[5] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentFemaleSPD);

        //---
        
//        layoutGoalActive = view.findViewById(R.id.layoutGoalIVsActive);
//        layoutGoalInactive = view.findViewById(R.id.layoutGoalIVsInactive);
//
//        goalIcon = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalIcon);
//        buttonAddGoal = (Button) view.findViewById(R.id.buttonAddGoal);
//
//        goalIVs[0] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalHP);
//        goalIVs[1] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalATK);
//        goalIVs[2] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalDEF);
//        goalIVs[3] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalSATK);
//        goalIVs[4] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalSDEF);
//        goalIVs[5] = (ImageView) view.findViewById(R.id.imageViewMainIVsFragmentGoalSPD);

        setListeners();

        return view;
    }

    void setListeners() {
        buttonAddMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting = Waiting.MALEPARENT;
                openPokemonPopupFragment(view, Gender.MALE);
            }
        });

        buttonAddFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting = Waiting.FEMALEPARENT;
                openPokemonPopupFragment(view, Gender.FEMALE);
            }
        });

        buttonAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waiting = Waiting.GOAL;
                openPokemonPopupFragment(view, Gender.GENDERLESS);
            }
        });

        buttonRemoveMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.removeMaleParent();
            }
        });

        buttonRemoveFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.removeFemaleParent();
            }
        });

        buttonRemoveGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.removeGoal();
            }
        });

        buttonMaleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingItem = Waiting.MALEPARENT;
                openParentItemsFragment(view, "male");
            }
        });

        buttonFemaleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingItem = Waiting.FEMALEPARENT;
                openParentItemsFragment(view, "female");
            }
        });
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }



    @Override
    public void onBuildPokemon(PokemonInfo pokemon) {
        if(waiting == Waiting.MALEPARENT)
            mCallback.updateMaleParent(pokemon);
        else if (waiting == Waiting.FEMALEPARENT)
            mCallback.updateFemaleParent(pokemon);
        else
            mCallback.updateGoal(pokemon);

        waiting = Waiting.NONE;
    }


    void openParentItemsFragment(View callerView, String gender){
        FragmentManager fm = getFragmentManager();
        ParentItemsFragment parentItemsFragment = new ParentItemsFragment();

        Bundle b = addPositionAsArguments(callerView);
        b.putString("gender", gender);
        parentItemsFragment.setArguments(b);
        parentItemsFragment.setTargetFragment(this,0);
        parentItemsFragment.show(fm, "what");
    }

    void openPokemonPopupFragment(View view, Gender genderRestriction) {
        FragmentManager fm = getFragmentManager();
        AddPokemonPopupFragment fragment = new AddPokemonPopupFragment();
        Bundle b = addPositionAsArguments(view);
        b.putSerializable("Gender",genderRestriction);
        b.putBoolean("showOnlyCompatible", true);
        fragment.setArguments(b);
        fragment.setTargetFragment(this,0);
        fragment.show(fm,"");
    }

    @Override
    public void updateItem(IvManager.item item) {
        if(waitingItem == Waiting.MALEPARENT)
            mCallback.updateParentItem(Gender.MALE, item);
        else if (waitingItem == Waiting.FEMALEPARENT)
            mCallback.updateParentItem(Gender.FEMALE,item);
        else
            throw new IllegalArgumentException("Item received for unknown recipient.");
    }

    interface UpdateActivePokemons {
        void updateMaleParent(PokemonInfo updatedMale);
        void updateFemaleParent(PokemonInfo updatedFemale);
        void updateGoal(PokemonInfo updatedGoal);
        void updateParentItem(Gender gender, IvManager.item item);
        void removeMaleParent();
        void removeFemaleParent();
        void removeGoal();
    }


}


