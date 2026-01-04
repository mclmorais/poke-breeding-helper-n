package marcelo.breguenait.breedinghelper;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

public class PopupDialogFragment extends DialogFragment {

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / (metrics.densityDpi / 160f);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0.12f;
            window.setAttributes(params);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    protected void setDialogPosition() {

        if (getArguments() == null) {
            return;
        }

        int sourceX = getArguments().getInt("x");
        int sourceY = getArguments().getInt("y");

        Window window = getDialog().getWindow();
        if (window == null) return;

        window.setGravity(Gravity.TOP | Gravity.LEFT);

        WindowManager.LayoutParams params = window.getAttributes();

        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = (int) convertPixelsToDp(metrics.widthPixels, requireContext());
        if (sourceX < (screenWidth / 2)) {
            params.x = sourceX + dpToPx(32);
            params.y = sourceY - dpToPx(32);
        } else {
            params.x = sourceX - dpToPx(32);
            params.y = sourceY - dpToPx(32);
        }


        window.setAttributes(params);
    }

    public int dpToPx(float valueInDp) {
        DisplayMetrics metrics = requireActivity().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }

    void closeFragment() {
        getParentFragmentManager().beginTransaction().remove(this).commit();
    }

}
