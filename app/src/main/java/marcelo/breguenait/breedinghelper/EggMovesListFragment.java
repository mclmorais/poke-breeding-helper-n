package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

import breedingmanager.MoveVerbose;
import customviews.WrappableGridLayoutManager;

public class EggMovesListFragment extends Fragment {
    RecyclerView mRecyclerView;
    AVLoadingIndicatorView loadingIcon;
    TextView noMovesText;
    ArrayList<MoveVerbose> moveVerboseList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moves_list, container, false);

        noMovesText = (TextView) v.findViewById(R.id.fragmentMoves_textNoMoves);
        noMovesText.setText(R.string.message_no_egg_moves);

        loadingIcon = (AVLoadingIndicatorView) v.findViewById(R.id.fragmentMoves_loadingIcon);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.movesRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(new EggMoveAdapter(getContext(), null, mRecyclerView));


        return v;
    }

    public void switchData(ArrayList<MoveVerbose> moveVerboseList) {
        if (moveVerboseList == null || moveVerboseList.size() == 0)
            noMovesText.setVisibility(View.VISIBLE);
        else
            noMovesText.setVisibility(View.GONE);

        if (moveVerboseList != null) {
            for (int i = 0; i < moveVerboseList.size(); i++) {
                if (moveVerboseList.get(i).getParentIds().isEmpty())
                    moveVerboseList.remove(i);
            }
        }
        mRecyclerView.swapAdapter(new EggMoveAdapter(getContext(), moveVerboseList, mRecyclerView), true);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        loadingIcon.setVisibility(View.GONE);
    }


    public static class EggMoveAdapter extends RecyclerView.Adapter<EggMoveAdapter.ViewHolder> {

        RecyclerView itself;
        Context context;
        List<MoveVerbose> moves;

        public EggMoveAdapter(Context context, ArrayList<MoveVerbose> moves, RecyclerView itself) {
            this.context = context;

            this.itself = itself;

            if (moves != null)
                this.moves = moves;
            else
                this.moves = new ArrayList<>(0);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_move_egg, parent, false);
            return new ViewHolder(v);
        }

        public int dpToPx(@SuppressWarnings("SameParameterValue") float valueInDp) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {


            int x = Math.max(1, itself.getMeasuredWidth() / dpToPx(45));

            holder.parentsRecyclerView.setLayoutManager(new WrappableGridLayoutManager(context, x));
            holder.parentsRecyclerView.setAdapter(new EggMoveParentAdapter(context, moves.get(position).getParentIds()));

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
                holder.machineType.setText(R.string.label_hm);
            else
                holder.machineType.setText(R.string.label_tm);

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

        public static class ViewHolder extends RecyclerView.ViewHolder {


            TextView machineNumber, machineType, name, effect, type, accuracy, power;
            LinearLayout layout;
            RecyclerView parentsRecyclerView;


            public ViewHolder(View itemView) {
                super(itemView);
                layout = (LinearLayout) itemView;
                machineNumber = (TextView) itemView.findViewById(R.id.dynMoveEgg_textMachineNumber);
                machineType = (TextView) itemView.findViewById(R.id.dynMoveEgg_textMachineType);
                name = (TextView) itemView.findViewById(R.id.dynMoveEgg_textName);
                effect = (TextView) itemView.findViewById(R.id.dynMoveEgg_textEffect);
                type = (TextView) itemView.findViewById(R.id.dynMoveEgg_textType);
                accuracy = (TextView) itemView.findViewById(R.id.dynMoveEgg_textAccuracy);
                power = (TextView) itemView.findViewById(R.id.dynMoveEgg_textPower);
                parentsRecyclerView = (RecyclerView) itemView.findViewById(R.id.dynMoveEgg_parentsRecyclerView);
            }
        }
    }

    /**
     * Created by Marcelo on 17/12/2015.
     */
    public static class EggMoveParentAdapter extends RecyclerView.Adapter<EggMoveParentAdapter.ViewHolder> {


        ArrayList<Integer> list;
        private Context context;

        public EggMoveParentAdapter(Context context, ArrayList<Integer> list) {
            if (list == null)
                this.list = new ArrayList<>(0);
            else
                this.list = list;


            this.context = context;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_layout_egg_move_parent, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.icon.setImageDrawable(CachedPokemonIcons.getInstance().getIcon(list.get(position)).getConstantState().newDrawable());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView icon;

            public ViewHolder(View itemView) {
                super(itemView);
                this.icon = (ImageView) itemView.findViewById(R.id.dynMoveEggParent_icon);
            }
        }
    }
}

