package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import java.util.ArrayList;

import marcelo.breguenait.breedinghelper.databinding.FragmentSelectPokemonBinding;


public class SelectPokemonFragment extends PopupDialogFragment {
    private FragmentSelectPokemonBinding binding;
    private OnPokemonSelectedListener updaterCallback;
    private SelectPokemonAdapter interfaceSelectorAdapter;

    private FeedDataSelectPokemon feederCallback;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null) {
                targetFragment = getParentFragment();
            }
            if (targetFragment == null)
                updaterCallback = (OnPokemonSelectedListener) context;
            else
                updaterCallback = (OnPokemonSelectedListener) targetFragment;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnPokemonSelectedListener");
        }

        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null) {
                targetFragment = getParentFragment();
            }
            if (targetFragment == null)
                feederCallback = (FeedDataSelectPokemon) context;
            else
                feederCallback = (FeedDataSelectPokemon) targetFragment;
        } catch (ClassCastException e) {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null) {
                targetFragment = getParentFragment();
            }
            throw new ClassCastException((targetFragment != null ? targetFragment.toString() : context.toString())
                    + " must implement FeedDataSelectPokemon");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = FragmentSelectPokemonBinding.inflate(inflater, container, false);

        interfaceSelectorAdapter = new SelectPokemonAdapter(
                requireActivity().getApplicationContext(),
                feederCallback.getPokemonIds(),
                feederCallback.getPokemonNames());

        interfaceSelectorAdapter.setCompatiblePokemonList(feederCallback.getCompatiblePokemonList());
        interfaceSelectorAdapter.setBasicPokemonList(feederCallback.getBasicPokemonList());
        interfaceSelectorAdapter.setPokemonFamilyList(feederCallback.getPokemonFamilyList());

        binding.gridViewSelectPokemon.setAdapter(interfaceSelectorAdapter);
        binding.gridViewSelectPokemon.setOnItemClickListener((adapterView, view, i, l) -> {
            int p = (int) interfaceSelectorAdapter.getItem(i);
            updaterCallback.onPokemonSelected(p);
            closeFragment();
        });

        binding.editTextSelectPokemon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                interfaceSelectorAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if (getArguments() != null && getArguments().getBoolean("showOnlyCompatible", false)) {
            binding.checkBoxSelectPokemonCompatible.setChecked(true);
            binding.checkBoxSelectPokemonCompatible.setEnabled(false);
            interfaceSelectorAdapter.showOnlyCompatible(true);
        }

        if (updaterCallback.showOnlyBasic()) {
            interfaceSelectorAdapter.setShowOnlyBasic(updaterCallback.showOnlyBasic());
            binding.checkBoxSelectPokemonCompatible.setChecked(true);
            binding.checkBoxSelectPokemonCompatible.setText("Basic Pokémon");
        }

        binding.checkBoxSelectPokemonCompatible.setOnCheckedChangeListener((compoundButton, b) -> {
            interfaceSelectorAdapter.showOnlyCompatible(b);
            interfaceSelectorAdapter.getFilter().filter(binding.editTextSelectPokemon.getText());
        });


        interfaceSelectorAdapter.getFilter().filter(binding.editTextSelectPokemon.getText());


        binding.fragmentSelectPokemonButtonCancel.setOnClickListener(v -> closeFragment());


        setDialogPosition();

        boolean showEggGroupFilter = updaterCallback.showEggGroupFilter();

        binding.checkBoxSelectPokemonCompatible.setEnabled(showEggGroupFilter);

        return binding.getRoot();
    }

    public interface OnPokemonSelectedListener {
        void onPokemonSelected(int id);

        boolean showEggGroupFilter();

        boolean showOnlyBasic();

    }

    public interface FeedDataSelectPokemon {

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonFamilyList();

        ArrayList<Integer> getBasicPokemonList();

    }

    public static class SelectPokemonAdapter extends BaseAdapter implements Filterable {

        private static final int DITTO_ID = 132;

        private final LayoutInflater inflater;

        private boolean addDittoLater = false;

        private boolean showOnlyCompatible = false;
        private boolean showOnlyBasic = false;
        private ArrayList<Integer> compatiblePokemonList;
        private ArrayList<Integer> pokemonFamilyList;
        private ArrayList<Integer> basicPokemonList;

        private ArrayList<Integer> pokemonIds;
        private ArrayList<String> pokemonNames;

        private ArrayList<Integer> filteredPokemonIds;

        SelectPokemonAdapter(Context mContext, ArrayList<Integer> pokemonIds, ArrayList<String> pokemonNames) {

            compatiblePokemonList = new ArrayList<>();

            this.pokemonIds = pokemonIds;
            this.filteredPokemonIds = pokemonIds;
            this.pokemonNames = pokemonNames;

            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void showOnlyCompatible(boolean b) {
            showOnlyCompatible = b;
        }

        void setShowOnlyBasic(boolean b) {
            showOnlyBasic = b;
        }


        public void setCompatiblePokemonList(ArrayList<Integer> compatiblePokemonList) {
            this.compatiblePokemonList = compatiblePokemonList;
        }

        public void setBasicPokemonList(ArrayList<Integer> basicPokemonList) {
            this.basicPokemonList = basicPokemonList;
        }

        public void setPokemonFamilyList(ArrayList<Integer> pokemonFamilyList) {
            this.pokemonFamilyList = pokemonFamilyList;
        }

        @Override
        public int getCount() {
            return filteredPokemonIds.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredPokemonIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View pokemonDynamicLayout = convertView;
            LayoutHolder holder;

            if (convertView == null) {
                pokemonDynamicLayout = inflater.inflate(R.layout.dynamic_view_layout_pokemon_selector, parent, false);

                holder = new LayoutHolder();
                holder.icon = pokemonDynamicLayout.findViewById(R.id.imageViewIcon);
                holder.id = pokemonDynamicLayout.findViewById(R.id.textViewId);
                pokemonDynamicLayout.setTag(holder);
            } else {
                holder = (LayoutHolder) convertView.getTag();
            }

            int pokemonId = filteredPokemonIds.get(position);

            holder.id.setText(String.valueOf(pokemonId));
            
            // Clear background - only show pokemon sprite
            holder.icon.setBackground(null);
            
            // Load pokemon icon from cache and set as image source
            if (CachedPokemonIcons.getInstance() != null) {
                Drawable icon = CachedPokemonIcons.getInstance().getIcon(pokemonId);
                holder.icon.setImageDrawable(icon);
            } else {
                // Fallback if cache not initialized
                String iconId = "pkmn_" + String.format("%03d", pokemonId);
                int resId = inflater.getContext().getResources().getIdentifier(iconId, "drawable", inflater.getContext().getPackageName());
                if (resId != 0) {
                    holder.icon.setImageResource(resId);
                } else {
                    holder.icon.setImageResource(R.drawable.pkmn_missingno);
                }
            }

            return pokemonDynamicLayout;

        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredPokemonIds = (ArrayList<Integer>) results.values;
                    if (filteredPokemonIds == null) filteredPokemonIds = new ArrayList<>();
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<Integer> filteredArray = new ArrayList<>();

                    int familyPosition = 0; //Used so that the family is not added in reverse order

                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < pokemonIds.size(); i++) {

                        int currentId = pokemonIds.get(i);

                        if (currentId <= 0) continue;

                        if (showOnlyCompatible) {
                            if (!compatiblePokemonList.contains(currentId))
                                continue;
                        }

                        if (showOnlyBasic) {
                            if (!basicPokemonList.contains(currentId))
                                continue;
                        }


                        String nameToBeCompared = pokemonNames.get(i);

                        if (nameToBeCompared.toLowerCase().contains(constraint.toString())) {

                            if (currentId == DITTO_ID) {
                                addDittoLater = true;
                                continue;
                            }

                            if (showOnlyCompatible && pokemonFamilyList.contains(currentId)) {
                                filteredArray.add(familyPosition, currentId);
                                familyPosition++;
                            } else
                                filteredArray.add(currentId);
                        }
                    }

                    if (addDittoLater)
                        filteredArray.add(0, DITTO_ID);


                    results.count = filteredArray.size();
                    results.values = filteredArray;

                    return results;
                }
            };
        }

        class LayoutHolder {
            ImageView icon;
            TextView id;
        }
    }
}
