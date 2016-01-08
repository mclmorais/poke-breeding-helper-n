package marcelo.breguenait.breedinghelper;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsHolderFragment extends Fragment {


    Toolbar settingsHolderToolbar;

    public SettingsHolderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_settings_holder, container, false);

        settingsHolderToolbar = (Toolbar) v.findViewById(R.id.settingsHolderToolbar);

        settingsHolderToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        getChildFragmentManager().beginTransaction().add(
                R.id.settingsHolderContainer, new PreferencesFragment()).commit();

        return v;
    }

}
