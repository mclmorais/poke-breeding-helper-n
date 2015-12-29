package marcelo.breguenait.breedinghelper;


import android.animation.Animator;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class BreedingFragment extends Fragment implements SelectPokemonCardFragment.selectPokemonInterface{

    InitialActivity initialActivity;

    ActionBarDrawerToggle drawerToggle;

    @Bind(R.id.breeding_goalIVsLayout)
    View goalIVsLayout;

    @Bind(R.id.breeding_toolbar)
    Toolbar toolbar;




    @Bind(R.id.REVEALFRAME)
    FrameLayout mRevealView;


    FrameLayout selectPokemonButton;

    public BreedingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        drawerToggle.syncState();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_breeding, container, false);
        ButterKnife.bind(this, v);

        selectPokemonButton = (FrameLayout) goalIVsLayout.findViewById(R.id.frameLayoutPokemonSelectorButton);

        selectPokemonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reveal();
            }
        });




        initialActivity = (InitialActivity) getActivity();

        drawerToggle = new ActionBarDrawerToggle(getActivity(),
                initialActivity.getDrawer(), toolbar,
                R.string.drawer_open,
                R.string.drawer_close);

        initialActivity.mDrawer.setDrawerListener(drawerToggle);

        FragmentManager fragmentManager = getFragmentManager();

        SelectPokemonCardFragment fragment = new SelectPokemonCardFragment();
        fragment.setTargetFragment(this,0);

        fragmentManager.beginTransaction().replace(R.id.REVEALFRAME, fragment).commit();


        return v;
    }

    void openSelectPokemonFragment(View v) {
//        FragmentManager fm = getFragmentManager();
//        SelectPokemonCardFragment selectPokemonCardFragment = new SelectPokemonCardFragment();
//        Bundle b = addPositionAsArguments(v);
//        selectPokemonCardFragment.setArguments(b);
//        selectPokemonCardFragment.setTargetFragment(this, 0);
//        selectPokemonCardFragment.show(fm, "");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }



    void reveal() {

        int cx = 0;
        int cy = 0;

        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SupportAnimator animator =
                    ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(450);
            mRevealView.setVisibility(View.VISIBLE);
            animator.start();
        } else {
            Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
            anim.setDuration(450);
            mRevealView.setVisibility(View.VISIBLE);
            anim.start();
        }





    }

    @Override
    public void closeSelectPokemonFragment() {
        mRevealView.setVisibility(View.GONE);
//        int cx = 0;
//        int cy = 0;
//
//        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());
//
//        SupportAnimator animator =
//                ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
//        animator.setInterpolator(new AccelerateDecelerateInterpolator());
//        animator.setDuration(450);
//        animator.reverse();
//        mRevealView.setVisibility(View.GONE);
//        animator.start();

    }
}
