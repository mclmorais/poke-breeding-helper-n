package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;



class InterfaceItem {
    int itemBackgroundIcon;
    int itemBackgroundDeleteIcon;
    int itemLowerLeftIcon;
    int[] itemIVs = new int[6];
    int itemNumber;

    final int EMPTY = 0;

    final int[] drawableIVActive = {
            R.drawable.iv_circle_checked,
            R.drawable.iv_triangle_checked,
            R.drawable.iv_square_checked,
            R.drawable.iv_heart_checked,
            R.drawable.iv_star_checked,
            R.drawable.iv_diamond_checked
    };

    final int[] drawableIVInactive = {
            R.drawable.iv_circle_clear,
            R.drawable.iv_triangle_clear,
            R.drawable.iv_square_clear,
            R.drawable.iv_heart_clear,
            R.drawable.iv_star_clear,
            R.drawable.iv_diamond_clear
    };

    final int[] drawableLowerLeft = {
            R.drawable.symbol_male,
            R.drawable.symbol_female,
            R.drawable.symbol_ditto
    };




}

class InterfaceDitto extends InterfaceItem {
    InterfaceDitto(int number, HatchInfo hatchInfo) {

        itemBackgroundIcon = R.drawable.ic_ditto_outline;
        itemBackgroundDeleteIcon = R.drawable.ic_ditto_outline_red;

        itemLowerLeftIcon = EMPTY;//drawableLowerLeft[2];

        itemNumber = number;
        //itemNumber = hatchInfo.number;

        for (int i = 0; i < 6; i++) {
            if (hatchInfo.IVs[i] == 1) {
                itemIVs[i] = drawableIVActive[i];
            } else {
                itemIVs[i] = drawableIVInactive[i];
            }
        }
    }
}

class InterfaceEgg extends InterfaceItem{

    InterfaceEgg(int number, HatchInfo eggInfo)
    {

        itemBackgroundIcon = R.drawable.ic_egg_outline;
        itemBackgroundDeleteIcon = R.drawable.ic_egg_outline_red;

        if(eggInfo.gender == Gender.MALE)
            itemLowerLeftIcon = drawableLowerLeft[0];
        else
            itemLowerLeftIcon = drawableLowerLeft[1];

        itemNumber = number;

        for(int i = 0; i < 6; i++) {
            if(eggInfo.IVs[i] == 1) {
                itemIVs[i] = drawableIVActive[i];
            }
            else {
                itemIVs[i] = drawableIVInactive[i];
            }
        }
    }
}

class InterfaceItemAdapter extends BaseAdapter {

    private final ArrayList<InterfaceItem> interfaceItemList;

    boolean deleteStatus;

    private final Context mContext;

    InterfaceItemAdapter(Context mContext, ArrayList<InterfaceItem> interfaceItemList)
    {
        this.mContext = mContext;
        this.interfaceItemList = interfaceItemList;
    }

    @Override
    public int getCount() {
        return interfaceItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return interfaceItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class LayoutHolder {
        RelativeLayout layout;
        ImageView eggIcon;
        ImageView eggGender;
        final ImageView[] eggIVs;
        TextView eggNumber;
        LayoutHolder() {
            eggIVs = new ImageView[6];
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View eggRelativeLayout = convertView;
        LayoutHolder holder;

        if(convertView == null) {
            //If this is not recycled,
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            eggRelativeLayout = inflater.inflate(R.layout.dynamic_view_layout_egg,parent,false);

            holder = new LayoutHolder();
            holder.layout =    (RelativeLayout) eggRelativeLayout.findViewById(R.id.relativeLayoutDynamicEgg);
            holder.eggGender = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggGender);
            holder.eggIcon =   (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggIcon);
            holder.eggIVs[0] = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggHP);
            holder.eggIVs[1] = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggATK);
            holder.eggIVs[2] = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggDEF);
            holder.eggIVs[3] = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggSATK);
            holder.eggIVs[4] = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggSDEF);
            holder.eggIVs[5] = (ImageView) eggRelativeLayout.findViewById(R.id.imageViewDynamicEggSPD);
            holder.eggNumber = (TextView)  eggRelativeLayout.findViewById(R.id.textViewDynamicEggNumber);
            eggRelativeLayout.setTag(holder);
        }
        else {
            holder = (LayoutHolder) convertView.getTag();
        }

        if(deleteStatus)
            holder.eggIcon.setBackgroundResource(interfaceItemList.get(position).itemBackgroundDeleteIcon);
        else
            holder.eggIcon.setBackgroundResource(interfaceItemList.get(position).itemBackgroundIcon);

            holder.eggGender.setBackgroundResource(interfaceItemList.get(position).itemLowerLeftIcon);

        for(int i = 0; i < 6; i++) {
            holder.eggIVs[i].setBackgroundResource(interfaceItemList.get(position).itemIVs[i]);
        }

        holder.eggNumber.setText(String.valueOf(interfaceItemList.get(position).itemNumber +1));

        return eggRelativeLayout;

    }
}
