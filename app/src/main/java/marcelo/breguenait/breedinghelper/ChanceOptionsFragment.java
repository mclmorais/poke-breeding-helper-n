package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class ChanceOptionsFragment extends PopupDialogFragment {


    BreedingFragment baseActivity;

    private CheckBox checkBoxShinyCharm;
    private CheckBox checkBoxMasudaMethod;
    private CheckBox checkBoxShiny;

    private OnLuckOptionsChange mCallback;

    private Button buttonClose;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
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

//        shinyOptionsDrawables = getResources().obtainTypedArray(R.array.shiny_spinner_options_drawables);
//        shinyOptionsStrings = getResources().getStringArray(R.array.shiny_spinner_options_strings);

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_luck_options, container, false);

        setDialogPosition();

        setListeners(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void setListeners(View view) {

        int shinyOptions = mCallback.getShinyStatus();

        buttonClose = (Button) view.findViewById(R.id.buttonClose);

        checkBoxShinyCharm = (CheckBox) view.findViewById(R.id.checkBoxShinyCharm);
        checkBoxShinyCharm.setChecked((shinyOptions & ChanceFragment.CHARM) == ChanceFragment.CHARM);

        checkBoxMasudaMethod = (CheckBox) view.findViewById(R.id.checkBoxMasudaMethod);
        checkBoxMasudaMethod.setChecked((shinyOptions & ChanceFragment.MASUDA) == ChanceFragment.MASUDA);

        checkBoxShiny = (CheckBox) view.findViewById(R.id.luckOptionsCheckBoxShiny);
        checkBoxShiny.setChecked((shinyOptions & ChanceFragment.SHINY) == ChanceFragment.SHINY);
        checkBoxShiny.setText((shinyOptions & ChanceFragment.SHINY) == ChanceFragment.SHINY ? getActivity().getString(R.string.label_shiny) : getActivity().getString(R.string.label_normal));


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


        checkBoxShiny.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkBoxShiny.setText(getActivity().getString(R.string.label_shiny));
                    mCallback.changeShinyStatus(ChanceFragment.SHINY, true);
                } else {
                    checkBoxShiny.setText(getActivity().getString(R.string.label_normal));
                    mCallback.changeShinyStatus(ChanceFragment.SHINY, false);
                }
            }
        });
    }

    @Override
    protected void setDialogPosition() {
        if (getArguments() == null) {
            return;
        }

        int sourceX = getArguments().getInt("x");
        int sourceY = getArguments().getInt("y");

        Window window = getDialog().getWindow();

        // set "origin" to top left corner
        window.setGravity(Gravity.TOP | Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = (int) convertPixelsToDp(metrics.widthPixels, getActivity().getApplicationContext());
        if (sourceX < (screenWidth / 2)) {
            // Just an example; edit to suit your needs.
            params.x = sourceX + dpToPx(32); // about half of confirm button size left of source view
            params.y = sourceY - dpToPx(32); // above source view
        } else {
            params.x = sourceX - dpToPx(180); // about half of confirm button size left of source view
            params.y = sourceY - dpToPx(24); // above source view
        }
        window.setAttributes(params);
    }

    interface OnLuckOptionsChange {
        int getShinyStatus();

        void changeShinyStatus(int bit, boolean add);
    }


}

