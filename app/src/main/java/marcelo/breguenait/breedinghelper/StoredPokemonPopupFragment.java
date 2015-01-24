package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link marcelo.breguenait.breedinghelper.StoredPokemonPopupFragment.OnPokemonPopupListener} interface
 * to handle interaction events.
 * Use the {@link StoredPokemonPopupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoredPokemonPopupFragment extends PopupDialogFragment implements AddPokemonPopupFragment.OnBuildPokemon{
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POS_X = "x";
    private static final String ARG_POS_Y = "y";

    private int mPosX;
    private int mPosY;

    private PokemonInfo selectedPokemon;
    private int pokemonPos;

    private OnPokemonPopupListener mListener;

    private ImageView imagePokemonIcon, imageGender;
    private TextView textPokemonName, textEggGroup1, textEggGroup2, textNature, textNumber;
    private Button buttonClose, buttonEdit;
    private ImageView[] IVs = new ImageView[6];

    private class PreloadedDrawables {
        Drawable maleIcon;
        Drawable femaleIcon;
        Drawable genderlessIcon;
        Drawable missingno;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];

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
            if (gender == Gender.MALE)          return maleIcon;
            else if (gender == Gender.FEMALE)   return femaleIcon;
            else  return genderlessIcon;
        }

        Drawable getIVDrawable(int position, boolean active) {
            if(active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }

    private PreloadedDrawables preloadedDrawables;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param callerPos Parameter 1.
     * @return A new instance of fragment StoredPokemonPopupFragment.
     */
    public static StoredPokemonPopupFragment newInstance(int[] callerPos, PokemonInfo pokemonInfo, int pokemonPos) {
        StoredPokemonPopupFragment fragment = new StoredPokemonPopupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS_X, callerPos[0]);
        args.putInt(ARG_POS_Y, callerPos[1]);
        fragment.selectedPokemon = pokemonInfo;
        fragment.setArguments(args);
        fragment.pokemonPos = pokemonPos;
        return fragment;
    }

    public StoredPokemonPopupFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View thisFragment = inflater.inflate(R.layout.fragment_stored_pokemon_popup, container, false);

        setListeners(thisFragment);
        updateInterface();

        return thisFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        int height = dpToPx(220);
        int width = dpToPx(250);

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width,height);
        }

        setDialogPosition();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mListener = (OnPokemonPopupListener) activity;
            else
                mListener = (OnPokemonPopupListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BuildPokemon");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    protected void setDialogPosition() {
        if(getArguments() == null) {
            return;
        }

        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP|Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = (int) convertPixelsToDp(metrics.widthPixels,getActivity().getApplicationContext());
        if(mPosX < (screenWidth/2)) {
            params.x = mPosX + dpToPx(40); // about half of confirm button size left of source view
            params.y = mPosY -  dpToPx(32+160); // above source view
        }
        else {
            params.x = mPosX + dpToPx(40); // about half of confirm button size left of source view
            params.y = mPosY -  dpToPx(32+160); // above source view
        }



        window.setAttributes(params);
    }

    private void setListeners(View v) {

        preloadedDrawables = new PreloadedDrawables(getActivity().getApplicationContext());
        imagePokemonIcon = (ImageView) v.findViewById(R.id.imagePokemonPopupIcon);
        textPokemonName = (TextView) v.findViewById(R.id.textPokemonPopupName);
        buttonClose = (Button) v.findViewById(R.id.buttonPokemonPopupClose);
        buttonEdit = (Button) v.findViewById(R.id.buttonPokemonPopupEdit);
        textEggGroup1 = (TextView) v.findViewById(R.id.textPokemonPopupEggGroup1);
        textEggGroup2 = (TextView) v.findViewById(R.id.textPokemonPopupEggGroup2);
        textNature = (TextView) v.findViewById(R.id.textPokemonPopupNature);
        textNumber = (TextView) v.findViewById(R.id.textPokemonPopupNumber);
        imageGender = (ImageView) v.findViewById(R.id.imagePokemonPopupGender);

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
                openAddPokemonFragment(v);
            }
        });
    }

    private void updateInterface() {
        int id = selectedPokemon.id;

        Drawable icon = PokemonData.getInstance().getDrawableFromId(id).getConstantState().newDrawable();
        String iconId = "pkmn_big_" + String.format("%03d", id);
        imagePokemonIcon.setBackgroundResource(getResources().getIdentifier(iconId,"drawable",getActivity().getPackageName()));

        String name = PokemonData.getInstance().getName(id);
        textPokemonName.setText(name);

        String eggGroup1 = PokemonData.getInstance().getFirstEggGroup(id).toString();
        eggGroup1 = eggGroup1.replaceAll("_"," ");
        textEggGroup1.setText(eggGroup1);


        String eggGroup2 = PokemonData.getInstance().getSecondEggGroup(id).toString();
        eggGroup2 = eggGroup2.replaceAll("_"," ");
        if(eggGroup2.equals("NONE")) eggGroup2 = "";
        textEggGroup2.setText(eggGroup2);

        String nature = selectedPokemon.nature.toString();
        if(nature.equals("UNKNOWN")) nature = "Nature not set";
        textNature.setText(nature);

        for(int i = 0; i < 6; i++) {
            IVs[i].setBackground(preloadedDrawables.getIVDrawable(i,selectedPokemon.IVs[i]==1));
        }

        Gender gender = selectedPokemon.gender;
        if(gender == Gender.MALE)
            imageGender.setBackgroundResource(R.drawable.symbol_male);
        else if (gender == Gender.FEMALE)
            imageGender.setBackgroundResource(R.drawable.symbol_female);
        else
            imageGender.setBackgroundResource(R.drawable.symbol_genderless);


        textNumber.setText(String.valueOf(pokemonPos+1));


    }

    void openAddPokemonFragment(View callerView){
        FragmentManager fm = getFragmentManager();
        AddPokemonPopupFragment fragment = new AddPokemonPopupFragment();
        Bundle b = addPositionAsArguments(callerView);
        fragment.setSelectedPokemon(selectedPokemon);
        fragment.setArguments(b);
        fragment.setTargetFragment(this,0);
        fragment.show(fm, "");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPokemonPopupListener {
        // TODO: Update argument type and name
        public void onPokemonAltered(PokemonInfo alteredPokemon, int position);
        public PokemonInfo getGoal();
    }


    @Override
    public void onBuildPokemon(PokemonInfo pokemon) {
        mListener.onPokemonAltered(pokemon,pokemonPos);
        selectedPokemon = pokemon;
        updateInterface();
    }

    @Override
    public PokemonInfo getGoal() {
        return mListener.getGoal();
    }
}
