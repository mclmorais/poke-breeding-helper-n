package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Created by Marcelo on 18/12/2014.
 */
public class AddPokemonPopupFragment extends PopupDialogFragment implements SelectPokemonFragment.OnPokemonSelectedListener{

    public interface BuildPokemon {
        void onBuildPokemon(PokemonInfo pokemon);
    }

    int selectedPokemonId = 0;
    Gender pokemonGender;

    TextView selectedName;
    ImageView selectedIcon;

    ToggleButton togglePokemonGender;

    Button confirmButton;

    CheckBox[] checkBoxInputIVs = new CheckBox[6];

    View buttonPokemonSelector;

    BuildPokemon mCallback;

    boolean showOnlyCompatible;

    void updateInterfacePokemon(int id) {
        selectedPokemonId = id;
        String text = PokemonData.getInstance().getName(id);
        selectedName.setText(text);
        selectedIcon.setBackground(PokemonData.getInstance().getDrawableIdFromId(id));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (BuildPokemon) activity;
            else
                mCallback = (BuildPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement BuildPokemon");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.expandable_layout_3_pokemon_input_fragment_test, container, false);

        buttonPokemonSelector = view.findViewById(R.id.buttonSelectPokemon);

        selectedName = (TextView) view.findViewById(R.id.textViewSelectedName);
        selectedIcon = (ImageView) view.findViewById(R.id.imageViewSelectedIcon);

        togglePokemonGender = (ToggleButton) view.findViewById(R.id.toggleButtonInputGender);

        confirmButton = (Button) view.findViewById(R.id.confirmIVsButton);

        checkBoxInputIVs[0]       = (CheckBox) view.findViewById(R.id.checkBoxInputHP);
        checkBoxInputIVs[1]       = (CheckBox) view.findViewById(R.id.checkBoxInputATK);
        checkBoxInputIVs[2]       = (CheckBox) view.findViewById(R.id.checkBoxInputDEF);
        checkBoxInputIVs[3]       = (CheckBox) view.findViewById(R.id.checkBoxInputSATK);
        checkBoxInputIVs[4]       = (CheckBox) view.findViewById(R.id.checkBoxInputSDEF);
        checkBoxInputIVs[5]       = (CheckBox) view.findViewById(R.id.checkBoxInputSPD);

        showOnlyCompatible = getArguments().getBoolean("showOnlyCompatible", false);
        setGenderDisplay();
        setListeners();

        int receivedId = getArguments().getInt("defaultPokemon",0);
        if(receivedId != 0) {
            updateInterfacePokemon(receivedId);
            updatePokemonGender(receivedId);
        }

/*        togglePokemonGender.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(selectedPokemonId != 0) {
                    updatePokemonGender(selectedPokemonId);
                }
            }
        });*/

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


                if(selectedPokemonId != 0 && hasIVs) { //redundant
                    mCallback.onBuildPokemon(buildPokemon());
                    closeFragment();
                }
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

        if (gender == gender.FEMALE) {
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
            togglePokemonGender.setBackgroundResource(R.drawable.ic_toggle_gender_selector);
            togglePokemonGender.setClickable(true);
            pokemonGender = togglePokemonGender.isChecked()?Gender.MALE:Gender.FEMALE;
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


    }

    @Override
    public void onPokemonSelected(int id) {
        selectedPokemonId = id;
        updateInterfacePokemon(selectedPokemonId);
        updatePokemonGender(selectedPokemonId);
    }

    void showToast(String string) {
        //TODO: show short or long (decided by parameter)
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
                .build();
    }

}
