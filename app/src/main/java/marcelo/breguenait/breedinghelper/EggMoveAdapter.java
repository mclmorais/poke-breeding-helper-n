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
 * Created by Marcelo on 17/12/2015.
 */
public class EggMoveAdapter extends RecyclerView.Adapter<EggMoveAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {


        TextView machineNumber, machineType,  name, effect, type, accuracy, power;
        LinearLayout layout;
        AutofitRecyclerView parentsRecyclerView;

        public ViewHolder(View itemView) {
            super(itemView);
            layout              = (LinearLayout) itemView;
            machineNumber       = (TextView) itemView.findViewById(R.id.dynMoveEgg_textMachineNumber);
            machineType         = (TextView) itemView.findViewById(R.id.dynMoveEgg_textMachineType);
            name                = (TextView) itemView.findViewById(R.id.dynMoveEgg_textName);
            effect              = (TextView) itemView.findViewById(R.id.dynMoveEgg_textEffect);
            type                = (TextView) itemView.findViewById(R.id.dynMoveEgg_textType);
            accuracy            = (TextView) itemView.findViewById(R.id.dynMoveEgg_textAccuracy);
            power               = (TextView) itemView.findViewById(R.id.dynMoveEgg_textPower);
            parentsRecyclerView = (AutofitRecyclerView) itemView.findViewById(R.id.dynMoveEgg_parentsRecyclerView);
        }
    }


    Context context;
    List<MoveInfo> moves;

    public EggMoveAdapter(Context context, ArrayList<MoveInfo> moves) {
        this.context = context;

        if(moves != null)
            this.moves = moves;
        else
            this.moves = new ArrayList<>(0);

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_move_egg,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ArrayList<String> TEMPORARIO = new ArrayList<>(3);
        TEMPORARIO.add("");
        TEMPORARIO.add("");
        TEMPORARIO.add("");


        holder.parentsRecyclerView.setAdapter(new EggMoveParentAdapter(context,TEMPORARIO));
//        notifyDataSetChanged();


        //TODO: to be modified into the egg moves adapter
        int machineNumber = moves.get(position).getMachineNumber();
        if(machineNumber>100)
            machineNumber -= 100;
        String machineNumberString = String.format("%02d",machineNumber);
        holder.machineNumber.setText(machineNumberString);
        holder.name.setText(moves.get(position).getName());
        holder.effect.setText(moves.get(position).getMoveClass());
        holder.type.setText(moves.get(position).getType());
        int accuracy = moves.get(position).getAccuracy();
        if(accuracy <= 0)
            holder.accuracy.setText("100%");
        else
            holder.accuracy.setText(Integer.toString(accuracy)+"%");
        int power = moves.get(position).getPower();
        if(power <= 0)
            holder.power.setText("―");
        else
            holder.power.setText(Integer.toString(power));

        if(moves.get(position).isHiddenMachine())
            holder.machineType.setText("HM");
        else
            holder.machineType.setText("TM");

        TypedArray ids = context.getResources().obtainTypedArray(R.array.type_colors);

        // Get resource id by its index
        ids.getResourceId(moves.get(position).getTypeId() - 1, -1);
        // be sure to call TypedArray.recycle() when done with the array
        holder.type.setTextColor(ids.getColor(moves.get(position).getTypeId()-1,-1));
        ids.recycle();
    }

    @Override
    public int getItemCount() {
        return moves.size();
    }
}
