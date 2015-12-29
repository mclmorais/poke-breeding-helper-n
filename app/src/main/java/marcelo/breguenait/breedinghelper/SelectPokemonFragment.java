package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class SelectPokemonFragment extends PopupDialogFragment {
    CardView mRevealView;
    private OnPokemonSelectedListener mCallback;
    private GridView gridViewSelector;
    private EditText editTextFilter;
    private InterfacePokemonSelectorAdapter interfaceSelectorAdapter;
    private CheckBox checkBoxCompatible;
    private Button buttonCancel;
    private PokemonInfo goalPokemon;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                mCallback = (OnPokemonSelectedListener) activity;
            else
                mCallback = (OnPokemonSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPokemonSelectedListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setRetainInstance(true);

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_select_pokemon, container, false);

        mRevealView = (CardView) view.findViewById(R.id.revealable);

        interfaceSelectorAdapter = new InterfacePokemonSelectorAdapter(getActivity().getApplicationContext());

        goalPokemon = mCallback.getGoal();


        if (goalPokemon != null)
            interfaceSelectorAdapter.setGoal(mCallback.getGoal());

        gridViewSelector = (GridView) view.findViewById(R.id.gridViewSelectPokemon);
        gridViewSelector.setAdapter(interfaceSelectorAdapter);
        gridViewSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PokemonDataBlock p = (PokemonDataBlock) interfaceSelectorAdapter.getItem(i);
                mCallback.onPokemonSelected(p.id);
                closeFragment();
            }
        });

        editTextFilter = (EditText) view.findViewById(R.id.editTextSelectPokemon);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                interfaceSelectorAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkBoxCompatible = (CheckBox) view.findViewById(R.id.checkBoxSelectPokemonCompatible);


        if (getArguments().getBoolean("showOnlyCompatible", false)) {
            if (goalPokemon != null) {
                checkBoxCompatible.setChecked(true);
                checkBoxCompatible.setEnabled(false);
                interfaceSelectorAdapter.showOnlyCompatible(true);
            }
        }

        if (mCallback.showOnlyBasic()) {
            interfaceSelectorAdapter.setShowOnlyBasic(mCallback.showOnlyBasic());
            checkBoxCompatible.setChecked(true);
            checkBoxCompatible.setText("Basic Pokémon");
        }

        checkBoxCompatible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (goalPokemon != null) {
                    interfaceSelectorAdapter.showOnlyCompatible(b);
                    interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());
                }
            }
        });


        interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());


        buttonCancel = (Button) view.findViewById(R.id.fragmentSelectPokemonButtonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });


        setDialogPosition();

        boolean showEggGroupFilter = mCallback.showEggGroupFilter();

        checkBoxCompatible.setEnabled(showEggGroupFilter);


//        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//            @Override
//            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//                v.removeOnLayoutChangeListener(this);
//                reveal();
//                gridViewSelector.setAdapter(interfaceSelectorAdapter);
//            }
//        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int margin = dpToPx(64);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(screenWidth - margin, screenHeight - margin);
        }
        //  reveal();
    }

    void reveal() {

        int cx = getArguments().getInt("x");
        int cy = getArguments().getInt("y");

        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        mRevealView.setVisibility(View.VISIBLE);
        animator.start();

//        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
//        anim.setInterpolator(new AccelerateDecelerateInterpolator());
//        anim.setDuration(500);
//        anim.start();

    }


    //IvCalculatorFragment baseActivity;
    public interface OnPokemonSelectedListener {
        void onPokemonSelected(int id);

        boolean showEggGroupFilter();

        boolean showOnlyBasic();

        PokemonInfo getGoal();
    }

}
