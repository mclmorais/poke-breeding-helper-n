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

public class InterfacePokemonSelectorAdapter extends BaseAdapter implements Filterable{

    private final ArrayList<PokemonDataBlock> pokemonList;
    private ArrayList<PokemonDataBlock> filteredPokemonList;
    private final LayoutInflater inflater;
    private PokemonInfo goalPokemon;
    private boolean showOnlyCompatible = false;
    private boolean showOnlyBasic = false;

    void showOnlyCompatible(boolean b) {
        showOnlyCompatible = b;
    }

    void setShowOnlyBasic(boolean b) {
        showOnlyBasic = b;
    }

    public void setGoal(PokemonInfo hatchInfo) {
          goalPokemon = hatchInfo;
//        goalEggGroup1 = hatchInfo.eggGroup1;
//        goalEggGroup2 = hatchInfo.eggGroup2;
//        goalGenderRestriction = PokemonData.getInstance().getGenderRestriction(hatchInfo.id);

    }

    InterfacePokemonSelectorAdapter(Context mContext) {
        this.pokemonList = PokemonData.getInstance().getOrderedData();
        this.filteredPokemonList = this.pokemonList;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

    class LayoutHolder {
        ImageView icon;
        TextView id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View pokemonDynamicLayout = convertView;
        LayoutHolder holder;

        if (convertView == null) {
            //If this view is new (instead of recycled)
            pokemonDynamicLayout = inflater.inflate(R.layout.dynamic_view_layout_pokemon_selector, parent, false);

            holder          = new LayoutHolder();
            holder.icon     = (ImageView)       pokemonDynamicLayout.findViewById(R.id.imageViewIcon);
            holder.id       = (TextView)        pokemonDynamicLayout.findViewById(R.id.textViewId);
            pokemonDynamicLayout.setTag(holder);
        } else {
            holder = (LayoutHolder) convertView.getTag();
        }
            holder.id.setText(String.format("%03d", filteredPokemonList.get(position).id));
            holder.icon.setBackground(filteredPokemonList.get(position).drawable);

        return pokemonDynamicLayout;

    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredPokemonList = (ArrayList<PokemonDataBlock>) results.values;
                if(filteredPokemonList == null) filteredPokemonList = new ArrayList<>();
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();
                ArrayList<PokemonDataBlock> FilteredArrayNames = new ArrayList<>();

                constraint = constraint.toString().toLowerCase();
                for (int i = 0; i < pokemonList.size(); i++) {



                    PokemonDataBlock currentPokemonData = pokemonList.get(i);


                    if(currentPokemonData.id == 0) continue;

                    /*If only compatible pokemons should be shown, ignores pokemons that don't have
                    * at least one egg group compatible with the current goal pokemon.*/
                    if(showOnlyCompatible) {
                        if(goalPokemon.id <= 0)
                            break;

                        boolean group1Compatible = false, group2Compatible = false;
                        EggGroup eggGroup1 = currentPokemonData.eggGroup1;
                        EggGroup eggGroup2 = currentPokemonData.eggGroup2;

                        if(eggGroup1 == EggGroup.DITTO) {
                            group1Compatible = true;
                        }
                        else if (eggGroup1 == goalPokemon.eggGroup1 || eggGroup1 == goalPokemon.eggGroup2) {
                            group1Compatible = true;
                        }

                        if(eggGroup2 != EggGroup.NONE) {
                            if(eggGroup2 == goalPokemon.eggGroup1 || eggGroup2 == goalPokemon.eggGroup2) {
                                group2Compatible = true;
                            }
                        }

                        if(!group1Compatible && !group2Compatible) continue;

                        //-------

                        if(PokemonData.getInstance().getGenderRestriction(goalPokemon.id) == GenderRestriction.GENDERLESS) {
                            if(currentPokemonData.breeds != PokemonData.getInstance().getBasicPokemon(goalPokemon.id)) {
                                if(currentPokemonData.eggGroup1 != EggGroup.DITTO)
                                    continue;
                            }
                        }
                    }

                    if(showOnlyBasic) {
                        int id = currentPokemonData.id;
                        int breeds = currentPokemonData.breeds;

                        if(id == Constants.DITTO_ID)
                            continue;

                        if(id != breeds) {
                            if(id != 32 && id != 314) //Excludes nidoranM and Illumise because they're special cases
                                continue;
                        }


                    }




                    String dataNames = currentPokemonData.name;
                    if (dataNames.toLowerCase().startsWith(constraint.toString()))  {
                        if(showOnlyCompatible && currentPokemonData.breeds == PokemonData.getInstance().getBasicPokemon(goalPokemon.id))
                            FilteredArrayNames.add(0,currentPokemonData);
                        else
                            FilteredArrayNames.add(currentPokemonData);
                    }

                }



                results.count = FilteredArrayNames.size();
                results.values = FilteredArrayNames;

                return results;
            }
        };
    }
}
