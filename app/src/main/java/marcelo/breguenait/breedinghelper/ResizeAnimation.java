package marcelo.breguenait.breedinghelper;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * an animation for resizing the view.
 */
public class ResizeAnimation extends Animation {
    final int startHeight;
    final int targetHeight;
    View view;

    public ResizeAnimation(View view, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        startHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}