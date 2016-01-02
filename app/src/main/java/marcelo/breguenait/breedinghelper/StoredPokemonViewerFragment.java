package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


class InterfaceViewerPokemon {
    private int pokemonId;
    private int genderId;
    private int IVs[];
    private String natureName;
    private String abilityName;
    private String pokemonName;

    public InterfaceViewerPokemon(int pokemonId, int genderId, int[] IVs, String natureName, String abilityName, String pokemonName) {
        this.pokemonId = pokemonId;
        this.genderId = genderId;
        this.IVs = IVs;
        this.natureName = natureName;
        this.abilityName = abilityName;
        this.pokemonName = pokemonName;
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

    public String getPokemonName() {
        return pokemonName;
    }
}

public class StoredPokemonViewerFragment extends PopupDialogFragment
        implements
        EditorPokemonFragment.FeedDataCreatePokemon,
        ModifierPokemonFragment.FeedDataModifyPokemon,
        ModifierPokemonFragment.UpdateModifyPokemon {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String ARG_POS_X = "x";
    private static final String ARG_POS_Y = "y";
    private static int FEMALE = 1;
    private static int MALE = 2;
    private static int GENDERLESS = 3;
    private int mPosX;
    private int mPosY;

    private UUID receivedUUID;

    private InterfaceViewerPokemon interfaceViewerPokemon;


    private int pokemonPos;

    private FeedDataPokemonViewer feederCallback;
    private UpdatePokemonViewer updaterCallback;

    private ImageView imageGender;
    private CircleImageView imagePokemonIcon;
    private TextView textPokemonName, textEggGroup1, textEggGroup2, textNature, textNumber, textAbility;
    private Button buttonClose;
    private FloatingActionButton buttonEdit;
    private ImageView[] IVs = new ImageView[6];
    private PreloadedDrawables preloadedDrawables;

    public StoredPokemonViewerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param callerPos Parameter 1.
     * @return A new instance of fragment StoredPokemonViewerFragment.
     */
    public static StoredPokemonViewerFragment newInstance(int[] callerPos, UUID receivedUUID, int pokemonPos) {
        StoredPokemonViewerFragment fragment = new StoredPokemonViewerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS_X, callerPos[0]);
        args.putInt(ARG_POS_Y, callerPos[1]);
        fragment.receivedUUID = receivedUUID;
        fragment.setArguments(args);
        fragment.pokemonPos = pokemonPos;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //setRetainInstance(true);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mPosX = getArguments().getInt(ARG_POS_X);
                mPosY = getArguments().getInt(ARG_POS_Y);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //View thisFragment = inflater.inflate(R.layout.fragment_stored_pokemon_popup, container, false);

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View thisFragment = localInflater.inflate(R.layout.fragment_stored_pokemon_viewer, container, false);

        interfaceViewerPokemon = feederCallback.getInterfaceViewerPokemon(receivedUUID);

        setListeners(thisFragment);

        updateInterface();

        return thisFragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        int height = dpToPx(232);
        int width = dpToPx(250);

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, height);
        }

        setDialogPosition();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataPokemonViewer) activity;
            else
                feederCallback = (FeedDataPokemonViewer) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeedDataPokemonViewer");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterCallback = (UpdatePokemonViewer) activity;
            else
                updaterCallback = (UpdatePokemonViewer) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement UpdatePokemonViewer");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //TODO: ver se é necessário colocar null nos callbacks
    }

    @Override
    protected void setDialogPosition() {
        if (getArguments() == null) {
            return;
        }

        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP | Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = (int) convertPixelsToDp(metrics.widthPixels, getActivity().getApplicationContext());
        if (mPosX < (screenWidth / 2)) {
            params.x = mPosX + dpToPx(40); // about half of confirm button size left of source view
            params.y = mPosY - dpToPx(32 + 160); // above source view
        } else {
            params.x = mPosX + dpToPx(40); // about half of confirm button size left of source view
            params.y = mPosY - dpToPx(32 + 160); // above source view
        }


        window.setAttributes(params);
    }

    private void setListeners(View v) {

        preloadedDrawables = new PreloadedDrawables(getActivity().getApplicationContext());
        imagePokemonIcon = (CircleImageView) v.findViewById(R.id.imagePokemonPopupIcon);
        textPokemonName = (TextView) v.findViewById(R.id.textPokemonPopupName);
        buttonClose = (Button) v.findViewById(R.id.buttonPokemonPopupClose);
        buttonEdit = (FloatingActionButton) v.findViewById(R.id.buttonPokemonPopupEdit);
        textEggGroup1 = (TextView) v.findViewById(R.id.textPokemonPopupEggGroup1);
        textEggGroup2 = (TextView) v.findViewById(R.id.textPokemonPopupEggGroup2);
        textNature = (TextView) v.findViewById(R.id.textPokemonPopupNature);
        textNumber = (TextView) v.findViewById(R.id.textPokemonPopupNumber);
        imageGender = (ImageView) v.findViewById(R.id.imagePokemonPopupGender);
        textAbility = (TextView) v.findViewById(R.id.textPokemonPopupAbility);

        IVs[0] = (ImageView) v.findViewById(R.id.imagePokemonPopupHP);
        IVs[1] = (ImageView) v.findViewById(R.id.imagePokemonPopupATK);
        IVs[2] = (ImageView) v.findViewById(R.id.imagePokemonPopupDEF);
        IVs[3] = (ImageView) v.findViewById(R.id.imagePokemonPopupSATK);
        IVs[4] = (ImageView) v.findViewById(R.id.imagePokemonPopupSDEF);
        IVs[5] = (ImageView) v.findViewById(R.id.imagePokemonPopupSPD);


        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openModifierPokemonFragment(v);
            }
        });
    }

    private void updateInterface() {
        int id = interfaceViewerPokemon.getPokemonId();


        String iconId = "pkmn_big_" + String.format("%03d", id);
        imagePokemonIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));

        //String name = PokemonData.getInstance().getName(id);
        textPokemonName.setText(interfaceViewerPokemon.getPokemonName());

