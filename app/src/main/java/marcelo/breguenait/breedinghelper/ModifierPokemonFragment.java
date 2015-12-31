package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import java.util.UUID;


class InterfaceModifierPokemon {

    private int pokemonId;
    private int genderId;
    private int[] IVs;
    private int natureId;
    private int abilitySlot;

    public InterfaceModifierPokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        this.pokemonId = pokemonId;
        this.genderId = genderId;
        this.IVs = IVs;
        this.natureId = natureId;
        this.abilitySlot = abilitySlot;
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

    public int getNatureId() {
        return natureId;
    }

    public int getAbilitySlot() {
        return abilitySlot;
    }
}

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
            checkBoxInputIVs[i].setChecked(interfaceModifierPokemon.getIVs()[i]==1);
        }
        selectedPokemonId = interfaceModifierPokemon.getPokemonId();

        selectedGenderId = interfaceModifierPokemon.getGenderId();

        selectedAbilitySlot = interfaceModifierPokemon.getAbilitySlot();

        selectedNatureId = interfaceModifierPokemon.getNatureId();

        updateInterface();

        pickSpinnersWithReceivedData();

    }

    void pickSpinnersWithReceivedData() {

        //TODO: mudar esse abilityslots q ta meio merda
        int position = 0;
        for (int i = 0; i < abilitySlots.size(); i++) {
            if(abilitySlots.get(i) == selectedAbilitySlot) {
                position = i;
                break;
            }
        }

        spinnerAbility.setSelection(position);

        spinnerNature.setSelection(selectedNatureId-1); //TODO: fazer de um jeito mais galo pra poder organizar os itens depois
    }

    void finishFragment() {

        boolean hasIVs = false;
        for (CheckBox IVs : checkBoxInputIVs) {
            if (IVs.isChecked()) hasIVs = true;
        }
        if (!hasIVs) {
            showToast("Select at least one IV.");
            return;
        }
        if (selectedPokemonId <= 0) {
            showToast("Select a Pokemon.");
            return;
        }

        int[] pokemonIVs = new int[6];
        for (int i = 0; i < 6; i++)
            pokemonIVs[i] = checkBoxInputIVs[i].isChecked() ? 1 : 0;

        int spinnerPosition = spinnerAbility.getSelectedItemPosition();
        int abilitySlot = abilitySlots.get(spinnerPosition);

        int natureId = spinnerNature.getSelectedItemPosition() + 1;

        updaterFeederCallback.updateStoredPokemon(receivedUUID,selectedPokemonId,selectedGenderId,pokemonIVs,natureId,abilitySlot);

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
