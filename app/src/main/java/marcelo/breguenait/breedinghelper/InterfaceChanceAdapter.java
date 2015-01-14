package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static marcelo.breguenait.breedinghelper.R.id.textDynamicChanceSecondNumber;

/**
 * Created by Marcelo on 14/01/2015.
 */
public class InterfaceChanceAdapter extends BaseAdapter {

    List<ChanceData> chanceDataList;
    private final LayoutInflater inflater;

    InterfaceChanceAdapter(List<ChanceData> chanceDataList, Context context) {
        this.chanceDataList = chanceDataList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<ChanceData> c) {
        chanceDataList = c;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chanceDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return chanceDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class LayoutHolder {
        View      frame;
        TextView firstNumber;
        TextView secondNumber;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View chance = convertView;
        LayoutHolder holder;

        if(convertView == null) {
            holder = new LayoutHolder();
            chance = inflater.inflate(R.layout.dynamic_view_layout_chance_data,parent,false);

            holder.firstNumber = (TextView) chance.findViewById(R.id.textDynamicChanceFirstNumber);
            holder.secondNumber = (TextView) chance.findViewById(textDynamicChanceSecondNumber);
            chance.setTag(holder);
        }
        else {
            holder = (LayoutHolder) convertView.getTag();
        }

        holder.firstNumber.setText(String.valueOf(chanceDataList.get(position).firstPokemonNumber+1));
        holder.secondNumber.setText(String.valueOf(chanceDataList.get(position).secondPokemonNumber+1));
        return chance;
    }

}
