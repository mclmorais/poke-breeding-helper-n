package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
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

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Marcelo on 21/12/2014.
 */
public class StoredPokemonsFragment extends Fragment implements AddPokemonPopupFragment.BuildPokemon{

    OnPokemonListChanged mCallback;

    HatchAdapter    hatchAdapter;
    GridView        gridViewPokemons;
    Button          buttonAdd;
    ToggleButton    buttonRemove;
    int             lastAddedPokemonId = 0;
    TextView        textHintStore;
    TextView        texthintRemove;

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
        View view = inflater.inflate(R.layout.fragment_pokemon_list, container, false);

        gridViewPokemons = (GridView) view.findViewById(R.id.gridViewPokemonsList);
        buttonAdd = (Button) view.findViewById(R.id.buttonFragmentPokemonListAdd);
        buttonRemove = (ToggleButton) view.findViewById(R.id.buttonFragmentPokemonListRemove);
        textHintStore = (TextView) view.findViewById(R.id.textViewHintStore);
        texthintRemove = (TextView) view.findViewById(R.id.textViewHintDelete);
        texthintRemove.setVisibility(View.GONE);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddPokemonFragment(view);
            }
        });

        buttonRemove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    texthintRemove.setVisibility(View.VISIBLE);
                    buttonRemove.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    hatchAdapter.setDeleteMode(true);

                }
                else {
                    texthintRemove.setVisibility(View.GONE);
                    buttonRemove.setTextColor(getResources().getColor(R.color.accent));
                    hatchAdapter.setDeleteMode(false);
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
            }
        });
        return view;
    }

    void setHatchAdapter(HashMap<UUID, PokemonInfo> list, Context context) {
        hatchAdapter = new HatchAdapter(list, context);
        gridViewPokemons.setAdapter(hatchAdapter);
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

    void updateGridView(){
        if(hatchAdapter.getCount() > 0) {
            textHintStore.setVisibility(View.GONE);
        }
        else {
            textHintStore.setVisibility(View.VISIBLE);
        }
        hatchAdapter.notifyDataSetChanged();
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

    interface OnPokemonListChanged {
        void addPokemonToList(PokemonInfo pokemon);
        void removePokemon(int position);
    }
}
