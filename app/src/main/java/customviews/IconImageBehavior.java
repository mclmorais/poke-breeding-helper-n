package customviews;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import marcelo.breguenait.breedinghelper.R;


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

        Button button = (Button) dependency.findViewById(R.id.moveDex_buttonSelectPokemon);

        View tabLayout = dependency.findViewById(R.id.moveDex_tabLayout);

        //Top goes from 0 to -totalScrollRange. Dividing it by the range gives a proportion from 0 to -1.
        float distanceMovedProportion = (float) appBarLayout.getTop() / (float) appBarLayout.getTotalScrollRange();

        //Flip to positive.
        distanceMovedProportion = -distanceMovedProportion;

        //Image will be the collapsed size minus the tab bar
        float finalImageHeight = appBarLayout.getHeight() - appBarLayout.getTotalScrollRange() - tabLayout.getHeight();

        //Initial size of the picture.
        float initialChildHeight = child.getHeight();

        //Final multiplier of the image.
        float imageResizeMultiplier = finalImageHeight / initialChildHeight;

        //Sets pivots to the lower right
        child.setPivotY(child.getHeight());
        child.setPivotX(child.getWidth());

        //Starts at the bottom of the appbarlayout, moves up its own size, then the appbar, then proportionally the button
        child.setY(appBarLayout.getBottom() - child.getHeight() - tabLayout.getHeight() - button.getHeight() * (1 - distanceMovedProportion));


        //(1-()) makes it get smaller with the proportion
        //proportion is the distance moved times the (1-resize multiplier)
        child.setScaleY(1 - (distanceMovedProportion * (1 - imageResizeMultiplier)));
        child.setScaleX(1 - (distanceMovedProportion * (1 - imageResizeMultiplier)));

        //Brings the button to the left proportionally to the movement and the size of the button
        child.setX(initialChildX - distanceMovedProportion * button.getWidth());

        return true;


    }


}
