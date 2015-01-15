package marcelo.breguenait.breedinghelper;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

    OnLuckOptionsChange mCallback;

    Button buttonClose;

    interface OnLuckOptionsChange {
        int getShinyStatus();
        void changeShinyStatus(int bit, boolean add);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (OnLuckOptionsChange) activity;
            else
                mCallback = (OnLuckOptionsChange) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnLuckOptionsChange!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        shinyOptionsDrawables = getResources().obtainTypedArray(R.array.shiny_spinner_options_drawables);
        shinyOptionsStrings = getResources().getStringArray(R.array.shiny_spinner_options_strings);

        View view =  inflater.inflate(R.layout.fragment_luck_options, container, false);

        setDialogPosition();

        setListeners(view);

        return view;
    }

    void setListeners(View view) {

        int shinyOptions = mCallback.getShinyStatus();

        buttonClose = (Button) view.findViewById(R.id.buttonClose);

        checkBoxShinyCharm = (CheckBox) view.findViewById(R.id.checkBoxShinyCharm);
        checkBoxShinyCharm.setChecked((shinyOptions&LuckFragment.CHARM)==LuckFragment.CHARM);

        checkBoxMasudaMethod = (CheckBox) view.findViewById(R.id.checkBoxMasudaMethod);
        checkBoxMasudaMethod.setChecked((shinyOptions&LuckFragment.MASUDA)==LuckFragment.MASUDA);


        spinnerShinyOptions = (Spinner) view.findViewById(R.id.spinnerShinyOptions);
        spinnerShinyOptions.setAdapter(new LuckSpinnerAdapter(getActivity().getApplicationContext(), R.layout.row, shinyOptionsStrings));
        spinnerShinyOptions.setSelection((shinyOptions&LuckFragment.SHINY)==LuckFragment.SHINY?1:0,false);
        spinnerShinyOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                    mCallback.changeShinyStatus(0x01, false);
                else if (position == 1)
                    mCallback.changeShinyStatus(0x01, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBoxShinyCharm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCallback.changeShinyStatus(0x02, isChecked);
            }
        });
        checkBoxMasudaMethod.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCallback.changeShinyStatus(0x04, isChecked);
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

    @Override
    protected void setDialogPosition() {
        if(getArguments() == null) {
            return;
        }

        int sourceX = getArguments().getInt("x");
        int sourceY = getArguments().getInt("y");

        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP|Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = (int) convertPixelsToDp(metrics.widthPixels,getActivity().getApplicationContext());
        if(sourceX < (screenWidth/2)) {
            // Just an example; edit to suit your needs.
            params.x = sourceX + dpToPx(32); // about half of confirm button size left of source view
            params.y = sourceY -  dpToPx(32); // above source view
        }
        else {
            params.x = sourceX - dpToPx(192); // about half of confirm button size left of source view
            params.y = sourceY -  dpToPx(24); // above source view
        }
        window.setAttributes(params);
    }
}
