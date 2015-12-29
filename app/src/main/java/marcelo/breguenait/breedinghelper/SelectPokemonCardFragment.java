package marcelo.breguenait.breedinghelper;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectPokemonCardFragment extends Fragment {

    @Bind(R.id.GRIDTESTE)
    GridView testeGridView;


    interface selectPokemonInterface {
        void closeSelectPokemonFragment();
    }

    public selectPokemonInterface mCallback;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (selectPokemonInterface) getTargetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_card_select_pokemon, container, false);
        ButterKnife.bind(this,v);

        InterfacePokemonSelectorAdapter interfaceSelectorAdapter = new InterfacePokemonSelectorAdapter(getActivity().getApplicationContext());

        testeGridView.setAdapter(interfaceSelectorAdapter);
        testeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mCallback.closeSelectPokemonFragment();
            }
        });
        return v;
    }

}



