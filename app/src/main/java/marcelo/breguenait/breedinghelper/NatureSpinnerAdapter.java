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

/**
 * Created by Marcelo on 06/01/2016.
 */
public class NatureSpinnerAdapter extends BaseAdapter {

    final ArrayList<InterfaceNature> interfaceNatures;
    final LayoutInflater inflater;
    final DisplayMetrics metrics;

    public NatureSpinnerAdapter(ArrayList<InterfaceNature> interfaceNatures, Context context) {
        this.interfaceNatures = interfaceNatures;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        metrics = context.getResources().getDisplayMetrics();
    }

    @Override
    public int getCount() {
        return interfaceNatures.size();
    }

    @Override
    public Object getItem(int position) {
        return interfaceNatures.get(position);
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
            natureView = inflater.inflate(R.layout.dynamic_layout_nature, parent, false);
            holder = new LayoutHolder();

            holder.layout = natureView.findViewById(R.id.dynNature_layout);
            holder.viewNatureName = (TextView) natureView.findViewById(R.id.dynNature_name);
            holder.viewIncreasedStatName = (TextView) natureView.findViewById(R.id.dynNature_increasedStat);
            holder.viewDecreasedStatName = (TextView) natureView.findViewById(R.id.dynNature_decreasedStat);

            natureView.setTag(holder);

        } else {
            holder = (LayoutHolder) natureView.getTag();
        }

        holder.viewNatureName.setText(interfaceNatures.get(position).natureName);

        String increasedStat = interfaceNatures.get(position).increasedStatName;
        String decreasedStat = interfaceNatures.get(position).decreasedStatName;

        if (increasedStat.equals(decreasedStat)) {
            holder.viewIncreasedStatName.setVisibility(View.GONE);
            holder.viewDecreasedStatName.setText(R.string.nature_neutral);
        } else {
            holder.viewIncreasedStatName.setVisibility(View.VISIBLE);
            holder.viewIncreasedStatName.setText("+" + interfaceNatures.get(position).increasedStatName);
            holder.viewDecreasedStatName.setText("-" + interfaceNatures.get(position).decreasedStatName);
        }

        return natureView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View natureView = convertView;
        LayoutHolder holder;

        if (convertView == null) {
            natureView = inflater.inflate(R.layout.dynamic_layout_nature, parent, false);
            holder = new LayoutHolder();

            holder.layout = natureView.findViewById(R.id.dynNature_layout);
            holder.viewNatureName = (TextView) natureView.findViewById(R.id.dynNature_name);
            holder.viewIncreasedStatName = (TextView) natureView.findViewById(R.id.dynNature_increasedStat);
            holder.viewDecreasedStatName = (TextView) natureView.findViewById(R.id.dynNature_decreasedStat);

            natureView.setTag(holder);

        } else {
            holder = (LayoutHolder) natureView.getTag();
        }

        holder.viewNatureName.setText(interfaceNatures.get(position).natureName);

        String increasedStat = interfaceNatures.get(position).increasedStatName;
        String decreasedStat = interfaceNatures.get(position).decreasedStatName;

        if (increasedStat.equals(decreasedStat)) {
            holder.viewIncreasedStatName.setVisibility(View.GONE);
            holder.viewDecreasedStatName.setText(R.string.nature_neutral);
        } else {
            holder.viewIncreasedStatName.setVisibility(View.VISIBLE);
            holder.viewIncreasedStatName.setText("+" + interfaceNatures.get(position).increasedStatName);
            holder.viewDecreasedStatName.setText("-" + interfaceNatures.get(position).decreasedStatName);
        }


        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(40));
        holder.layout.setLayoutParams(layoutParams);
        return natureView;
    }

    public int dpToPx(@SuppressWarnings("SameParameterValue") float valueInDp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    class LayoutHolder {
        View layout;
        TextView viewNatureName;
        TextView viewIncreasedStatName;
        TextView viewDecreasedStatName;
    }
}
