package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.UUID;

import databasemanager.DatabaseConstants;


public class ModifierPokemonFragment extends EditorPokemonFragment {

    private static final String ARG_POS_X = "x";
    private static final String ARG_POS_Y = "y";

    private UUID receivedUUID;
    private InterfaceModifierPokemon interfaceModifierPokemon;

    private FeedDataModifyPokemon modifierFeederCallback;
    private UpdateModifyPokemon updaterFeederCallback;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param callerPos Parameter 1.
     * @return A new instance of fragment StoredPokemonViewerFragment.
     */
    public static ModifierPokemonFragment newInstance(int[] callerPos, UUID receivedUUID) {
        ModifierPokemonFragment fragment = new ModifierPokemonFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POS_X, callerPos[0]);
        args.putInt(ARG_POS_Y, callerPos[1]);
        fragment.receivedUUID = receivedUUID;
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        interfaceModifierPokemon = modifierFeederCallback.getInterfaceModifierPokemon(receivedUUID);

        fillInterfaceWithReceivedData();

        return v;
    }

    void fillInterfaceWithReceivedData() {
        for (int i = 0; i < checkBoxInputIVs.length; i++) {
            checkBoxInputIVs[i].setChecked(interfaceModifierPokemon.getIVs()[i] == 1);
        }
        selectedPokemonId = interfaceModifierPokemon.getPokemonId();

        selectedGenderId = interfaceModifierPokemon.getGenderId();

        selectedAbilitySlot = interfaceModifierPokemon.getAbilitySlot();

        selectedNatureId = interfaceModifierPokemon.getNatureId();

        feedInterface();

        pickSpinnersWithReceivedData();

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

    void pickSpinnersWithReceivedData() {
        feedAbilitySpinnerSelection(interfaceModifierPokemon.getAbilitySlot());
        feedNatureSpinnerSelection();
    }

    void finishFragment() {

//        boolean hasIVs = false;
//        for (CheckBox IVs : checkBoxInputIVs) {
//            if (IVs.isChecked()) hasIVs = true;
//        }
//        if (!hasIVs) {
//            showToast(getActivity().getString(R.string.message_select_one_iv));
//            return;
//        }
        if (selectedPokemonId <= 0) {
            showToast(getActivity().getString(R.string.message_select_pokemon));
            return;
        }

        int[] pokemonIVs = new int[6];
        for (int i = 0; i < 6; i++)
            pokemonIVs[i] = checkBoxInputIVs[i].isChecked() ? 1 : 0;

        updaterFeederCallback.updateStoredPokemon(receivedUUID, selectedPokemonId, selectedGenderId, pokemonIVs, selectedNatureId, selectedAbilitySlot);

        closeFragment();
    }

    @Override
    void setListeners() {
        super.setListeners();
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishFragment();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                modifierFeederCallback = (FeedDataModifyPokemon) activity;
            else
                modifierFeederCallback = (FeedDataModifyPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeedDataModifyPokemon");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterFeederCallback = (UpdateModifyPokemon) activity;
            else
                updaterFeederCallback = (UpdateModifyPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement UpdateModifyPokemon");
        }
    }


    interface FeedDataModifyPokemon {
        InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid);
    }

    interface UpdateModifyPokemon {
        void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);

    }

}
