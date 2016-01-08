package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//TODO: SALVAR ULTIMA POSIÇAO DE TUDO PRA EVITAR FADIGA
public class CreatePokemonFragment extends EditorPokemonFragment {


    private UpdateCreatePokemon updaterCallback;
    private FeedDataCreatePokemon feederCallback;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        InterfaceModifierPokemon interfaceModifierPokemon = feederCallback.getLastInterfaceModifierPokemon();
        selectedPokemonId = interfaceModifierPokemon.getPokemonId();
        selectedNatureId = interfaceModifierPokemon.getNatureId();
        selectedAbilitySlot = interfaceModifierPokemon.getAbilitySlot();
        selectedGenderId = interfaceModifierPokemon.getGenderId();

        feedInterface();
        feedNatureSpinnerSelection();
        feedAbilitySpinnerSelection(selectedAbilitySlot);
        return v;
    }

    void feedNatureSpinnerSelection() {
        //If there isn't a value received from somewhere else, doesn't select anything
        if (selectedNatureId < 0) return;

        for (int i = 0; i < interfaceNatures.size(); i++) {
            if (interfaceNatures.get(i).id == selectedNatureId) {
                spinnerNature.setTag(i);
                spinnerNature.setSelection(i);
                return;
            }

        }
        Log.d("GoalFragment", "Received a pokemon with invalid nature");
    }

    void feedAbilitySpinnerSelection(int abilitySlot) {
        //If the slot is valid
        if (abilitySlot > 0) {
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
                selectedPokemonId, //TODO: fazer tudo atualizar uma variavel na hora ao inves de calcular aqui?
                selectedGenderId,
                pokemonIVs,
                selectedNatureId,
                selectedAbilitySlot);

        closeFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterCallback = (UpdateCreatePokemon) activity;
            else
                updaterCallback = (UpdateCreatePokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement UpdateCreatePokemon");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataCreatePokemon) activity;
            else
                feederCallback = (FeedDataCreatePokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeedDataCreatePokemon");
        }
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

    interface FeedDataCreatePokemon {
        InterfaceModifierPokemon getLastInterfaceModifierPokemon();
    }

    interface UpdateCreatePokemon {
        void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);
    }
}
