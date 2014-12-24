package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;




public class HatchAdapter extends BaseAdapter{

    private class PreloadedDrawables {
        Drawable maleIcon;
        Drawable femaleIcon;
        Drawable genderlessIcon;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];

        boolean deleteMode = false;

        private PreloadedDrawables(Context c) {

            maleIcon = c.getResources().getDrawable(R.drawable.symbol_male);
            femaleIcon = c.getResources().getDrawable(R.drawable.symbol_female);
            genderlessIcon = c.getResources().getDrawable(R.drawable.symbol_genderless);

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

        Drawable getGenderDrawable(Gender gender) {
            if (gender == Gender.MALE)          return maleIcon;
            else if (gender == Gender.FEMALE)   return femaleIcon;
            else  return genderlessIcon;
        }

        Drawable getIVDrawable(int position, boolean active) {
            if(active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }

    PreloadedDrawables preloadedDrawables;

    private List<PokemonInfo> hatchList;
    private final LayoutInflater inflater;

    private boolean deleteMode = false;

    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
    }

    public HatchAdapter(List<PokemonInfo> hatchList, Context context) {
        preloadedDrawables = new PreloadedDrawables(context);
        this.hatchList = hatchList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return hatchList.size();
    }

    @Override
    public Object getItem(int position) {
        return hatchList.get(position);
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
            hatch = inflater.inflate(R.layout.dynamic_view_layout_hatch,parent,false);

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
        PokemonInfo pokemonInfo = hatchList.get(i);

        if(deleteMode)
            holder.frame.setBackgroundResource(R.drawable.layer_card_background_round_red);
        else
            holder.frame.setBackgroundResource(R.drawable.layer_card_background_round);

        holder.icon.setBackground(data.getDrawableIdFromId(pokemonInfo.id));
        holder.gender.setBackground(preloadedDrawables.getGenderDrawable(pokemonInfo.gender));
        for(int j = 0; j < 6; j++)
            holder.IVs[j].setBackground(preloadedDrawables.getIVDrawable(j,(pokemonInfo.IVs[j] == 1)));
        holder.number.setText(String.valueOf(i+1));

        return hatch;
    }
}
