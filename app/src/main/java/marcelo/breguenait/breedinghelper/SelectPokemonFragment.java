package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;


public class SelectPokemonFragment extends PopupDialogFragment {
    //MainActivity baseActivity;

    OnPokemonSelectedListener mCallback;

    GridView gridViewSelector;
    EditText editTextFilter;
    InterfacePokemonSelectorAdapter interfaceSelectorAdapter;

    CheckBox checkBoxCompatible;

    MainActivity callBackActivity;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callBackActivity = (MainActivity) activity;
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (OnPokemonSelectedListener) activity;
            else
                mCallback = (OnPokemonSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPokemonSelectedListener");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view =  inflater.inflate(R.layout.fragment_select_pokemon, container, false);

        interfaceSelectorAdapter = new InterfacePokemonSelectorAdapter(getActivity().getApplicationContext(), PokemonData.getInstance().getOrderedData());

        if(callBackActivity.goalExists())
            interfaceSelectorAdapter.setGoal(callBackActivity.getGoal()); //TODO: Hack porco por enquanto

        gridViewSelector = (GridView) view.findViewById(R.id.gridViewSelectPokemon);
        gridViewSelector.setAdapter(interfaceSelectorAdapter);
        gridViewSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PokemonDataBlock p = (PokemonDataBlock) interfaceSelectorAdapter.getItem(i);
                mCallback.onPokemonSelected(p.id);
                closeFragment();
            }
        });

        editTextFilter = (EditText) view.findViewById(R.id.editTextSelectPokemon);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                interfaceSelectorAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        checkBoxCompatible = (CheckBox) view.findViewById(R.id.checkBoxSelectPokemonCompatible);

        if(getArguments().getBoolean("showOnlyCompatible",false)) {
            if(callBackActivity.goalExists()) {
                checkBoxCompatible.setChecked(true);
                checkBoxCompatible.setEnabled(false);
                interfaceSelectorAdapter.showOnlyCompatible(true);
            }
        }

        checkBoxCompatible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(callBackActivity.goalExists()) {
                    interfaceSelectorAdapter.showOnlyCompatible(b);
                    interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());
                }
            }
        });


        interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());

        setDialogPosition();

        return view;
    }


    public interface OnPokemonSelectedListener {
        public void onPokemonSelected(int id);
    }

}
