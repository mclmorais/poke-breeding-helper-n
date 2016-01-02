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

import java.util.ArrayList;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class SelectPokemonFragment extends PopupDialogFragment {
    CardView mRevealView;
    private OnPokemonSelectedListener updaterCallback;
    private EditText editTextFilter;
    private InterfacePokemonSelectorAdapter interfaceSelectorAdapter;

    private FeedDataSelectPokemon feederCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterCallback = (OnPokemonSelectedListener) activity;
            else
                updaterCallback = (OnPokemonSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPokemonSelectedListener");
        }

        try {

            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataSelectPokemon) activity;
            else
                feederCallback = (FeedDataSelectPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeedDataSelectPokemon");
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

        interfaceSelectorAdapter = new InterfacePokemonSelectorAdapter(
                getActivity().getApplicationContext(),
                feederCallback.getPokemonIds(),
                feederCallback.getPokemonNames());

        interfaceSelectorAdapter.setCompatiblePokemonList(feederCallback.getCompatiblePokemonList()); //TODO: ver se nao da pra fazer essa call somente se o fragment for pedir isso
        interfaceSelectorAdapter.setBasicPokemonList(feederCallback.getBasicPokemonList());
        interfaceSelectorAdapter.setPokemonFamilyList(feederCallback.getPokemonFamilyList());

        GridView gridViewSelector = (GridView) view.findViewById(R.id.gridViewSelectPokemon);
        gridViewSelector.setAdapter(interfaceSelectorAdapter);
        gridViewSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int p = (int) interfaceSelectorAdapter.getItem(i);
                updaterCallback.onPokemonSelected(p);
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

        CheckBox checkBoxCompatible = (CheckBox) view.findViewById(R.id.checkBoxSelectPokemonCompatible);


        if (getArguments().getBoolean("showOnlyCompatible", false)) {
            checkBoxCompatible.setChecked(true);
            checkBoxCompatible.setEnabled(false);
            interfaceSelectorAdapter.showOnlyCompatible(true);
        }

        if (updaterCallback.showOnlyBasic()) {
            interfaceSelectorAdapter.setShowOnlyBasic(updaterCallback.showOnlyBasic());
            checkBoxCompatible.setChecked(true);
            checkBoxCompatible.setText("Basic Pokémon"); //TODO: mudar pra sistema
        }

        checkBoxCompatible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                interfaceSelectorAdapter.showOnlyCompatible(b);
                interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());
            }
        });


        interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());


        Button buttonCancel = (Button) view.findViewById(R.id.fragmentSelectPokemonButtonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });


        setDialogPosition();

        boolean showEggGroupFilter = updaterCallback.showEggGroupFilter();

        checkBoxCompatible.setEnabled(showEggGroupFilter);

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


    //BreedingFragment baseActivity;
    public interface OnPokemonSelectedListener {
        void onPokemonSelected(int id);

        boolean showEggGroupFilter();

        boolean showOnlyBasic();

    }

    public interface FeedDataSelectPokemon {

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonFamilyList();

        ArrayList<Integer> getBasicPokemonList();

    }

}
