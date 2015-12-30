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


    private final LayoutInflater inflater;


    private boolean showOnlyCompatible = false;
    private boolean showOnlyBasic = false;
    private ArrayList<Integer> compatiblePokemonList;

    private ArrayList<Integer> pokemonIds;
    private ArrayList<String> pokemonNames;

    private ArrayList<Integer> filteredPokemonIds;

    InterfacePokemonSelectorAdapter(Context mContext, ArrayList<Integer> pokemonIds, ArrayList<String> pokemonNames) {

        compatiblePokemonList = new ArrayList<>();

        this.pokemonIds = pokemonIds;
        this.filteredPokemonIds = pokemonIds; //TODO: ver se precisa mesmo
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
            //If this view is new (instead of recycled)
            pokemonDynamicLayout = inflater.inflate(R.layout.dynamic_view_layout_pokemon_selector, parent, false);

            holder = new LayoutHolder();
            holder.icon = (ImageView) pokemonDynamicLayout.findViewById(R.id.imageViewIcon);
            holder.id = (TextView) pokemonDynamicLayout.findViewById(R.id.textViewId);
            pokemonDynamicLayout.setTag(holder);
        } else {
            holder = (LayoutHolder) convertView.getTag();
        }

        int pokemonId = filteredPokemonIds.get(position);

        holder.id.setText(String.format("%03d", pokemonId));
        holder.icon.setBackground(CachedPokemonIcons.getInstance().getIcon(pokemonId)); //TODO HMMMMMM

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
                ArrayList<Integer> FilteredArray = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();

                for (int i = 0; i < pokemonIds.size(); i++) {

                    int currentId = pokemonIds.get(i);

                    if (currentId <= 0) continue; //TODO: ver se precisa tambem

                    /*If only compatible pokemons should be shown, ignores pokemons that don't have
                    * at least one egg group compatible with the current goal pokemon.*/
                    if (showOnlyCompatible) {
                        if (!compatiblePokemonList.contains(currentId))
                            continue;
                    }

//                    if (showOnlyBasic) { //TODO: REDO IT ALL, DISABLED BY NOW
//                        int id = currentPokemonData.id;
//                        int breeds = currentPokemonData.breeds;
//
//                        if (id == Constants.DITTO_ID)
//                            continue;
//
//                        if (id != breeds) {
//                            if (id != 32 && id != 314) //Excludes nidoranM and Illumise because they're special cases
//                                continue;
//                        }
//
//
//                    }


                    String nameToBeCompared = pokemonNames.get(i);

                    //IF CONSTRAINT IS EMPTY, ADDS ALL
                    if (nameToBeCompared.toLowerCase().startsWith(constraint.toString())) {
//                        if (showOnlyCompatible && currentPokemonData.breeds == PokemonData.getInstance().getBasicPokemon(goalPokemon.getPokemonId()))
//                            FilteredArray.add(0, currentPokemonData); //TODO: isso eh oq faz colocar na frente, fazer do jeito novo depois
//                        else
                        FilteredArray.add(currentId);
                    }
                }

                results.count = FilteredArray.size();
                results.values = FilteredArray;

                return results;
            }
        };
    }

    class LayoutHolder {
        ImageView icon;
        TextView id;
    }
}
