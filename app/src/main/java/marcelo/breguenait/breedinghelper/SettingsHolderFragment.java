package marcelo.breguenait.breedinghelper;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class SettingsHolderFragment extends Fragment {


    Toolbar settingsHolderToolbar;

    public SettingsHolderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_holder, container, false);

        settingsHolderToolbar = v.findViewById(R.id.settingsHolderToolbar);

        settingsHolderToolbar.setNavigationOnClickListener(v1 -> requireActivity().onBackPressed());


        getChildFragmentManager().beginTransaction().add(
                R.id.settingsHolderContainer, new PreferencesFragment()).commit();

        return v;
    }

}
