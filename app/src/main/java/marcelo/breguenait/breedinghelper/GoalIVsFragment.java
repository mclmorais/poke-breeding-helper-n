package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class GoalIVsFragment extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener{

    interface OnGoalUpdate {
        void updateGoal(PokemonInfo p);
    }

    private OnGoalUpdate mCallback;

    private final CheckBox[] goalIVs = new CheckBox[6];
    private View buttonPokemonSelector;
    private ImageView selectedIcon;
    private TextView selectedName;

    private int selectedPokemonId = 0;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            Fragment targetFragment = getTargetFragment();
            if(targetFragment == null)
                mCallback = (OnGoalUpdate) activity;
            else
                mCallback = (OnGoalUpdate) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGoalUpdate!");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_goal_ivs, container, false);

        goalIVs[0] = (CheckBox) view.findViewById(R.id.checkBoxGoalHP);
        goalIVs[1] = (CheckBox) view.findViewById(R.id.checkBoxGoalATK);
        goalIVs[2] = (CheckBox) view.findViewById(R.id.checkBoxGoalDEF);
        goalIVs[3] = (CheckBox) view.findViewById(R.id.checkBoxGoalSATK);
        goalIVs[4] = (CheckBox) view.findViewById(R.id.checkBoxGoalSDEF);
        goalIVs[5] = (CheckBox) view.findViewById(R.id.checkBoxGoalSPD);

        for(int i = 0; i < 6; i ++) {
            goalIVs[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.updateGoal(updateGoalPokemon());
                }
            });

        }

        buttonPokemonSelector = view.findViewById(R.id.frameLayoutPokemonSelectorButton);

        buttonPokemonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPokemonFragment(v);
            }
        });

        selectedIcon = (ImageView) view.findViewById(R.id.imageViewSelectedPokemonIcon);

        selectedName = (TextView) view.findViewById(R.id.textViewPokemonName);

        return view;
    }

    void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.setTargetFragment(this,0);
        selectPokemonFragment.show(fm,"");
    }

    Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x",callerViewPosition[0]);
        b.putInt("y",callerViewPosition[1]);
        return b;
    }

    void refreshGoal(PokemonInfo goal) {
        if(goal == null) return;
        if(goal.id <= 0) return;
        selectedPokemonId = goal.id;
        updateInterfacePokemon(goal.id);
        for(int i = 0; i < 6; i++) {
            goalIVs[i].setChecked(goal.IVs[i] == 1);
        }

    }

    PokemonInfo updateGoalPokemon() {
        int[] IVs = new int[6];
        for(int i = 0; i < 6; i++) {
            IVs[i] = goalIVs[i].isChecked()?1:0;
        }

        return new PokemonInfo.Builder()
                .id(selectedPokemonId)
                .gender(Gender.MALE) //TODO: Depois fazer ele poder escolher?
                .IVs(IVs)
                .build();
        //TODO: fazer nao ficar recriando toda vez
    }

    @Override
    public void onPokemonSelected(int id) {
        selectedPokemonId = id;
        mCallback.updateGoal(updateGoalPokemon());
        updateInterfacePokemon(id);
    }

    void updateInterfacePokemon(int id) {
        selectedPokemonId = id;
        String name = PokemonData.getInstance().getName(id);
        selectedName.setText(name);
        selectedIcon.setBackground(PokemonData.getInstance().getDrawableFromId(id).getConstantState().newDrawable());
    }

    @Override
    public boolean showEggGroupFilter() {
        return false;
    }

    @Override
    public boolean showOnlyBasic() {
        return true;
    }


    @Override
    public PokemonInfo getGoal() {
        return updateGoalPokemon();
    }
}
