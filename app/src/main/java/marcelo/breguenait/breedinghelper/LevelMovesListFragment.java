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

public class LevelMovesListFragment extends Fragment {
    RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moves_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.movesRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(new LevelMoveAdapter(getContext(), null));


        return v;
    }

    public void switchData(ArrayList<MoveInfo> moveInfoList) {
        if (mRecyclerView != null)
            mRecyclerView.swapAdapter(new LevelMoveAdapter(getContext(), moveInfoList), true);
    }

    public static class LevelMoveAdapter extends RecyclerView.Adapter<LevelMoveAdapter.ViewHolder> {


        Context context;

        List<MoveInfo> moves;

        public LevelMoveAdapter(Context context, ArrayList<MoveInfo> moves) {
            this.context = context;

            if (moves != null)
                this.moves = moves;
            else
                this.moves = new ArrayList<>(0);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_move_levelup, parent, false);


            return new ViewHolder(v);

        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            int level = moves.get(position).getLevel();
            if (level == 1)
                holder.level.setText("―");
            else
                holder.level.setText(Integer.toString(level));
            holder.name.setText(moves.get(position).getName());
            holder.effect.setText(moves.get(position).getMoveClass());
            holder.type.setText(moves.get(position).getType());
            int accuracy = moves.get(position).getAccuracy();
            if (accuracy <= 0)
                holder.accuracy.setText("100%");
            else
                holder.accuracy.setText(Integer.toString(moves.get(position).getAccuracy()) + "%");
            int power = moves.get(position).getPower();
            if (power <= 0)
                holder.power.setText("―");
            else
                holder.power.setText(Integer.toString(moves.get(position).getPower()));

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


            TextView level, name, effect, type, accuracy, power;
            LinearLayout layout;

            public ViewHolder(View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView;
                level = (TextView) itemView.findViewById(R.id.dynMoveLvl_textLevel);
                name = (TextView) itemView.findViewById(R.id.dynMoveLvl_textName);
                effect = (TextView) itemView.findViewById(R.id.dynMoveLvl_textEffect);
                type = (TextView) itemView.findViewById(R.id.dynMoveLvl_textType);
                accuracy = (TextView) itemView.findViewById(R.id.dynMoveLvl_textAccuracy);
                power = (TextView) itemView.findViewById(R.id.dynMoveLvl_textPower);
            }
        }
    }
}

