package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class InterfacePokemonSelectorAdapter extends BaseAdapter implements Filterable {

    private final ArrayList<PokemonDataBlock> pokemonList;
    private final LayoutInflater inflater;
    private ArrayList<PokemonDataBlock> filteredPokemonList;
    private StoredPokemon goalPokemon;
    private boolean showOnlyCompatible = false;
    private boolean showOnlyBasic = false;
    private ArrayList<Integer> compatiblePokemonList;

    InterfacePokemonSelectorAdapter(Context mContext) {
        this.pokemonList = PokemonData.getInstance().getOrderedData();
        this.filteredPokemonList = this.pokemonList;
        compatiblePokemonList = new ArrayList<>();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    void showOnlyCompatible(boolean b) {
        showOnlyCompatible = b;
    }

    void setShowOnlyBasic(boolean b) {
        showOnlyBasic = b;
    }

    @Deprecated
    public void setGoal(StoredPokemon hatchInfo) {
        goalPokemon = hatchInfo;
//        goalEggGroup1 = hatchInfo.eggGroup1;
//        goalEggGroup2 = hatchInfo.eggGroup2;
//        goalGenderRestriction = PokemonData.getInstance().getGenderRestriction(hatchInfo.id);

    }

    public void setCompatiblePokemonList(ArrayList<Integer> compatiblePokemonList) {
        this.compatiblePokemonList = compatiblePokemonList;
    }

    @Override
    public int getCount() {
        return filteredPokemonList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredPokemonList.get(position);
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
            //If this view is new (instead of recycled)
            pokemonDynamicLayout = inflater.inflate(R.layout.dynamic_view_layout_pokemon_selector, parent, false);

            holder = new LayoutHolder();
            holder.icon = (ImageView) pokemonDynamicLayout.findViewById(R.id.imageViewIcon);
            holder.id = (TextView) pokemonDynamicLayout.findViewById(R.id.textViewId);
            pokemonDynamicLayout.setTag(holder);
        } else {
            holder = (LayoutHolder) convertView.getTag();
        }
        holder.id.setText(String.format("%03d", filteredPokemonList.get(position).id));
        holder.icon.setBackground(filteredPokemonList.get(position).drawable);
        //holder.icon.setBackground(PokemonData.getInstance().getDrawableFromId(filteredPokemonList.get(position).id)); //TODO: do something like this instead

        return pokemonDynamicLayout;

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
//FILTERED POKEMON NAMES DOESNT HAVE NAMES!!!!
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPokemonList = (ArrayList<PokemonDataBlock>) results.values;
                if (filteredPokemonList == null) filteredPokemonList = new ArrayList<>();
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<PokemonDataBlock> FilteredArrayNames = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();

                for (int i = 0; i < pokemonList.size(); i++) {

                    PokemonDataBlock currentPokemonData = pokemonList.get(i);
//TODO: instead of getting goal, just gets list of compatible poceymans
                    //TODO: get the goal -> get ids compatible with goal -> filter ids that arent
                    if (currentPokemonData.id == 0) continue;

                    /*If only compatible pokemons should be shown, ignores pokemons that don't have
                    * at least one egg group compatible with the current goal pokemon.*/
                    if (showOnlyCompatible) {


                        if(!compatiblePokemonList.contains(currentPokemonData.id))
                            continue;

//                        boolean group1Compatible = false, group2Compatible = false;
//                        EggGroup eggGroup1 = currentPokemonData.eggGroup1;
//                        EggGroup eggGroup2 = currentPokemonData.eggGroup2;
//
//                        if (eggGroup1 == EggGroup.DITTO) {
//                            group1Compatible = true;
//                        } else if (eggGroup1 == goalPokemon.eggGroup1 || eggGroup1 == goalPokemon.eggGroup2) {
//                            group1Compatible = true;
//                        }
//
//                        if (eggGroup2 != EggGroup.NONE) {
//                            if (eggGroup2 == goalPokemon.eggGroup1 || eggGroup2 == goalPokemon.eggGroup2) {
//                                group2Compatible = true;
//                            }
//                        }
//
//                        if (!group1Compatible && !group2Compatible) continue;
//
//                        //-------
//
//                        if (PokemonData.getInstance().getGenderRestriction(goalPokemon.id) == GenderRestriction.GENDERLESS) {
//                            if (currentPokemonData.breeds != PokemonData.getInstance().getBasicPokemon(goalPokemon.id)) {
//                                if (currentPokemonData.eggGroup1 != EggGroup.DITTO)
//                                    continue;
//                            }
//                        }
                    }

                    if (showOnlyBasic) {
                        int id = currentPokemonData.id;
                        int breeds = currentPokemonData.breeds;

                        if (id == Constants.DITTO_ID)
                            continue;

                        if (id != breeds) {
                            if (id != 32 && id != 314) //Excludes nidoranM and Illumise because they're special cases
                                continue;
                        }


                    }


                    String dataNames = currentPokemonData.name;
                    if (dataNames.toLowerCase().startsWith(constraint.toString())) {
//                        if (showOnlyCompatible && currentPokemonData.breeds == PokemonData.getInstance().getBasicPokemon(goalPokemon.getPokemonId()))
//                            FilteredArrayNames.add(0, currentPokemonData); //TODO: isso eh oq faz colocar na frente, fazer do jeito novo depois
//                        else
                            FilteredArrayNames.add(currentPokemonData);
                    }

                }


                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };
    }

    class LayoutHolder {
        ImageView icon;
        TextView id;
    }
}