//        String eggGroup1 = PokemonData.getInstance().getFirstEggGroup(id).toString();
//        eggGroup1 = eggGroup1.replaceAll("_", " ");
//        textEggGroup1.setText(eggGroup1);
//
//
//        String eggGroup2 = PokemonData.getInstance().getSecondEggGroup(id).toString();
//        eggGroup2 = eggGroup2.replaceAll("_", " ");
//        if (eggGroup2.equals("NONE")) eggGroup2 = "";
//        textEggGroup2.setText(eggGroup2);

        textNature.setText(interfaceViewerPokemon.getNatureName());


        for (int i = 0; i < 6; i++) {
            IVs[i].setBackground(preloadedDrawables.getIVDrawable(i, interfaceViewerPokemon.getIVs()[i] == 1));
        }

        int genderId = interfaceViewerPokemon.getGenderId();
        if (genderId == MALE)
            imageGender.setBackgroundResource(R.drawable.symbol_male);
        else if (genderId == FEMALE)
            imageGender.setBackgroundResource(R.drawable.symbol_female);
        else
            imageGender.setBackgroundResource(R.drawable.symbol_genderless);


        textNumber.setText("#" + String.valueOf(pokemonPos + 1));


        textAbility.setText(interfaceViewerPokemon.getAbilityName());

//        if (selectedPokemon.ability == PokemonData.getInstance().getFirstAbilityId(selectedPokemon.id))
//            textAbility.setText(PokemonData.getInstance().getFirstAbility(selectedPokemon.id));
//        else if (selectedPokemon.ability == PokemonData.getInstance().getSecondAbilityId(selectedPokemon.id))
//            textAbility.setText(PokemonData.getInstance().getSecondAbility(selectedPokemon.id));
//        else if (selectedPokemon.ability == PokemonData.getInstance().getHiddenAbilityId(selectedPokemon.id))
//            textAbility.setText(PokemonData.getInstance().getHiddenAbility(selectedPokemon.id));
//        else
//            textAbility.setText("Unset");


//        for (int i = 0; i < Nature.values().length; i++)
//            if (Nature.values()[i] == selectedPokemon.nature) {
//                textNature.setText(PokemonData.getInstance().getNatureName(i));
//                break;
//            }

//        Nature nature = selectedPokemon.nature;
//        if(nature == null) nature = Nature.UNSET;
//        textNature.setText(nature.toString());


    }

    void openModifierPokemonFragment(View callerView) {
        FragmentManager fm = getFragmentManager();
        int callerViewPosition[] = new int[2];
        callerView.getLocationOnScreen(callerViewPosition);
        EditorPokemonFragment fragment = ModifierPokemonFragment.newInstance(callerViewPosition, receivedUUID);
        Bundle b = addPositionAsArguments(callerView);
        fragment.setArguments(b);
        fragment.setTargetFragment(this, 0);
        fragment.show(fm, "");
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
    public ArrayList<String> getListOfNatures() {
        return feederCallback.getListOfNatures();
    }

    @Override
    public HashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return feederCallback.getListOfAbilities(pokemonId);
    }

    @Override
    public int getGenderRate(int pokemonId) {
        return feederCallback.getGenderRate(pokemonId);
    }

    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return feederCallback.getCompatiblePokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return feederCallback.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return feederCallback.getPokemonNames();
    }

    @Override
    public String getPokemonName(int pokemonId) {
        return feederCallback.getPokemonName(pokemonId);
    }

    @Override
    public InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
        return feederCallback.getInterfaceModifierPokemon(uuid);
    }

    @Override
    public void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        updaterCallback.updateStoredPokemon(uuid, pokemonId, genderId, IVs, natureId, abilitySlot);
        interfaceViewerPokemon = feederCallback.getInterfaceViewerPokemon(uuid);
        updateInterface();
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return feederCallback.getPokemonFamilyList();
    }

    public interface FeedDataPokemonViewer {

        ArrayList<String> getListOfNatures();

        HashMap<Integer, String> getListOfAbilities(int pokemonId);

        int getGenderRate(int pokemonId);

        ArrayList<Integer> getPokemonFamilyList();
        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid);

        String getPokemonName(int pokemonId);

        InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid);
    }

    public interface UpdatePokemonViewer {
        void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);
    }

    private class PreloadedDrawables {
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];
        Drawable maleIcon;
        Drawable femaleIcon;
        Drawable genderlessIcon;
        Drawable missingno;

        private PreloadedDrawables(Context c) {

            maleIcon = c.getResources().getDrawable(R.drawable.symbol_male);
            femaleIcon = c.getResources().getDrawable(R.drawable.symbol_female);
            genderlessIcon = c.getResources().getDrawable(R.drawable.symbol_genderless);

            missingno = c.getResources().getDrawable(R.drawable.pkmn_missingno);

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
            if (gender == Gender.MALE) return maleIcon;
            else if (gender == Gender.FEMALE) return femaleIcon;
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
