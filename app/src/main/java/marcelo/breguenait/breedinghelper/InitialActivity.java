package marcelo.breguenait.breedinghelper;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class InitialActivity extends AppCompatActivity {


    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.nvView)
    NavigationView nvDrawer;

    Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);
        FragmentManager.enableDebugLogging(true);

        ButterKnife.bind(this);

        setupDrawerContent(nvDrawer);

        if (currentFragment == null) {

            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment fragment = null;
            Class fragmentClass;

            fragmentClass = MoveDexActivity.class;

            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }

    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        //Fragment fragment = null;

        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = MoveDexActivity.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = MoveDexActivity.class;
                break;
            default:
                fragmentClass = MoveDexActivity.class;
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


        fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
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
