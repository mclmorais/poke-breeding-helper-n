package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcelo on 17/12/2015.
 */
public class EggMoveParentAdapter extends RecyclerView.Adapter<EggMoveParentAdapter.ViewHolder> {


    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            this.icon = (ImageView) itemView.findViewById(R.id.dynMoveEggParent_icon);
        }
    }


    private Context context;
    ArrayList<Integer> list;

    public EggMoveParentAdapter(Context context, ArrayList<Integer> list) {
        if(list == null)
            this.list = new ArrayList<>(0);
        else
            this.list = list;



        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_egg_move_parent,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String iconId = "pkmn_" + String.format("%03d", list.get(position));

        //context.getResources().getIdentifier(iconId, "drawable", context.getPackageName());
        holder.icon.setImageDrawable(PokemonData.getInstance().getDrawableFromId(list.get(position)));
        //holder.icon.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().getIdentifier(iconId,"drawable",context.getPackageName())));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

