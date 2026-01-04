package marcelo.breguenait.breedinghelper;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import breedingmanager.NatureManager;
import databasemanager.DatabaseConstants;
import de.hdodenhof.circleimageview.CircleImageView;
import marcelo.breguenait.breedinghelper.R;


public class StoredPokemonViewerFragment extends PopupDialogFragment
        implements
        EditorPokemonFragment.FeedDataCreatePokemon,
        ModifierPokemonFragment.FeedDataModifyPokemon,
        ModifierPokemonFragment.UpdateModifyPokemon {

    private static final String ARG_POS_X = "x";
    private static final String ARG_POS_Y = "y";
    private int mPosX;
    private int mPosY;

    private UUID receivedUUID;

    private InterfaceViewerPokemon interfaceViewerPokemon;


    private int pokemonPos;

    private FeedDataPokemonViewer feederCallback;
    private UpdatePokemonViewer updaterCallback;

    private ImageView imageGender;
    private CircleImageView imagePokemonIcon;
    private TextView textPokemonName, textNature, textNumber, textAbility;
    private ImageView[] IVs = new ImageView[6];
    private PreloadedDrawables preloadedDrawables;

    public StoredPokemonViewerFragment() {
    }

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
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosX = getArguments().getInt(ARG_POS_X);
            mPosY = getArguments().getInt(ARG_POS_Y);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

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
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(width, height);
        }

        setDialogPosition();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            if (getParentFragment() == null) {
                feederCallback = (FeedDataPokemonViewer) context;
                updaterCallback = (UpdatePokemonViewer) context;
            } else {
                feederCallback = (FeedDataPokemonViewer) getParentFragment();
                updaterCallback = (UpdatePokemonViewer) getParentFragment();
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement callbacks");
        }
    }

    @Override
    protected void setDialogPosition() {
        if (getArguments() == null || getDialog() == null || getDialog().getWindow() == null) {
            return;
        }
        super.setDialogPosition();
    }

    private void setListeners(View v) {

        preloadedDrawables = new PreloadedDrawables(requireActivity().getApplicationContext());
        imagePokemonIcon = v.findViewById(R.id.imagePokemonPopupIcon);
        textPokemonName = v.findViewById(R.id.textPokemonPopupName);
        Button buttonClose = v.findViewById(R.id.buttonPokemonPopupClose);
        FloatingActionButton buttonEdit = v.findViewById(R.id.buttonPokemonPopupEdit);
        textNature = v.findViewById(R.id.textPokemonPopupNature);
        textNumber = v.findViewById(R.id.textPokemonPopupNumber);
        imageGender = v.findViewById(R.id.imagePokemonPopupGender);
        textAbility = v.findViewById(R.id.textPokemonPopupAbility);

        IVs[0] = v.findViewById(R.id.imagePokemonPopupHP);
        IVs[1] = v.findViewById(R.id.imagePokemonPopupATK);
        IVs[2] = v.findViewById(R.id.imagePokemonPopupDEF);
        IVs[3] = v.findViewById(R.id.imagePokemonPopupSATK);
        IVs[4] = v.findViewById(R.id.imagePokemonPopupSDEF);
        IVs[5] = v.findViewById(R.id.imagePokemonPopupSPD);


        buttonClose.setOnClickListener(v1 -> closeFragment());

        buttonEdit.setOnClickListener(this::openModifierPokemonFragment);
    }

    private void updateInterface() {
        int id = interfaceViewerPokemon.getPokemonId();


        if(DatabaseConstants.pokemonIdIsValid(id)) {
            String iconId = "pkmn_big_" + String.format("%03d", id);
            imagePokemonIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", requireActivity().getPackageName()));
            textPokemonName.setText(interfaceViewerPokemon.getPokemonName());
        }

        textNature.setText(interfaceViewerPokemon.getNatureName());


        for (int i = 0; i < 6; i++) {
            IVs[i].setBackground(preloadedDrawables.getIVDrawable(i, interfaceViewerPokemon.getIVs()[i] == 1));
        }

        int genderId = interfaceViewerPokemon.getGenderId();
        if (genderId == DatabaseConstants.MALE_ID)
            imageGender.setBackgroundResource(R.drawable.symbol_male);
        else if (genderId == DatabaseConstants.FEMALE_ID)
            imageGender.setBackgroundResource(R.drawable.symbol_female);
        else
            imageGender.setBackgroundResource(R.drawable.symbol_genderless);


        textNumber.setText("#" + String.valueOf(pokemonPos + 1));


        textAbility.setText(interfaceViewerPokemon.getAbilityName());

    }

    void openModifierPokemonFragment(View callerView) {
        FragmentManager fm = getParentFragmentManager();
        int[] callerViewPosition = new int[2];
        callerView.getLocationOnScreen(callerViewPosition);
        EditorPokemonFragment fragment = ModifierPokemonFragment.newInstance(callerViewPosition, receivedUUID);
        Bundle b = addPositionAsArguments(callerView);
        fragment.setArguments(b);
        fragment.show(fm, "ModifierPokemonFragment");
    }

    Bundle addPositionAsArguments(View v) {
        int[] callerViewPosition = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }


    @Override
    public LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId) {
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

    @Override
    public ArrayList<NatureManager.NatureVerbose> getInterfaceNatures() {
        return feederCallback.getInterfaceNatures();
    }

    public interface FeedDataPokemonViewer {

        LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId);

        int getGenderRate(int pokemonId);

        ArrayList<Integer> getPokemonFamilyList();

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid);

        String getPokemonName(int pokemonId);

        InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid);

        ArrayList<NatureManager.NatureVerbose> getInterfaceNatures();
    }


    public interface UpdatePokemonViewer {
        void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);
    }

    public static class InterfaceViewerPokemon {
        final private int pokemonId;
        final private int genderId;
        final private int[] IVs;
        final private String natureName;
        final private String abilityName;
        final private String pokemonName;

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

    private class PreloadedDrawables {
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];
        final Drawable maleIcon;
        final Drawable femaleIcon;
        final Drawable genderlessIcon;
        final Drawable missingno;

        private PreloadedDrawables(Context c) {

            maleIcon = ContextCompat.getDrawable(c, R.drawable.symbol_male);
            femaleIcon = ContextCompat.getDrawable(c, R.drawable.symbol_female);
            genderlessIcon = ContextCompat.getDrawable(c, R.drawable.symbol_genderless);

            missingno = ContextCompat.getDrawable(c, R.drawable.pkmn_missingno);

            IVActive[0] = ContextCompat.getDrawable(c, R.drawable.iv_circle_checked);
            IVActive[1] = ContextCompat.getDrawable(c, R.drawable.iv_triangle_checked);
            IVActive[2] = ContextCompat.getDrawable(c, R.drawable.iv_square_checked);
            IVActive[3] = ContextCompat.getDrawable(c, R.drawable.iv_heart_checked);
            IVActive[4] = ContextCompat.getDrawable(c, R.drawable.iv_star_checked);
            IVActive[5] = ContextCompat.getDrawable(c, R.drawable.iv_diamond_checked);

            IVInactive[0] = ContextCompat.getDrawable(c, R.drawable.iv_circle_clear);
            IVInactive[1] = ContextCompat.getDrawable(c, R.drawable.iv_triangle_clear);
            IVInactive[2] = ContextCompat.getDrawable(c, R.drawable.iv_square_clear);
            IVInactive[3] = ContextCompat.getDrawable(c, R.drawable.iv_heart_clear);
            IVInactive[4] = ContextCompat.getDrawable(c, R.drawable.iv_star_clear);
            IVInactive[5] = ContextCompat.getDrawable(c, R.drawable.iv_diamond_clear);
        }

        Drawable getIVDrawable(int position, boolean active) {
            if (active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }
}
