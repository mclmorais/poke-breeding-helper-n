package customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import marcelo.breguenait.breedinghelper.R;


@SuppressWarnings("unused")
public class IconImageBehavior extends CoordinatorLayout.Behavior<ImageView> {

    float initialValue = 0.0f;
    float newValue = 0.0f;
    float difference;
    float initialChildX = 0.0f;

    public IconImageBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    void init(ImageView child) {
        if (Float.compare(initialChildX, 0) == 0)
            initialChildX = child.getX();
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageView child, View dependency) {
        init(child);

        AppBarLayout appBarLayout = (AppBarLayout) dependency;

        FloatingActionButton button = dependency.findViewById(R.id.moveDex_buttonSelectPokemon);

        View tabLayout = dependency.findViewById(R.id.moveDex_tabLayout);

        float distanceMovedProportion = (float) appBarLayout.getTop() / (float) appBarLayout.getTotalScrollRange();

        distanceMovedProportion = -distanceMovedProportion;

        float finalImageHeight = appBarLayout.getHeight() - appBarLayout.getTotalScrollRange() - tabLayout.getHeight();

        float initialChildHeight = child.getHeight();

        float imageResizeMultiplier = finalImageHeight / initialChildHeight;

        child.setPivotY(child.getHeight());
        child.setPivotX(child.getWidth());

        child.setY(appBarLayout.getBottom() - child.getHeight() - tabLayout.getHeight() - button.getHeight() * (1 - distanceMovedProportion));


        child.setScaleY(1 - (distanceMovedProportion * (1 - imageResizeMultiplier)));
        child.setScaleX(1 - (distanceMovedProportion * (1 - imageResizeMultiplier)));

        child.setX(initialChildX - distanceMovedProportion * button.getWidth());

        return true;


    }


}
