package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link marcelo.breguenait.breedinghelper.LuckFragment.TemporaryLuckInterface} interface
 * to handle interaction events.
 */
public class LuckFragment extends Fragment {

    private class PreloadedDrawables {
        Drawable maleIcon;
        Drawable femaleIcon;
        final Drawable[] IVActive = new Drawable[6];
        final Drawable[] IVInactive = new Drawable[6];

        private PreloadedDrawables(Context c) {

            maleIcon = c.getResources().getDrawable(R.drawable.symbol_male);
            femaleIcon = c.getResources().getDrawable(R.drawable.symbol_female);

            IVActive[0] = c.getResources().getDrawable(R.drawable.iv_circle_checked);
            IVActive[1] = c.getResources().getDrawable(R.drawable.iv_triangle_checked);
            IVActive[2] = c.getResources().getDrawable(R.drawable.iv_square_checked);
            IVActive[3] = c.getResources().getDrawable(R.drawable.iv_heart_checked);
            IVActive[4] = c.getResources().getDrawable(R.drawable.iv_star_checked);
            IVActive[5] = c.getResources().getDrawable(R.drawable.iv_diamond_checked);

            IVInactive[0] = c.getResources().getDrawable(R.drawable.iv_circle_clear);
            IVInactive[1] = c.getResources().getDrawable(R.drawable.iv_triangle_clear);
            IVInactive[2] = c.getResources().getDrawable(R.drawable.iv_square_clear);
            IVInactive[3] = c.getResources().getDrawable(R.drawable.iv_heart_clear);
            IVInactive[4] = c.getResources().getDrawable(R.drawable.iv_star_clear);
            IVInactive[5] = c.getResources().getDrawable(R.drawable.iv_diamond_clear);
        }

        Drawable getGenderDrawable(Gender gender) {
            if (gender == Gender.MALE)          return maleIcon;
            else if (gender == Gender.FEMALE)   return femaleIcon;
            else                                return maleIcon; //TODO: fazer genderless
        }

        Drawable getIVDrawable(int position, boolean active) {
            if(active)
                return IVActive[position];
            else
                return IVInactive[position];
        }


    }

    PreloadedDrawables preloadedDrawables;

    List<ChanceData> chanceDataList;
    List<View> interfaceChanceList = new ArrayList<>();

    private TemporaryLuckInterface mListener;
    LinearLayout layoutChances;
    LayoutInflater inflater2;


    public LuckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_luck, container, false);

        preloadedDrawables = new PreloadedDrawables(getActivity().getApplicationContext());
        layoutChances = (LinearLayout) v.findViewById(R.id.luckFragmentLayoutChances);

        inflater2 = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return v;

    }

    public void updateCurrentChances(List<ChanceData> list) {
        chanceDataList = list;
        layoutChances.removeAllViews();

        interfaceChanceList.clear();

        Collections.reverse(list);

        for(int i = 0; i < list.size(); i++) {
            if(i > 4) break;
            if(list.get(i).chance < 0.0001) continue;
            View v = inflater2.inflate(R.layout.dynamic_view_layout_chance_data,layoutChances,false);
            layoutChances.addView(v);
            interfaceChanceList.add(v);
           // interfaceChanceList.add(inflater2.inflate(R.layout.dynamic_view_layout_chance_data,layoutChances,true));
        }



        for(int i = 0; i < interfaceChanceList.size(); i++) {



            TextView v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceFirstNumber);
            v.setText(String.valueOf(list.get(i).firstPokemonNumber+1));

            v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChanceSecondNumber);
            v.setText(String.valueOf(list.get(i).secondPokemonNumber+1));

            v = (TextView) interfaceChanceList.get(i).findViewById(R.id.textDynamicChancePercentage);
            v.setText(String.format("%.2f",list.get(i).chance*100) + "%");

            ImageView firstIcon = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstIcon);
            firstIcon.setBackground(PokemonData.getInstance().getDrawableIdFromId(list.get(i).firstPokemon.id));

            ImageView secondIcon = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondIcon);
            secondIcon.setBackground(PokemonData.getInstance().getDrawableIdFromId(list.get(i).secondPokemon.id));

            ImageView firstIVs[] = new ImageView[6];
            firstIVs[0] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstHP);
            firstIVs[1] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstATK);
            firstIVs[2] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstDEF);
            firstIVs[3] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSATK);
            firstIVs[4] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSDEF);
            firstIVs[5] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceFirstSPD);
            for (int j = 0; j < 6; j++) {
                firstIVs[j].setBackground(preloadedDrawables.getIVDrawable(j, list.get(i).firstPokemon.IVs[j] != 0));
            }

            ImageView secondIVs[] = new ImageView[6];
            secondIVs[0] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondHP);
            secondIVs[1] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondATK);
            secondIVs[2] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondDEF);
            secondIVs[3] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSATK);
            secondIVs[4] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSDEF);
            secondIVs[5] = (ImageView) interfaceChanceList.get(i).findViewById(R.id.imageDynamicChanceSecondSPD);

            for (int j = 0; j < 6; j++) {
                secondIVs[j].setBackground(preloadedDrawables.getIVDrawable(j, list.get(i).secondPokemon.IVs[j] != 0));
            }

            
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mListener = (TemporaryLuckInterface) activity;
            else
                mListener = (TemporaryLuckInterface) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface TemporaryLuckInterface {
        // TODO: Update argument type and name
    }

}
