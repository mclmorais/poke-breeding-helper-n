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

    InterfacePokemonSelectorAdapter(Context mContext, ArrayList<Integer> pokemonIds, ArrayList<String> pokemonNames) {

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
        holder.icon.setBackground(CachedPokemonIcons.getInstance().getIcon(pokemonId));

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

                    if (currentId <= 0) continue; //TODO: ver se precisa tambem

                    /*If only compatible pokemons should be shown, ignores pokemons that don't have
                    * at least one egg group compatible with the current goal pokemon.*/
                    if (showOnlyCompatible) {
                        if (!compatiblePokemonList.contains(currentId))
                            continue;
                    }

                    if (showOnlyBasic) {
                        if (!basicPokemonList.contains(currentId))
                            continue;
                    }


                    String nameToBeCompared = pokemonNames.get(i);


                    //IF CONSTRAINT IS EMPTY, ADDS ALL
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

                if (showOnlyCompatible && addDittoLater)
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
