package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;


//TODO: fazer list do stored adapter ser mais proativo ao invés de ressetar toda vez

/**
 * Created by Marcelo on 21/12/2014.
 */
public class StoredPokemonFragment extends Fragment implements
        EditorPokemonFragment.FeedDataCreatePokemon,
        CreatePokemonFragment.UpdateCreatePokemon,
        StoredPokemonViewerFragment.FeedDataPokemonViewer,
        StoredPokemonViewerFragment.UpdatePokemonViewer {

    private StoredPokemonAdapter storedPokemonAdapter;
    private GridView gridViewPokemons;
    private Button buttonAdd;
    private ToggleButton buttonRemove;
    private int lastAddedPokemonId = 0;
    private TextView textHintStore;
    private TextView textHintRemove;

    private FeedDataStoredPokemon feederCallback;
    private UpdateStoredPokemonList updaterCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataStoredPokemon) activity;
            else
                feederCallback = (FeedDataStoredPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FeedDataStoredPokemon!");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterCallback = (UpdateStoredPokemonList) activity;
            else
                updaterCallback = (UpdateStoredPokemonList) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UpdateStoredPokemonList!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_stored_pokemon, container, false);

        gridViewPokemons = (GridView) view.findViewById(R.id.gridViewPokemonsList);
        buttonAdd = (Button) view.findViewById(R.id.buttonFragmentPokemonListAdd);
        buttonRemove = (ToggleButton) view.findViewById(R.id.buttonFragmentPokemonListRemove);
        textHintStore = (TextView) view.findViewById(R.id.textViewHintStore);
        textHintRemove = (TextView) view.findViewById(R.id.textViewHintDelete);

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
                if (b && storedPokemonAdapter.getCount() > 0) {
                    textHintRemove.setVisibility(View.VISIBLE);
                    buttonRemove.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    storedPokemonAdapter.setDeleteMode(true);

                } else {
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
                if (buttonRemove.isChecked()) {
                    InterfaceStoredPokemon p = (InterfaceStoredPokemon) gridViewPokemons.getAdapter().getItem(i);
                    updaterCallback.removeStoredPokemon(p.getUUID());
                    setHatchAdapter(feederCallback.getInterfaceStoredPokemonList(), getContext());
                    updateGridView();


                } else {
                    InterfaceStoredPokemon p = (InterfaceStoredPokemon) gridViewPokemons.getAdapter().getItem(i);
                    openStoredPokemonViewerFragment(view, p.getUUID(), i);
                }
            }
        });


        return view;
    }

    void setHatchAdapter(ArrayList<InterfaceStoredPokemon> list, Context context) {
        storedPokemonAdapter = new StoredPokemonAdapter(list, context);
        gridViewPokemons.setAdapter(storedPokemonAdapter);
        updateGridView();

    }

    void openAddPokemonFragment(View callerView) {
        FragmentManager fm = getFragmentManager();
        CreatePokemonFragment fragment = new CreatePokemonFragment();
        Bundle b = addPositionAsArguments(callerView);
        b.putInt("defaultPokemon", lastAddedPokemonId);
        fragment.setArguments(b);
        fragment.setTargetFragment(this, 0);
        fragment.show(fm, "");
    }

    void openStoredPokemonViewerFragment(View callerView, UUID pokemonUUID, int pokemonPos) {
        FragmentManager fragmentManager = getFragmentManager();
        int callerViewPosition[] = new int[2];
        callerView.getLocationOnScreen(callerViewPosition);
        StoredPokemonViewerFragment fragment = StoredPokemonViewerFragment.newInstance(callerViewPosition, pokemonUUID, pokemonPos); //TODO: usar newinstance pra passar o pokemon pro modifypokemonfragment
        fragment.setTargetFragment(this, 0);
        fragment.show(fragmentManager, "storedPokemonPopup");
    }

    void updateGridView() {
        if (storedPokemonAdapter.getCount() > 0) {
            buttonRemove.setEnabled(true);
            textHintStore.setVisibility(View.GONE);
        } else {
            buttonRemove.setChecked(false);
            buttonRemove.setEnabled(false);
            textHintStore.setVisibility(View.VISIBLE);
        }
        storedPokemonAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        setHatchAdapter(feederCallback.getInterfaceStoredPokemonList(), getContext());
        updateGridView();
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }


    @Override
    public ArrayList<String> getListOfNatures() {
        return feederCallback.getListOfNatures();
    }

    @Override
    public HashMap<Integer, String> getListOfAbilities(int pokemonId) {
        return feederCallback.getListOfAbilities(pokemonId);
    }

    @Override
    public int getGenderRate(int pokemonId) {
        return feederCallback.getGenderRate(pokemonId);
    }

    @Override
    public void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        lastAddedPokemonId = pokemonId;
        updaterCallback.storePokemon(pokemonId, genderId, IVs, natureId, abilitySlot);
        setHatchAdapter(feederCallback.getInterfaceStoredPokemonList(), getContext());

    }


    @Override
    public ArrayList<Integer> getCompatiblePokemonList() {
        return feederCallback.getCompatiblePokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return feederCallback.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return feederCallback.getPokemonNames();
    }

    @Override
    public InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {
        return feederCallback.getInterfaceViewerPokemon(uuid);
    }

    @Override
    public String getPokemonName(int pokemonId) {
        return feederCallback.getPokemonName(pokemonId);
    }

    @Override
    public InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid) {
        return feederCallback.getInterfaceModifierPokemon(uuid);
    }

    @Override
    public void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        updaterCallback.updateStoredPokemon(uuid, pokemonId, genderId, IVs, natureId, abilitySlot);
        setHatchAdapter(feederCallback.getInterfaceStoredPokemonList(), getContext());
    }

    interface FeedDataStoredPokemon {
        ArrayList<String> getListOfNatures();

        HashMap<Integer, String> getListOfAbilities(int pokemonId);

        ArrayList<InterfaceStoredPokemon> getInterfaceStoredPokemonList(); //TODO: ver se não é uma boa passar uma cópia dessa lista pra evitar merda aqui (ver no final isso)

        int getGenderRate(int pokemonId);

        StoredPokemon getGoalPokemon();

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid);

        String getPokemonName(int pokemonId);

        InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid);
    }

    interface UpdateStoredPokemonList {
        void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);

        void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);

        void removeStoredPokemon(UUID uuid);
    }


}
