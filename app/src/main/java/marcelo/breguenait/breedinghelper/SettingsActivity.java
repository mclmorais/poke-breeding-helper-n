package marcelo.breguenait.breedinghelper;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Marcelo on 08/09/2014.
 */
public class SettingsActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_settings);


        setContentView(R.layout.activity_settings);
//        // Display the fragment as the main content.
//        getFragmentManager().beginTransaction()
//                .replace(android.R.id.content, new SettingsFragment())
//                .commit();

        if(findViewById(R.id.frameSettingsContainer) != null) {
            if (savedInstanceState != null) {
                return;
            }
            SettingsFragment f = new SettingsFragment();
            getFragmentManager().beginTransaction().add(R.id.frameSettingsContainer,f).commit();

        }


        Toolbar actionbar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(actionbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
        //actionbar.setTitle("Settings");
     //   actionbar.setNavigationIcon(getResources().getDrawable(R.drawable.iv_diamond_clear));
        actionbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


}
