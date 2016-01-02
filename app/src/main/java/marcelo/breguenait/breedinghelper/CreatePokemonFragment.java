package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by Marcelo on 30/12/2015.
 */

//TODO: SALVAR ULTIMA POSIÇAO DE TUDO PRA EVITAR FADIGA
public class CreatePokemonFragment extends EditorPokemonFragment {


    UpdateCreatePokemon updaterCallback;


    void finishFragment() {

        boolean hasIVs = false;
        for (CheckBox IVs : checkBoxInputIVs) {
            if (IVs.isChecked()) hasIVs = true;
        }
        if (!hasIVs) {
            showToast(getActivity().getString(R.string.message_select_one_iv));
            return;
        }
        if (selectedPokemonId <= 0) {
            showToast(getActivity().getString(R.string.message_select_pokemon));
            return;
        }

        int[] pokemonIVs = new int[6];
        for (int i = 0; i < 6; i++)
            pokemonIVs[i] = checkBoxInputIVs[i].isChecked() ? 1 : 0;

        int spinnerPosition = spinnerAbility.getSelectedItemPosition();
        int abilitySlot = abilitySlots.get(spinnerPosition);

        int natureId = spinnerNature.getSelectedItemPosition() + 1;


        updaterCallback.storePokemon(
                selectedPokemonId, //TODO: fazer tudo atualizar uma variavel na hora ao inves de calcular aqui?
                selectedGenderId,
                pokemonIVs,
                natureId,
                abilitySlot);

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

    interface UpdateCreatePokemon {
        void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);
    }
}
