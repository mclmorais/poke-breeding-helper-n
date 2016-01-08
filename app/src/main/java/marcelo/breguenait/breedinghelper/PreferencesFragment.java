package marcelo.breguenait.breedinghelper;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

public class PreferencesFragment extends PreferenceFragmentCompat {


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.fragment_settings);
    }


}