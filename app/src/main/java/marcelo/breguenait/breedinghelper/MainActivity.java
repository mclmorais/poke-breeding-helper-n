package marcelo.breguenait.breedinghelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @Bind(R.id.nvView)
    NavigationView nvDrawer;
    MenuItem lastMenuItem = null;
    MenuItem currentMenuItem;


    Fragment currentFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Lifecycle", "MainActivity - onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        ButterKnife.bind(this);

        CircleImageView portrait = (CircleImageView) nvDrawer.getHeaderView(0).findViewById(R.id.breeder_portrait);

        Random random = new java.util.Random();

        if (random.nextBoolean())
            portrait.setImageResource(R.drawable.portrait_breeder_female_whitebg);
        else
            portrait.setImageResource(R.drawable.portrait_breeder_male_whitebg);

        setupDrawerContent(nvDrawer);

        currentMenuItem = nvDrawer.getMenu().getItem(0);
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
            lastMenuItem = nvDrawer.getMenu().getItem(0);
            lastMenuItem.setChecked(true);
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        }
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPref.getBoolean("hasSeenDrawer", false)) {
            mDrawer.openDrawer(nvDrawer);
            sharedPref.edit().putBoolean("hasSeenDrawer", true).commit();
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
        mDrawer.setDrawerListener(null);

        Class fragmentClass;
        switch (menuItem.getItemId()) {
            case R.id.nav_first_fragment:
                fragmentClass = MoveDexFragment.class;
                break;
            case R.id.nav_second_fragment:
                fragmentClass = BreedingFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsHolderFragment.class;
                break;
            case R.id.nav_issue_report:
                sendBugReport();
                lastMenuItem.setChecked(true);
                return;
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

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction().replace(R.id.flContent, currentFragment).commit();

        lastMenuItem = currentMenuItem;

        currentMenuItem = menuItem;

        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
    }

    @Override
    public void onBackPressed() {

        if (currentFragment instanceof MoveDexFragment)
            selectDrawerItem(nvDrawer.getMenu().getItem(0));
        else if (currentFragment instanceof SettingsHolderFragment && lastMenuItem != null)
            selectDrawerItem(lastMenuItem);
        else
            super.onBackPressed();
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //drawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public DrawerLayout getDrawer() {
        return mDrawer;
    }

    private void sendBugReport() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"marcelofernandesmorais+bhbug@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "[Breeding Helper Issue]");

        String body = "Android version: " + Build.VERSION.RELEASE + " (" + Integer.toString(Build.VERSION.SDK_INT) + ") " + Build.PRODUCT + System.getProperty("line.separator");
        body += "Phone Model: " + Build.BRAND + " " + Build.MODEL + System.getProperty("line.separator");
        body += "Bug Description: ";

        i.putExtra(Intent.EXTRA_TEXT, body);
        try {
            startActivity(Intent.createChooser(i, "Please select an e-mail client to send the report"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no e-mail clients installed.", Toast.LENGTH_SHORT).show();
        }

    }


}


