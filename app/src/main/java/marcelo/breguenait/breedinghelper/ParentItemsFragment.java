package marcelo.breguenait.breedinghelper;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

public class ParentItemsFragment extends PopupDialogFragment{

    Gender parentGender;
    MainActivity baseActivity;

    RadioGroup radioGroupItems;
    CheckBox checkboxDitto;

    UpdateItem mCallback;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (UpdateItem) activity;
            else
                mCallback = (UpdateItem) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement UpdateItem");
        }
        baseActivity = ((MainActivity)getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        /*Gets which gender asked for this fragment*/
        String genderString = getArguments().getString("gender");
        try {
            if (genderString.equals("male"))
                parentGender = Gender.MALE;
            else if (genderString.equals("female"))
                parentGender = Gender.FEMALE;
            else
                throw new Exception("Unknown gender referred when opening parent options");
        }
        catch(Exception e) {
            closeFragment();
            Log.w("BreedingHelper",e.getMessage());
        }

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_parent_options, container, false);

        setListeners(view);
        setDialogPosition();
        //getItem();
        return view;
    }

    private void setListeners(View view){

        Button close = (Button) view.findViewById(R.id.buttonClose);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });

        radioGroupItems = (RadioGroup) view.findViewById(R.id.radiogroupItems);

        radioGroupItems.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_destiny_knot:
                        mCallback.updateItem(IvManager.item.DESTINY_KNOT);
                        break;
                    case R.id.radio_power_hp:
                        mCallback.updateItem(IvManager.item.POWER_HP);
                        break;
                    case R.id.radio_power_atk:
                        mCallback.updateItem(IvManager.item.POWER_ATK);
                        break;
                    case R.id.radio_power_def:
                        mCallback.updateItem(IvManager.item.POWER_DEF);
                        break;
                    case R.id.radio_power_satk:
                        mCallback.updateItem(IvManager.item.POWER_SATK);
                        break;
                    case R.id.radio_power_sdef:
                        mCallback.updateItem(IvManager.item.POWER_SDEF);
                        break;
                    case R.id.radio_power_spd:
                        mCallback.updateItem(IvManager.item.POWER_SPD);
                        break;
                    case R.id.radio_unequip:
                        mCallback.updateItem(IvManager.item.NO_ITEM);
                        break;
                }
            }
        });


    }

    void getItem() {

        IvManager.item it =  baseActivity.getItem(parentGender);
        if(it == IvManager.item.DESTINY_KNOT)
            radioGroupItems.check(R.id.radio_destiny_knot);
        else if (it == IvManager.item.POWER_HP)
            radioGroupItems.check(R.id.radio_power_hp);
        else if (it == IvManager.item.POWER_ATK)
            radioGroupItems.check(R.id.radio_power_atk);
        else if (it == IvManager.item.POWER_DEF)
            radioGroupItems.check(R.id.radio_power_def);
        else if (it == IvManager.item.POWER_SATK)
            radioGroupItems.check(R.id.radio_power_satk);
        else if (it == IvManager.item.POWER_SDEF)
            radioGroupItems.check(R.id.radio_power_sdef);
        else if (it == IvManager.item.POWER_SPD)
            radioGroupItems.check(R.id.radio_power_spd);
        else
            radioGroupItems.check(R.id.radio_unequip);
    }

    interface UpdateItem {
        void updateItem(IvManager.item item);
    }

}
