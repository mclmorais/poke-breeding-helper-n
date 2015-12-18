package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
    List<String> list;

    public EggMoveParentAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_egg_move_parent,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pkmn_076));
    }

    @Override
    public int getItemCount() {
        return 20;
    }
}

