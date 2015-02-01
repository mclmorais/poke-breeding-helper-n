package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AddPokemonPopupFragment extends PopupDialogFragment implements SelectPokemonFragment.OnPokemonSelectedListener{

    public interface OnBuildPokemon {
        void onBuildPokemon(PokemonInfo pokemon);
        PokemonInfo getGoal();
    }

    private int selectedPokemonId = 0;
    private Gender pokemonGender;

    private PokemonInfo selectedPokemon;

    private TextView selectedName;
    private ImageView selectedIcon;

    private ToggleButton togglePokemonGender;

    private Button confirmButton;
    private Button cancelButton;

    private final CheckBox[] checkBoxInputIVs = new CheckBox[6];

    private View buttonPokemonSelector;

    private OnBuildPokemon mCallback;

    private boolean showOnlyCompatible;

    private Spinner spinnerNature;

    void updateInterfacePokemon(int id) {
        selectedPokemonId = id;
        String text = PokemonData.getInstance().getName(id);
        selectedName.setText(text);

        String iconId = "pkmn_big_" + String.format("%03d", id);
        selectedIcon.setBackgroundResource(getResources().getIdentifier(iconId,"drawable",getActivity().getPackageName()));
    }

    public void setSelectedPokemon(PokemonInfo selectedPokemon) {
        this.selectedPokemon = selectedPokemon;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (OnBuildPokemon) activity;
            else
                mCallback = (OnBuildPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BuildPokemon");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view =  localInflater.inflate(R.layout.fragment_add_pokemon, container, false);

        buttonPokemonSelector = view.findViewById(R.id.buttonSelectPokemon);

        selectedName = (TextView) view.findViewById(R.id.textViewSelectedName);
        selectedIcon = (ImageView) view.findViewById(R.id.imageViewSelectedIcon);

        togglePokemonGender = (ToggleButton) view.findViewById(R.id.toggleButtonInputGender);

        confirmButton = (Button) view.findViewById(R.id.confirmIVsButton);
        cancelButton = (Button) view.findViewById(R.id.fragmentAddPokemonButtonCancel);

        checkBoxInputIVs[0]       = (CheckBox) view.findViewById(R.id.checkBoxInputHP);
        checkBoxInputIVs[1]       = (CheckBox) view.findViewById(R.id.checkBoxInputATK);
        checkBoxInputIVs[2]       = (CheckBox) view.findViewById(R.id.checkBoxInputDEF);
        checkBoxInputIVs[3]       = (CheckBox) view.findViewById(R.id.checkBoxInputSATK);
        checkBoxInputIVs[4]       = (CheckBox) view.findViewById(R.id.checkBoxInputSDEF);
        checkBoxInputIVs[5]       = (CheckBox) view.findViewById(R.id.checkBoxInputSPD);

        spinnerNature = (Spinner) view.findViewById(R.id.spinnerAddPokemonNature);

        showOnlyCompatible = getArguments().getBoolean("showOnlyCompatible", false);
        setGenderDisplay();
        setListeners();

        int receivedId = getArguments().getInt("defaultPokemon",0);
        if(receivedId != 0) {
            updateInterfacePokemon(receivedId);
            updatePokemonGender(receivedId);
        }

        if(selectedPokemon != null) {
            updateInterfacePokemon(selectedPokemon.id);
            updatePokemonGender(selectedPokemon.id);
            for(int i = 0; i < 6; i++) {
                checkBoxInputIVs[i].setChecked(selectedPokemon.IVs[i]==1);
            }
            if(selectedPokemon.gender != Gender.GENDERLESS)
                togglePokemonGender.setChecked(selectedPokemon.gender == Gender.MALE);
        }

        spinnerNature.setAdapter(new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.spinner_item, Nature.values()));

        setDialogPosition();
        return view;
    }

    void setListeners() {

        buttonPokemonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            openSelectPokemonFragment(view);
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean hasIVs = false;
                for(CheckBox IVs : checkBoxInputIVs) {
                    if(IVs.isChecked()) hasIVs = true;
                }
                if(!hasIVs) {
                    showToast("Select at least one IV.");
                    return;
                }
                if(selectedPokemonId == 0) {
                    showToast("Select a Pokemon.");
                    return;
                }


                mCallback.onBuildPokemon(buildPokemon());
                closeFragment();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
    }

    void setGenderDisplay(){
        Gender gender = (Gender) getArguments().getSerializable("Gender");

        if(gender == null)
            return;

        if (gender == Gender.MALE) {
            togglePokemonGender.setChecked(true);
            togglePokemonGender.setClickable(false);
            return;
        }

        if (gender == Gender.FEMALE) {
            togglePokemonGender.setChecked(false);
            togglePokemonGender.setClickable(false);
            return;
        }
        if(gender == Gender.GENDERLESS) {
            togglePokemonGender.setVisibility(View.INVISIBLE);
            togglePokemonGender.setClickable(false);
        }


    }

    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        b.putBoolean("showOnlyCompatible",showOnlyCompatible);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.setTargetFragment(this,0);
        selectPokemonFragment.show(fm,"");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }

    void updatePokemonGender(int id) {
        if(PokemonData.getInstance().getGenderRestriction(id) == GenderRestriction.NONE) {
            if(!togglePokemonGender.isClickable()) {
                togglePokemonGender.setBackgroundResource(R.drawable.ic_toggle_gender_selector);
                togglePokemonGender.setClickable(true);
            }
            pokemonGender = togglePokemonGender.isChecked() ? Gender.MALE : Gender.FEMALE;
        }
        else if (PokemonData.getInstance().getGenderRestriction(id) == GenderRestriction.GENDERLESS) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_genderless);
            togglePokemonGender.setClickable(false);
            pokemonGender = Gender.GENDERLESS;
        }
        else if (PokemonData.getInstance().getGenderRestriction(id) == GenderRestriction.DITTO) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_genderless);
            togglePokemonGender.setClickable(false);
            pokemonGender = Gender.DITTO;
        }
        else if (PokemonData.getInstance().getGenderRestriction(id) == GenderRestriction.MALE_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_male);
            togglePokemonGender.setClickable(false);
            pokemonGender = Gender.MALE;
        }
        else if (PokemonData.getInstance().getGenderRestriction(id) == GenderRestriction.FEMALE_ONLY) {
            togglePokemonGender.setBackgroundResource(R.drawable.symbol_female);
            togglePokemonGender.setClickable(false);
            pokemonGender = Gender.FEMALE;
        }

        togglePokemonGender.invalidate();
    }

    @Override
    public void onPokemonSelected(int id) {
        selectedPokemonId = id;
        updateInterfacePokemon(selectedPokemonId);
        updatePokemonGender(selectedPokemonId);
    }

    void showToast(String string) {
        Toast.makeText(getActivity().getApplicationContext(), string, Toast.LENGTH_LONG).show();
    }

    PokemonInfo buildPokemon() {
        int[] pokemonIVs = new int[6];
        for(int i = 0; i < 6; i++)
            pokemonIVs[i] = checkBoxInputIVs[i].isChecked()?1:0;

        updatePokemonGender(selectedPokemonId);

        return new PokemonInfo.Builder()
                .id(selectedPokemonId)
                .gender(pokemonGender)
                .IVs(pokemonIVs)
                .nature(Nature.valueOf(spinnerNature.getSelectedItem().toString()))
                .build();
    }

    @Override
    protected void setDialogPosition() {
        if(getArguments() == null) {
            return;
        }

        int sourceY = getArguments().getInt("y");

        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);

        WindowManager.LayoutParams params = window.getAttributes();

        params.y = sourceY -  dpToPx(24);

        window.setAttributes(params);
    }

    @Override
    public boolean showEggGroupFilter() {
        return true;
    }

    @Override
    public boolean showOnlyBasic() {
        return false;
    }

    @Override
    public PokemonInfo getGoal() {
        return mCallback.getGoal();
    }
}
