package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import breedingmanager.MoveVerbose;

/**
 * Created by Marcelo on 06/01/2016.
 */
public class EggMoveSpinnerAdapter extends BaseAdapter {

    final ArrayList<MoveVerbose> eggMoves;
    final LayoutInflater inflater;
    final DisplayMetrics metrics;

    public EggMoveSpinnerAdapter(ArrayList<MoveVerbose> eggMoves, Context context) {
        this.eggMoves = eggMoves;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        metrics = context.getResources().getDisplayMetrics();
    }

    @Override
    public int getCount() {
        return eggMoves.size();
    }

    @Override
    public Object getItem(int position) {
        return eggMoves.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View natureView = convertView;
        LayoutHolder holder;

        if (convertView == null) {
            natureView = inflater.inflate(R.layout.dynamic_layout_spinner_egg_move, parent, false);
            holder = new LayoutHolder();

            holder.layout = natureView.findViewById(R.id.eggMoveSpinnerLayout);
            holder.viewEggMoveName = (TextView) natureView.findViewById(R.id.moveName);
            holder.viewEggMoveType = (TextView) natureView.findViewById(R.id.moveType);

            natureView.setTag(holder);

        } else {
            holder = (LayoutHolder) natureView.getTag();
        }

        holder.viewEggMoveName.setText(eggMoves.get(position).getName());
        holder.viewEggMoveType.setText(eggMoves.get(position).getType());

        return natureView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View natureView = convertView;
        LayoutHolder holder;

        if (convertView == null) {
            natureView = inflater.inflate(R.layout.dynamic_layout_spinner_egg_move, parent, false);
            holder = new LayoutHolder();

            holder.layout = natureView.findViewById(R.id.eggMoveSpinnerLayout);
            holder.viewEggMoveName = (TextView) natureView.findViewById(R.id.moveName);
            holder.viewEggMoveType = (TextView) natureView.findViewById(R.id.moveType);

            natureView.setTag(holder);

        } else {
            holder = (LayoutHolder) natureView.getTag();
        }

        holder.viewEggMoveName.setText(eggMoves.get(position).getName());
        holder.viewEggMoveType.setText(eggMoves.get(position).getType());


        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(40));
        holder.layout.setLayoutParams(layoutParams);
        return natureView;
    }

    public int dpToPx(@SuppressWarnings("SameParameterValue") float valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    class LayoutHolder {
        View layout;
        TextView viewEggMoveName;
        TextView viewEggMoveType;
    }
}
