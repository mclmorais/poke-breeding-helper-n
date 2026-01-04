package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;

import breedingmanager.NatureManager;
import databasemanager.DatabaseConstants;

public class CreatePokemonFragment extends DialogFragment {


    private UpdateCreatePokemon updaterCallback;
    private FeedDataCreatePokemon feederCallback;
    private int selectedPokemonId = -1;
    private int selectedGenderId = 2;
    private int selectedNatureId = -1;
    private int selectedAbilitySlot = -1;
    private ArrayList<NatureManager.NatureVerbose> natureVerboses;
    private ArrayList<InterfaceAbility> interfaceAbilities;
    private Spinner spinnerNature;
    private Spinner spinnerAbility;
    private CheckBox[] checkBoxInputIVs = new CheckBox[6];
    private Button confirmButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        InterfaceModifierPokemon interfaceModifierPokemon = feederCallback.getLastInterfaceModifierPokemon();
        int selectedPokemonId = interfaceModifierPokemon.getPokemonId();
        int selectedNatureId = interfaceModifierPokemon.getNatureId();
        int selectedAbilitySlot = interfaceModifierPokemon.getAbilitySlot();
        int selectedGenderId = interfaceModifierPokemon.getGenderId();

        // feedInterface();
        // feedNatureSpinnerSelection();
        // feedAbilitySpinnerSelection(selectedAbilitySlot);
        return v;
    }

    void feedNatureSpinnerSelection() {
        //If there isn't a value received from somewhere else, doesn't select anything
        if (selectedNatureId < 0) return;

        for (int i = 0; i < natureVerboses.size(); i++) {
            if (natureVerboses.get(i).id == selectedNatureId) {
                spinnerNature.setTag(i);
                spinnerNature.setSelection(i);
                return;
            }

        }
        Log.d("GoalFragment", "Received a pokemon with invalid nature");
    }

    void feedAbilitySpinnerSelection(int abilitySlot) {
        //If the slot is valid
        if (DatabaseConstants.abilitySlotIsValid(abilitySlot)) {
            //Searches the interfaceAbilities for a one that corresponds to the goal slot
            int position = -1;
            for (int i = 0; i < interfaceAbilities.size(); i++) {
                if (interfaceAbilities.get(i).abilitySlot == abilitySlot) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                //If it has been found, sets the spinner to that position
                spinnerAbility.setTag(position);
                spinnerAbility.setSelection(position);
            } else {
                Log.d("GOAL", "AbilitySlot " + String.valueOf(abilitySlot) +
                        " wasn't found in InterfaceAbilities.");
            }
        }
    }


    private void finishFragment() {

//        boolean hasIVs = false;
//        for (CheckBox IVs : checkBoxInputIVs) {
//            if (IVs.isChecked()) hasIVs = true;
//        }
//        if (!hasIVs) {
//            showToast(getActivity().getString(R.string.message_select_one_iv));
//            return;
//        }
//        if (selectedPokemonId <= 0) {
//            showToast(getActivity().getString(R.string.message_select_pokemon));
//            return;
//        }

        int[] pokemonIVs = new int[6];
        for (int i = 0; i < 6; i++)
            pokemonIVs[i] = checkBoxInputIVs[i].isChecked() ? 1 : 0;


        updaterCallback.storePokemon(
                selectedPokemonId,
                selectedGenderId,
                pokemonIVs, //TODO: fazer tudo atualizar uma variavel na hora ao inves de calcular aqui?
                selectedNatureId,
                selectedAbilitySlot);

        dismiss();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null) {
                targetFragment = getParentFragment();
            }
            if (targetFragment == null) {
                updaterCallback = (UpdateCreatePokemon) context;
                feederCallback = (FeedDataCreatePokemon) context;
            } else {
                updaterCallback = (UpdateCreatePokemon) targetFragment;
                feederCallback = (FeedDataCreatePokemon) targetFragment;
            }
        } catch (ClassCastException e) {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null) {
                targetFragment = getParentFragment();
            }
            throw new ClassCastException((targetFragment != null ? targetFragment.toString() : context.toString())
                    + " must implement callbacks");
        }
    }

    void setListeners() {
        if (confirmButton != null) {
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishFragment();
                }
            });
        }
    }

    public interface FeedDataCreatePokemon {
        InterfaceModifierPokemon getLastInterfaceModifierPokemon();
    }

    public interface UpdateCreatePokemon {
        void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);
    }
}
