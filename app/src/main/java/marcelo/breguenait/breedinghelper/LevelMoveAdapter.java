package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcelo on 15/12/2015.
 */
public class LevelMoveAdapter extends RecyclerView.Adapter<LevelMoveAdapter.ViewHolder> {


    Context context;

    List<MoveInfo> moves;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView level, name, effect, type, accuracy, power;
        LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView;
            level       = (TextView) itemView.findViewById(R.id.dynMoveLvl_textLevel);
            name        = (TextView) itemView.findViewById(R.id.dynMoveLvl_textName);
            effect      = (TextView) itemView.findViewById(R.id.dynMoveLvl_textEffect);
            type        = (TextView) itemView.findViewById(R.id.dynMoveLvl_textType);
            accuracy    = (TextView) itemView.findViewById(R.id.dynMoveLvl_textAccuracy);
            power       = (TextView) itemView.findViewById(R.id.dynMoveLvl_textPower);
        }
    }

    public LevelMoveAdapter(Context context, ArrayList<MoveInfo> moves) {
        this.context = context;

        if(moves != null)
            this.moves = moves;
        else
            this.moves = new ArrayList<>(0);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_move_levelup,parent,false);


        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.level.setText(Integer.toString(moves.get(position).level));
        holder.name.setText(moves.get(position).name);
        holder.effect.setText(moves.get(position).effect);
        holder.type.setText(moves.get(position).type);
        int accuracy = moves.get(position).accuracy;
        if(accuracy <= 0)
            holder.accuracy.setText("100%");
        else
            holder.accuracy.setText(Integer.toString(moves.get(position).accuracy)+"%");
        int power = moves.get(position).power;
        if(power <= 0)
            holder.power.setText("―");
        else
            holder.power.setText(Integer.toString(moves.get(position).power));

        TypedArray ids = context.getResources().obtainTypedArray(R.array.type_colors);

        // Get resource id by its index
        ids.getResourceId(moves.get(position).typeId - 1, -1);
        // be sure to call TypedArray.recycle() when done with the array
        holder.type.setTextColor(ids.getColor(moves.get(position).typeId-1,-1));
        ids.recycle();
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }
}

class MoveInfo {
    int level, accuracy, power, typeId;
    String name, effect, type;

    public MoveInfo(int level, int accuracy, int power, String name, String effect, String type, int typeId) {
        this.level = level;
        this.accuracy = accuracy;
        this.power = power;
        this.name = name;
        this.effect = effect;
        this.type = type;
        this.typeId = typeId;
    }
}

