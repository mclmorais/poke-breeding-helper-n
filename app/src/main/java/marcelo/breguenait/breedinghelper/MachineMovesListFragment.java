package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import breedingmanager.MoveVerbose;

public class MachineMovesListFragment extends Fragment {
    RecyclerView mRecyclerView;
    ArrayList<MoveVerbose> moveVerboseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moves_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.movesRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(new MachineMoveAdapter(getContext(), null));


        return v;
    }

    public void switchData(ArrayList<MoveVerbose> moveVerboseList) {
        mRecyclerView.swapAdapter(new MachineMoveAdapter(getContext(), moveVerboseList), true);
    }

    public static class MachineMoveAdapter extends RecyclerView.Adapter<MachineMoveAdapter.ViewHolder> {


        final Context context;

        final List<MoveVerbose> moves;

        public MachineMoveAdapter(Context context, ArrayList<MoveVerbose> moves) {
            this.context = context;

            if (moves != null)
                this.moves = moves;
            else
                this.moves = new ArrayList<>(0);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_move_machine, parent, false);
            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int machineNumber = moves.get(position).getMachineNumber();
            if (machineNumber > 100)
                machineNumber -= 100;
            String machineNumberString = String.format("%02d", machineNumber);
            holder.machineNumber.setText(machineNumberString);
            holder.name.setText(moves.get(position).getName());
            holder.effect.setText(moves.get(position).getMoveClass());
            holder.type.setText(moves.get(position).getType());
            int accuracy = moves.get(position).getAccuracy();
            if (accuracy <= 0)
                holder.accuracy.setText("100%");
            else
                holder.accuracy.setText(Integer.toString(accuracy) + "%");
            int power = moves.get(position).getPower();
            if (power <= 0)
                holder.power.setText("―");
            else
                holder.power.setText(Integer.toString(power));

            if (moves.get(position).isHiddenMachine())
                holder.machineType.setText(context.getString(R.string.label_hm));
            else
                holder.machineType.setText(context.getString(R.string.label_tm));

            TypedArray ids = context.getResources().obtainTypedArray(R.array.colorPokemonTypes);

            // Get resource id by its index
            ids.getResourceId(moves.get(position).getTypeId() - 1, -1);
            // be sure to call TypedArray.recycle() when done with the array
            holder.type.setTextColor(ids.getColor(moves.get(position).getTypeId() - 1, -1));
            ids.recycle();
        }

        @Override
        public int getItemCount() {
            return moves.size();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public static class ViewHolder extends RecyclerView.ViewHolder {


            final TextView machineNumber, machineType, name, effect, type, accuracy, power;
            LinearLayout layout;

            public ViewHolder(View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView;
                machineNumber = (TextView) itemView.findViewById(R.id.dynMoveMch_textMachineNumber);
                machineType = (TextView) itemView.findViewById(R.id.dynMoveMch_textMachineType);
                name = (TextView) itemView.findViewById(R.id.dynMoveMch_textName);
                effect = (TextView) itemView.findViewById(R.id.dynMoveMch_textEffect);
                type = (TextView) itemView.findViewById(R.id.dynMoveMch_textType);
                accuracy = (TextView) itemView.findViewById(R.id.dynMoveMch_textAccuracy);
                power = (TextView) itemView.findViewById(R.id.dynMoveMch_textPower);
            }
        }

    }
}

