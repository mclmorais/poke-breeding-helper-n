package marcelo.breguenait.breedinghelper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

class ExpandAnimation {

    private final View expandableView;
    private boolean expanded;
    private boolean initialized;
    private boolean stopAnimation = false;
    private Animator mAnimator;

    public boolean isCollapsed() {
        return !expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    private ValueAnimator vAnimator;

    public ExpandAnimation(final View expandableView) {
        this.expandableView = expandableView;
        expanded = false;

        expandableView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        expandableView.getViewTreeObserver().removeOnPreDrawListener(this);
                        int currentVis = expandableView.getVisibility();
                        expanded = currentVis == View.VISIBLE;
                        expandableView.setVisibility(View.GONE);

                        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                        expandableView.measure(widthSpec,heightSpec);
                        vAnimator = slideAnimator(0,expandableView.getMeasuredHeight());
                        vAnimator.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                expanded = true;
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        initialized = true;
                        expandableView.setVisibility(currentVis);
                        return true;
                    }
                }
        );


    }

    public void expand() {
        expandableView.setVisibility(View.VISIBLE);
        if(initialized)
            vAnimator.start();

    }
    public void collapse() {
        int finalHeight = expandableView.getHeight();

        mAnimator = slideAnimator(finalHeight, 0);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!stopAnimation) {
                    expandableView.setVisibility(View.GONE);
                    expanded = false;
                }
                else {
                    expandableView.setVisibility(View.VISIBLE);
                    expanded = true;
                    stopAnimation = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                expandableView.setVisibility(View.VISIBLE);
                stopAnimation = true;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAnimator.start();
    }

    void stopAnimation() {
        if(vAnimator != null)
            vAnimator.cancel();
        if(mAnimator != null)
            mAnimator.cancel();
    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = expandableView.getLayoutParams();
                layoutParams.height = value;
                expandableView.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

}
