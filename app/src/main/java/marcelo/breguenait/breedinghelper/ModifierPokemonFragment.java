package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        if (DatabaseConstants.abilitySlotIsValid(abilitySlot)) {
            int position = -1;
            for (int i = 0; i < interfaceAbilities.size(); i++) {
                if (interfaceAbilities.get(i).abilitySlot == abilitySlot) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                spinnerAbility.setTag(position);
                spinnerAbility.setSelection(position);
            } else {
                Log.d("GOAL", "AbilitySlot " + abilitySlot +
                        " wasn't found in InterfaceAbilities.");
            }
        }
    }

    void pickSpinnersWithReceivedData() {
        feedAbilitySpinnerSelection(interfaceModifierPokemon.getAbilitySlot());
        feedNatureSpinnerSelection();
    }

    void finishFragment() {

        if (selectedPokemonId <= 0) {
            showToast(requireActivity().getString(R.string.message_select_pokemon));
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
        confirmButton.setOnClickListener(view -> finishFragment());
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            if (getParentFragment() != null) {
                modifierFeederCallback = (FeedDataModifyPokemon) getParentFragment();
                updaterFeederCallback = (UpdateModifyPokemon) getParentFragment();
            } else {
                modifierFeederCallback = (FeedDataModifyPokemon) context;
                updaterFeederCallback = (UpdateModifyPokemon) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement callbacks");
        }
    }


    interface FeedDataModifyPokemon {
        InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid);
    }

    interface UpdateModifyPokemon {
        void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);

    }

}
