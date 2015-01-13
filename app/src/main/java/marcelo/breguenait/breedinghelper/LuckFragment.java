package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link marcelo.breguenait.breedinghelper.LuckFragment.TemporaryLuckInterface} interface
 * to handle interaction events.
 */
public class LuckFragment extends Fragment {

    TextView luck1, luck2;

    private TemporaryLuckInterface mListener;

    public LuckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_luck, container, false);

        luck1 = (TextView) v.findViewById(R.id.textViewLuck1);
        luck2 = (TextView) v.findViewById(R.id.textViewLuck2);

        return v;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mListener = (TemporaryLuckInterface) activity;
            else
                mListener = (TemporaryLuckInterface) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    void updateChance(int poke1, int poke2, double chance) {
        luck1.setText(String.valueOf(poke1+1) + " + " + String.valueOf(poke2+1) + " = " + String.valueOf(chance));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface TemporaryLuckInterface {
        // TODO: Update argument type and name
    }

}
