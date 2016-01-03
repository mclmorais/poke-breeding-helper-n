package marcelo.breguenait.breedinghelper;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.nvView)
    NavigationView nvDrawer;


    Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        ButterKnife.bind(this);
        //       cleanOldFragments();
        setupDrawerContent(nvDrawer);

        if (currentFragment == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment fragment = null;
            Class fragmentClass;

            fragmentClass = BreedingFragment.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            currentFragment = fragment;
            nvDrawer.getMenu().getItem(0).setChecked(true);
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "MainActivity - onDestroy");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("Lifecycle", "MainActivity - onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        final MenuItem item = menuItem;

                        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
                            @Override
                            public void onDrawerSlide(View drawerView, float slideOffset) {

                            }

                            @Override
                            public void onDrawerOpened(View drawerView) {

                            }

                            @Override
                            public void onDrawerClosed(View drawerView) {
                                selectDrawerItem(item);
                            }

                            @Override
                            public void onDrawerStateChanged(int newState) {

                            }
                        });


                        mDrawer.closeDrawers();
                        return true;
                    }

                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        //Fragment fragment = null;
//        cleanOldFragments();
        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = MoveDexFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = BreedingFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = PreferencesFragment.class;
                break;
            default:
                fragmentClass = MoveDexFragment.class;
        }

        if (currentFragment == null || (currentFragment.getClass() != fragmentClass)) {
            try {
                currentFragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().
                setCustomAnimations(R.anim.sliderightleft, R.anim.slideleftright, R.anim.sliderightleft, R.anim.slideleftright).
                replace(R.id.flContent, currentFragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
//        mDrawer.closeDrawers();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //drawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public DrawerLayout getDrawer() {
        return mDrawer;
    }


}


