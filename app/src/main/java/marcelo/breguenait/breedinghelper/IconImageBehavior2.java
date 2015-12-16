package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Marcelo on 15/12/2015.
 */
public class IconImageBehavior2 extends CoordinatorLayout.Behavior<ImageView> {

    float initialValue = 0.0f;
    float newValue = 0.0f;
    float difference;

    public IconImageBehavior2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        boolean a;
        if(initialValue == 0) {
            initialValue = dependency.getY();
             a = false;
        }
        else {
            newValue = dependency.getY();
            difference = initialValue-newValue;
            child.setTranslationY(-difference);
            a = true;
        }

            int x = dependency.getTop();
            int y = dependency.getHeight();
            AppBarLayout aa = (AppBarLayout) dependency;
            int asddas = aa.getTotalScrollRange();
            int z = dependency.getBottom();

        View v = dependency.findViewById(R.id.moveDex_tabLayout);
        v.getHeight();

            float maxShrinkage = 0.55f;

            float proportion = (1.0f / aa.getTotalScrollRange()) * aa.getTop();
            proportion = -proportion;
            float finalImageHeight = aa.getHeight() - aa.getTotalScrollRange() - v.getHeight();

            float initialChildHeight = child.getHeight();

            float proportion2 = finalImageHeight / initialChildHeight;



        child.setY(z - child.getHeight() - v.getHeight());
        child.setPivotY(child.getHeight());
        child.setPivotX(child.getWidth());
        child.setScaleY(1-(proportion*(1-proportion2)));
        child.setScaleX(1-(proportion*(1-proportion2)));

        int xasd = 3;
        return true;





    }


}
