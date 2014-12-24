package marcelo.breguenait.breedinghelper;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Created by Marcelo on 08/12/2014.
 */
public class LuckOptionsFragment extends PopupDialogFragment {

    MainActivity baseActivity;

    TypedArray shinyOptionsDrawables;
    String[] shinyOptionsStrings;

    Spinner  spinnerShinyOptions;
    CheckBox checkBoxShinyCharm;
    CheckBox checkBoxMasudaMethod;


    Button buttonClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shinyOptionsDrawables = getResources().obtainTypedArray(R.array.shiny_spinner_options_drawables);
        shinyOptionsStrings = getResources().getStringArray(R.array.shiny_spinner_options_strings);

        View view =  inflater.inflate(R.layout.fragment_luck_options, container, false);

        setDialogPosition();

        setListeners(view);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        baseActivity = ((MainActivity)getActivity());

    }

    void setListeners(View view) {
        boolean shinyStatus[] = getArguments().getBooleanArray("shinyStatus");

        buttonClose = (Button) view.findViewById(R.id.buttonClose);

        checkBoxShinyCharm = (CheckBox) view.findViewById(R.id.checkBoxShinyCharm);
        checkBoxShinyCharm.setChecked(shinyStatus[1]);
        checkBoxMasudaMethod = (CheckBox) view.findViewById(R.id.checkBoxMasudaMethod);
        checkBoxMasudaMethod.setChecked(shinyStatus[2]);

        spinnerShinyOptions = (Spinner) view.findViewById(R.id.spinnerShinyOptions);
        spinnerShinyOptions.setAdapter(new LuckSpinnerAdapter(getActivity().getApplicationContext(), R.layout.row, shinyOptionsStrings));
        spinnerShinyOptions.setSelection(shinyStatus[0]?1:0,false);
        spinnerShinyOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                    baseActivity.setShiny(false);
                else if (position == 1)
                    baseActivity.setShiny(true);
                baseActivity.cardChance.updateEggChance();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBoxShinyCharm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                baseActivity.setShinyCharmActive(isChecked);
                baseActivity.cardChance.updateEggChance();
            }
        });
        checkBoxMasudaMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                baseActivity.setMasudaMethodActive(isChecked);
                baseActivity.cardChance.updateEggChance();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });
    }

    public class LuckSpinnerAdapter extends ArrayAdapter<String> {


        public LuckSpinnerAdapter(Context context, int textViewResourceId,   String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater= getActivity().getLayoutInflater();
            View row=inflater.inflate(R.layout.row, parent, false);
            TextView label=(TextView)row.findViewById(R.id.company);
            label.setText(shinyOptionsStrings[position]);



            ImageView icon=(ImageView)row.findViewById(R.id.image);
            icon.setImageResource(shinyOptionsDrawables.getResourceId(position,-1));

            return row;
        }
    }


}
