package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.List;

/**
 * Created by Marcelo on 21/12/2014.
 */
public class StoredPokemonFragment extends Fragment implements AddPokemonPopupFragment.OnBuildPokemon,
        StoredPokemonPopupFragment.OnPokemonPopupListener {

    private OnPokemonListChanged mCallback;

    interface OnPokemonListChanged {
        void addPokemonToList(PokemonInfo pokemon);
        void editPokemon(PokemonInfo pokemon, int position);
        void removePokemon(int position);
        PokemonInfo getGoalData();
        PokemonInfo getSelectedPokemonData(int position);
    }

    private StoredPokemonAdapter storedPokemonAdapter;
    private GridView        gridViewPokemons;
    private Button          buttonAdd;
    private ToggleButton    buttonRemove;
    private int             lastAddedPokemonId = 0;
    private TextView        textHintStore;
    private TextView        textHintRemove;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (OnPokemonListChanged) activity;
            else
                mCallback = (OnPokemonListChanged) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement TempInterface");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_stored_pokemon, container, false);

        gridViewPokemons = (GridView) view.findViewById(R.id.gridViewPokemonsList);
        buttonAdd        = (Button) view.findViewById(R.id.buttonFragmentPokemonListAdd);
        buttonRemove     = (ToggleButton) view.findViewById(R.id.buttonFragmentPokemonListRemove);
        textHintStore    = (TextView) view.findViewById(R.id.textViewHintStore);
        textHintRemove   = (TextView) view.findViewById(R.id.textViewHintDelete);

        textHintRemove.setVisibility(View.GONE);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPokemonFragment(view);
                buttonRemove.setChecked(false);
            }
        });

        buttonRemove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && storedPokemonAdapter.getCount() > 0) {
                    textHintRemove.setVisibility(View.VISIBLE);
                    buttonRemove.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    storedPokemonAdapter.setDeleteMode(true);

                }
                else {
                    textHintRemove.setVisibility(View.GONE);
                    buttonRemove.setTextColor(getResources().getColor(R.color.colorPrimary));
                    storedPokemonAdapter.setDeleteMode(false);
                }
                updateGridView();
            }
        });

        gridViewPokemons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(buttonRemove.isChecked()) {
                    mCallback.removePokemon(i);
                    updateGridView();
                }
                else {
                    openStoredPokemonPopupFragment(view, mCallback.getSelectedPokemonData(i), i);
                }
            }
        });


        return view;
    }

    void setHatchAdapter(List<PokemonInfo> list, Context context) {
        storedPokemonAdapter = new StoredPokemonAdapter(list, context);
        gridViewPokemons.setAdapter(storedPokemonAdapter);
        updateGridView();

    }

    void openAddPokemonFragment(View callerView){
        FragmentManager fm = getFragmentManager();
        AddPokemonPopupFragment fragment = new AddPokemonPopupFragment();
        Bundle b = addPositionAsArguments(callerView);
        b.putInt("defaultPokemon",lastAddedPokemonId);
        fragment.setArguments(b);
        fragment.setTargetFragment(this,0);
        fragment.show(fm, "");
    }

    void openStoredPokemonPopupFragment(View callerView, PokemonInfo selectedPokemon, int pokemonPos) {
        FragmentManager fragmentManager = getFragmentManager();
        int callerViewPosition[] = new int[2];
        callerView.getLocationOnScreen(callerViewPosition);
        StoredPokemonPopupFragment fragment = StoredPokemonPopupFragment.newInstance(callerViewPosition, selectedPokemon, pokemonPos);
        fragment.setTargetFragment(this,0);
        fragment.show(fragmentManager,"storedPokemonPopup");
    }

    void updateGridView(){
        if(storedPokemonAdapter.getCount() > 0) {
            buttonRemove.setEnabled(true);
            textHintStore.setVisibility(View.GONE);
        }
        else {
            buttonRemove.setChecked(false);
            buttonRemove.setEnabled(false);
            textHintStore.setVisibility(View.VISIBLE);
        }
        storedPokemonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBuildPokemon(PokemonInfo pokemon) {
        lastAddedPokemonId = pokemon.id;
        mCallback.addPokemonToList(pokemon);
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }

    @Override
    public PokemonInfo getGoal() {
        return mCallback.getGoalData();
    }

    @Override
    public void onPokemonAltered(PokemonInfo alteredPokemon, int position) {
        mCallback.editPokemon(alteredPokemon,position);
        storedPokemonAdapter.notifyDataSetChanged();

    }


}
