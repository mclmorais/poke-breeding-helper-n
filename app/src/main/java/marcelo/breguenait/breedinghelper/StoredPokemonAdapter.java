package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;


class StoredPokemonAdapter extends BaseAdapter{

    public static int FEMALE = 1;
    public static int MALE = 2;
    public static int GENDERLESS = 3;

    private class PreloadedDrawables {
        Drawable maleIcon;
        Drawable femaleIcon;
        Drawable genderlessIcon;
        Drawable missingno;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];

        private PreloadedDrawables(Context c) {

            maleIcon = c.getResources().getDrawable(R.drawable.symbol_male);
            femaleIcon = c.getResources().getDrawable(R.drawable.symbol_female);
            genderlessIcon = c.getResources().getDrawable(R.drawable.symbol_genderless);

            missingno = c.getResources().getDrawable(R.drawable.pkmn_missingno);

            IVActive[0] = c.getResources().getDrawable(R.drawable.iv_circle_checked);
            IVActive[1] = c.getResources().getDrawable(R.drawable.iv_triangle_checked);
            IVActive[2] = c.getResources().getDrawable(R.drawable.iv_square_checked);
            IVActive[3] = c.getResources().getDrawable(R.drawable.iv_heart_checked);
            IVActive[4] = c.getResources().getDrawable(R.drawable.iv_star_checked);
            IVActive[5] = c.getResources().getDrawable(R.drawable.iv_diamond_checked);

            IVInactive[0] = c.getResources().getDrawable(R.drawable.iv_circle_clear);
            IVInactive[1] = c.getResources().getDrawable(R.drawable.iv_triangle_clear);
            IVInactive[2] = c.getResources().getDrawable(R.drawable.iv_square_clear);
            IVInactive[3] = c.getResources().getDrawable(R.drawable.iv_heart_clear);
            IVInactive[4] = c.getResources().getDrawable(R.drawable.iv_star_clear);
            IVInactive[5] = c.getResources().getDrawable(R.drawable.iv_diamond_clear);
        }

        Drawable getGenderDrawable(int genderId) {
            if (genderId == MALE)          return maleIcon;
            else if (genderId == FEMALE)   return femaleIcon;
            else  return genderlessIcon;
        }

        Drawable getIVDrawable(int position, boolean active) {
            if(active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }

    private final PreloadedDrawables preloadedDrawables;

    private ArrayList<StoredPokemon> storedPokemonList;
    private final LayoutInflater inflater;

    private boolean deleteMode = false;

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    public StoredPokemonAdapter(ArrayList<StoredPokemon> storedPokemonList, Context context) {
        preloadedDrawables = new PreloadedDrawables(context);
        this.storedPokemonList = storedPokemonList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

    class LayoutHolder {
        ImageView icon;
        ImageView gender;
        TextView  number;
        View      frame;

        ImageView[] IVs = new ImageView[6];
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {


        PokemonData data = PokemonData.getInstance();

        View hatch = convertView;
        LayoutHolder holder;

        if(convertView == null) {
            hatch = inflater.inflate(R.layout.dynamic_view_layout_stored_pokemon,parent,false);

            holder = new LayoutHolder();
            holder.frame = hatch.findViewById(R.id.frameDynamicHatch);
            holder.icon = (ImageView) hatch.findViewById(R.id.imageDynamicHatchIcon);
            holder.gender = (ImageView) hatch.findViewById(R.id.imageDynamicHatchGender);
            holder.number = (TextView)  hatch.findViewById(R.id.textDynamicHatchNumber);
            holder.IVs[0] = (ImageView) hatch.findViewById(R.id.imageDynamicHatchHP);
            holder.IVs[1] = (ImageView) hatch.findViewById(R.id.imageDynamicHatchATK);
            holder.IVs[2] = (ImageView) hatch.findViewById(R.id.imageDynamicHatchDEF);
            holder.IVs[3] = (ImageView) hatch.findViewById(R.id.imageDynamicHatchSATK);
            holder.IVs[4] = (ImageView) hatch.findViewById(R.id.imageDynamicHatchSDEF);
            holder.IVs[5] = (ImageView) hatch.findViewById(R.id.imageDynamicHatchSPD);
            hatch.setTag(holder);
        }
        else {
            holder = (LayoutHolder) convertView.getTag();
        }
        StoredPokemon storedPokemon = storedPokemonList.get(i);

        if(deleteMode)
            holder.frame.setBackgroundResource(R.drawable.layer_card_background_round_red);
        else
            holder.frame.setBackgroundResource(R.drawable.layer_background_round_selector);

        if(storedPokemon.getPokemonId() > 0)
            holder.icon.setBackground(data.getDrawableFromId(storedPokemon.getPokemonId()).getConstantState().newDrawable());
        else
            holder.icon.setBackground(preloadedDrawables.missingno);
        holder.gender.setBackground(preloadedDrawables.getGenderDrawable(storedPokemon.getGenderId()));
        for(int j = 0; j < 6; j++)
            holder.IVs[j].setBackground(preloadedDrawables.getIVDrawable(j,(storedPokemon.getIVs()[j] == 1)));
        holder.number.setText(String.valueOf(i+1));

        return hatch;
    }
}
