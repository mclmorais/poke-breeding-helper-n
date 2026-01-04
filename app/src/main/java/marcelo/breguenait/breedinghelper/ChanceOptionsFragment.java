package marcelo.breguenait.breedinghelper;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import marcelo.breguenait.breedinghelper.databinding.FragmentLuckOptionsBinding;

public class ChanceOptionsFragment extends PopupDialogFragment {

    private OnLuckOptionsChange mCallback;
    private FragmentLuckOptionsBinding binding;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            if (getParentFragment() != null) {
                mCallback = (OnLuckOptionsChange) getParentFragment();
            } else {
                mCallback = (OnLuckOptionsChange) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLuckOptionsChange!");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        binding = FragmentLuckOptionsBinding.inflate(localInflater, container, false);

        setDialogPosition();

        setListeners();

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            int width = ViewGroup.LayoutParams.WRAP_CONTENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    private void setListeners() {

        int shinyOptions = mCallback.getShinyStatus();

        binding.checkBoxShinyCharm.setChecked((shinyOptions & ChanceFragment.CHARM) == ChanceFragment.CHARM);
        binding.checkBoxMasudaMethod.setChecked((shinyOptions & ChanceFragment.MASUDA) == ChanceFragment.MASUDA);

        binding.luckOptionsCheckBoxShiny.setChecked((shinyOptions & ChanceFragment.SHINY) == ChanceFragment.SHINY);
        binding.luckOptionsCheckBoxShiny.setText((shinyOptions & ChanceFragment.SHINY) == ChanceFragment.SHINY ? requireActivity().getString(R.string.label_shiny) : requireActivity().getString(R.string.label_normal));


        binding.checkBoxShinyCharm.setOnCheckedChangeListener((buttonView, isChecked) -> mCallback.changeShinyStatus(0x02, isChecked));
        binding.checkBoxMasudaMethod.setOnCheckedChangeListener((buttonView, isChecked) -> mCallback.changeShinyStatus(0x04, isChecked));

        binding.buttonClose.setOnClickListener(v -> closeFragment());


        binding.luckOptionsCheckBoxShiny.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.luckOptionsCheckBoxShiny.setText(requireActivity().getString(R.string.label_shiny));
                mCallback.changeShinyStatus(ChanceFragment.SHINY, true);
            } else {
                binding.luckOptionsCheckBoxShiny.setText(requireActivity().getString(R.string.label_normal));
                mCallback.changeShinyStatus(ChanceFragment.SHINY, false);
            }
        });
    }

    @Override
    protected void setDialogPosition() {
        if (getArguments() == null || getDialog() == null) {
            return;
        }
        Window window = getDialog().getWindow();
        if (window == null) return;

        super.setDialogPosition();
    }

    interface OnLuckOptionsChange {
        int getShinyStatus();

        void changeShinyStatus(int bit, boolean add);
    }


}
