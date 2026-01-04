package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import breedingmanager.NatureManager;
import databasemanager.DatabaseConstants;
import marcelo.breguenait.breedinghelper.R;

public class StoredPokemonFragment extends Fragment implements
        EditorPokemonFragment.FeedDataCreatePokemon,
        CreatePokemonFragment.UpdateCreatePokemon,
        StoredPokemonViewerFragment.FeedDataPokemonViewer,
        StoredPokemonViewerFragment.UpdatePokemonViewer,
        CreatePokemonFragment.FeedDataCreatePokemon {

    private StoredPokemonAdapter storedPokemonAdapter;
    private GridView gridViewPokemons;
    private ToggleButton buttonRemove;
    private int lastAddedPokemonId = 0;
    private TextView textHintStore;
    private TextView textHintRemove;

    private FeedDataStoredPokemon feederCallback;
    private UpdateStoredPokemonList updaterCallback;

    public void setCallbacks(Fragment callbacks) {
        this.feederCallback = (FeedDataStoredPokemon) callbacks;
        this.updaterCallback = (UpdateStoredPokemonList) callbacks;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_stored_pokemon, container, false);

        gridViewPokemons = view.findViewById(R.id.gridViewPokemonsList);
        Button buttonAdd = view.findViewById(R.id.buttonFragmentPokemonListAdd);
        buttonRemove = view.findViewById(R.id.buttonFragmentPokemonListRemove);
        textHintStore = view.findViewById(R.id.textViewHintStore);
        textHintRemove = view.findViewById(R.id.textViewHintDelete);

        textHintRemove.setVisibility(View.GONE);

        buttonAdd.setOnClickListener(v -> {
            openAddPokemonFragment(v);
            buttonRemove.setChecked(false);
        });

        buttonRemove.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b && storedPokemonAdapter != null && !storedPokemonAdapter.isEmpty()) {
                textHintRemove.setVisibility(View.VISIBLE);
                buttonRemove.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                storedPokemonAdapter.setDeleteMode(true);

            } else {
                textHintRemove.setVisibility(View.GONE);
                buttonRemove.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimary));
                if (storedPokemonAdapter != null) {
                    storedPokemonAdapter.setDeleteMode(false);
                }
            }
            updateGridView();
        });

        gridViewPokemons.setOnItemClickListener((adapterView, view1, i, l) -> {
            if (buttonRemove.isChecked()) {
                InterfaceStoredPokemon p = (InterfaceStoredPokemon) gridViewPokemons.getAdapter().getItem(i);
                updaterCallback.removeStoredPokemon(p.getUUID());
                setHatchAdapter(feederCallback.getInterfaceStoredPokemonList(), getContext());
                updateGridView();


            } else {
                InterfaceStoredPokemon p = (InterfaceStoredPokemon) gridViewPokemons.getAdapter().getItem(i);
                openStoredPokemonViewerFragment(view1, p.getUUID(), i);
            }
        });


        return view;
    }

    void setHatchAdapter(ArrayList<InterfaceStoredPokemon> list, Context context) {
        storedPokemonAdapter = new StoredPokemonAdapter(list, context);
        storedPokemonAdapter.setDeleteMode(buttonRemove.isChecked());
        gridViewPokemons.setAdapter(storedPokemonAdapter);
        updateGridView();

    }

    void openAddPokemonFragment(View callerView) {
        FragmentManager fm = getParentFragmentManager();
        CreatePokemonFragment fragment = new CreatePokemonFragment();
        Bundle b = addPositionAsArguments(callerView);
        b.putInt("defaultPokemon", lastAddedPokemonId);
        fragment.setArguments(b);
        // Set this fragment as the target so CreatePokemonFragment can find it
        fragment.setTargetFragment(this, 0);
        fragment.show(fm, "CreatePokemonFragment");
    }

    void openStoredPokemonViewerFragment(View callerView, UUID pokemonUUID, int pokemonPos) {
        FragmentManager fragmentManager = getParentFragmentManager();
        int[] callerViewPosition = new int[2];
        callerView.getLocationOnScreen(callerViewPosition);
        StoredPokemonViewerFragment fragment = StoredPokemonViewerFragment.newInstance(callerViewPosition, pokemonUUID, pokemonPos);
        fragment.show(fragmentManager, "storedPokemonPopup");
    }

    void updateGridView() {
        if (storedPokemonAdapter != null && !storedPokemonAdapter.isEmpty()) {
            if(buttonRemove != null) buttonRemove.setEnabled(true);
            if(textHintStore != null) textHintStore.setVisibility(View.GONE);
        } else if (buttonRemove != null && textHintStore != null) {
            buttonRemove.setChecked(false);
            buttonRemove.setEnabled(false);
            textHintStore.setVisibility(View.VISIBLE);
        }
        if (storedPokemonAdapter != null) {
            storedPokemonAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (feederCallback != null) {
            setHatchAdapter(feederCallback.getInterfaceStoredPokemonList(), getContext());
        }
        updateGridView();
    }

    Bundle addPositionAsArguments(View v) {
        int[] callerViewPosition = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }


    @Override
    public LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId) {
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
    public StoredPokemonViewerFragment.InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid) {
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

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return feederCallback.getPokemonFamilyList();
    }

    int getInterfacePokemonPosition(UUID uuid) {
        return ((StoredPokemonAdapter) gridViewPokemons.getAdapter()).getPositionByUUID(uuid);
    }

    @Override
    public ArrayList<NatureManager.NatureVerbose> getInterfaceNatures() {
        return feederCallback.getInterfaceNatures();
    }

    @Override
    public InterfaceModifierPokemon getLastInterfaceModifierPokemon() {
        UUID lastPokemonUUID = ((StoredPokemonAdapter) gridViewPokemons.getAdapter()).getLastInterfaceStoredpokemonUUID();
        if (lastPokemonUUID != null)
            return feederCallback.getInterfaceModifierPokemon(lastPokemonUUID);
        else
            return new InterfaceModifierPokemon();
    }

    public interface FeedDataStoredPokemon {

        LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId);

        ArrayList<InterfaceStoredPokemon> getInterfaceStoredPokemonList();

        int getGenderRate(int pokemonId);

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonFamilyList();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        StoredPokemonViewerFragment.InterfaceViewerPokemon getInterfaceViewerPokemon(UUID uuid);

        String getPokemonName(int pokemonId);

        InterfaceModifierPokemon getInterfaceModifierPokemon(UUID uuid);

        ArrayList<NatureManager.NatureVerbose> getInterfaceNatures();
    }

    public interface UpdateStoredPokemonList {
        void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);

        void updateStoredPokemon(UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot);

        void removeStoredPokemon(UUID uuid);
    }

    public static class InterfaceStoredPokemon {
        final private UUID storedId;
        final private int pokemonId;
        final private int genderId;
        final private int[] IVs;

        public InterfaceStoredPokemon(UUID storedId, int pokemonId, int genderId, int[] IVs) {
            this.storedId = storedId;
            this.pokemonId = pokemonId;
            this.genderId = genderId;
            this.IVs = IVs;
        }

        public UUID getUUID() {
            return storedId;
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
    }

    static class StoredPokemonAdapter extends BaseAdapter {

        private final PreloadedDrawables preloadedDrawables;
        private final LayoutInflater inflater;
        private final ArrayList<InterfaceStoredPokemon> storedPokemonList;
        private boolean deleteMode = false;

        public StoredPokemonAdapter(ArrayList<InterfaceStoredPokemon> storedPokemonList, Context context) {
            preloadedDrawables = new PreloadedDrawables(context);
            this.storedPokemonList = storedPokemonList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        public void setDeleteMode(boolean deleteMode) {
            this.deleteMode = deleteMode;
        }

        @Override
        public int getCount() {
            return storedPokemonList.size();
        }

        @Override
        public Object getItem(int position) {
            return storedPokemonList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {


            View hatch = convertView;
            LayoutHolder holder;

            if (convertView == null) {
                hatch = inflater.inflate(R.layout.dynamic_view_layout_stored_pokemon, parent, false);

                holder = new LayoutHolder();
                holder.frame = hatch.findViewById(R.id.frameDynamicHatch);
                holder.icon = hatch.findViewById(R.id.imageDynamicHatchIcon);
                holder.gender = hatch.findViewById(R.id.imageDynamicHatchGender);
                holder.number = hatch.findViewById(R.id.textDynamicHatchNumber);
                holder.IVs[0] = hatch.findViewById(R.id.imageDynamicHatchHP);
                holder.IVs[1] = hatch.findViewById(R.id.imageDynamicHatchATK);
                holder.IVs[2] = hatch.findViewById(R.id.imageDynamicHatchDEF);
                holder.IVs[3] = hatch.findViewById(R.id.imageDynamicHatchSATK);
                holder.IVs[4] = hatch.findViewById(R.id.imageDynamicHatchSDEF);
                holder.IVs[5] = hatch.findViewById(R.id.imageDynamicHatchSPD);

                hatch.setTag(holder);
            } else {
                holder = (LayoutHolder) convertView.getTag();
            }

            InterfaceStoredPokemon interfaceStoredPokemon = storedPokemonList.get(i);

            if (deleteMode)
                holder.frame.setBackgroundResource(R.drawable.layer_card_background_round_red);
            else
                holder.frame.setBackgroundResource(R.drawable.layer_background_round_selector);

            holder.icon.setBackground(ContextCompat.getDrawable(hatch.getContext(), R.drawable.pkmn_missingno));

            holder.gender.setBackground(preloadedDrawables.getGenderDrawable(interfaceStoredPokemon.getGenderId()));
            for (int j = 0; j < 6; j++)
                holder.IVs[j].setBackground(preloadedDrawables.getIVDrawable(j, (interfaceStoredPokemon.getIVs()[j] == 1)));
            holder.number.setText(String.valueOf(i + 1));

            return hatch;
        }


        int getPositionByUUID(UUID uuid) {
            for (int i = 0; i < storedPokemonList.size(); i++) {
                if (storedPokemonList.get(i).getUUID().equals(uuid))
                    return i;
            }

            Log.d("StoredAdapter", "Couldn't find the UUID sent!");
            return -1;
        }

        UUID getLastInterfaceStoredpokemonUUID() {
            if (!storedPokemonList.isEmpty())
                return storedPokemonList.get(storedPokemonList.size() - 1).getUUID();
            else
                return null;
        }

        private static class PreloadedDrawables {
            final Drawable[] IVActive = new Drawable[6];
            final Drawable[] IVInactive = new Drawable[6];
            final Drawable maleIcon;
            final Drawable femaleIcon;
            final Drawable genderlessIcon;
            final Drawable missingno;

            private PreloadedDrawables(Context c) {
                maleIcon = ContextCompat.getDrawable(c, R.drawable.symbol_male);
                femaleIcon = ContextCompat.getDrawable(c, R.drawable.symbol_female);
                genderlessIcon = ContextCompat.getDrawable(c, R.drawable.symbol_genderless);

                missingno = ContextCompat.getDrawable(c, R.drawable.pkmn_missingno);

                IVActive[0] = ContextCompat.getDrawable(c, R.drawable.iv_circle_checked);
                IVActive[1] = ContextCompat.getDrawable(c, R.drawable.iv_triangle_checked);
                IVActive[2] = ContextCompat.getDrawable(c, R.drawable.iv_square_checked);
                IVActive[3] = ContextCompat.getDrawable(c, R.drawable.iv_heart_checked);
                IVActive[4] = ContextCompat.getDrawable(c, R.drawable.iv_star_checked);
                IVActive[5] = ContextCompat.getDrawable(c, R.drawable.iv_diamond_checked);

                IVInactive[0] = ContextCompat.getDrawable(c, R.drawable.iv_circle_clear);
                IVInactive[1] = ContextCompat.getDrawable(c, R.drawable.iv_triangle_clear);
                IVInactive[2] = ContextCompat.getDrawable(c, R.drawable.iv_square_clear);
                IVInactive[3] = ContextCompat.getDrawable(c, R.drawable.iv_heart_clear);
                IVInactive[4] = ContextCompat.getDrawable(c, R.drawable.iv_star_clear);
                IVInactive[5] = ContextCompat.getDrawable(c, R.drawable.iv_diamond_clear);
            }

            Drawable getGenderDrawable(int genderId) {
                if (genderId == DatabaseConstants.MALE_ID) return maleIcon;
                else if (genderId == DatabaseConstants.FEMALE_ID) return femaleIcon;
                else return genderlessIcon;
            }

            Drawable getIVDrawable(int position, boolean active) {
                if (active)
                    return IVActive[position];
                else
                    return IVInactive[position];
            }


        }

        static class LayoutHolder {
            ImageView icon;
            ImageView gender;
            TextView number;
            View frame;

            ImageView[] IVs = new ImageView[6];
        }


    }
}
