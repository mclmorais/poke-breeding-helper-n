package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

//TODO: Make checks on the ability spinner when switching pokemon (right now OK - switches to the first)
// (put it always on the same slot or on the first if it doesn't have the previous slot)


public class GoalPokemonFragment extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon {

    @Bind({R.id.checkBoxGoalHP,
            R.id.checkBoxGoalATK,
            R.id.checkBoxGoalDEF,
            R.id.checkBoxGoalSATK,
            R.id.checkBoxGoalSDEF,
            R.id.checkBoxGoalSPD})
    CheckBox[] goalIVs;

    @Bind(R.id.frameLayoutPokemonSelectorButton)
    View buttonPokemonSelector;
    @Bind(R.id.imageViewSelectedPokemonIcon)
    ImageView selectedIcon;
    @Bind(R.id.spinnerGoalIVsNatures)
    Spinner spinnerNature;
    @Bind(R.id.spinnerGoalIVsAbilities)
    Spinner spinnerAbility;
    @Bind(R.id.checkBoxGoalIVsActivateNatures)
    SwitchCompat checkBoxActivateNatures;
    @Bind(R.id.checkBoxGoalIVsActivateAbilities)
    SwitchCompat checkBoxActivateAbilities;
    @Bind(R.id.textViewPokemonName)
    TextView selectedName;

    private FeedDataGoalIVs feederCallback;
    private UpdateGoal updaterCallback;

    ArrayList<InterfaceNature> interfaceNatures;
    ArrayList<InterfaceAbility> interfaceAbilities;

    private View.OnClickListener onClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v == buttonPokemonSelector) {
                openSelectPokemonFragment(v);
            }

        }
    };

    private Spinner.OnItemSelectedListener onSpinnerItemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent == spinnerNature) {
                if (spinnerNature.getTag() != position) {
                    spinnerNature.setTag(-1);
                    updateGoalNature();
                }
            } else if (parent == spinnerAbility) {
                if (spinnerAbility.getTag() != position) {
                    spinnerAbility.setTag(-1);
                    updateGoalAbility();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private CheckBox.OnCheckedChangeListener onCheckBoxCheckHandler = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == checkBoxActivateNatures) {
                updaterCallback.updateNatureStatus(isChecked);
                String s;
                if (isChecked)
                    s = getActivity().getString(R.string.message_nature_considered);
                else
                    s = getActivity().getString(R.string.message_nature_ignored);
                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            } else if (buttonView == checkBoxActivateAbilities) {
                updaterCallback.updateAbilityStatus(isChecked);
                String s;
                if (isChecked)
                    s = getActivity().getString(R.string.message_ability_considered);
                else
                    s = getActivity().getString(R.string.message_ability_ignored);

                Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }

            for (CheckBox goalIVsCheckBox : goalIVs) {
                if (buttonView == goalIVsCheckBox) {
                    updateGoalIVs();
                    return;
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Stops this from crashing by not having the callbacks initialized when
        // restarting the app from a termination. Why? It's a mystery to everybody!
        if (savedInstanceState != null) return null;

        View view = inflater.inflate(R.layout.fragment_goal_ivs, container, false);
        ButterKnife.bind(this, view);

        InterfaceGoalPokemon interfaceGoalPokemon = feederCallback.getInterfaceGoalPokemon();

        buttonPokemonSelector.setOnClickListener(onClickHandler);

        for (int i = 0; i < goalIVs.length; i++) {
            removeRippleEffectFromCheckBox(goalIVs[i]);
            goalIVs[i].setChecked(interfaceGoalPokemon.getIVs()[i] == 1);
            goalIVs[i].setOnCheckedChangeListener(onCheckBoxCheckHandler);
        }

        interfaceNatures = feederCallback.getInterfaceNatures();
        spinnerNature.setAdapter(new NatureSpinnerAdapter(interfaceNatures, getContext()));
        spinnerNature.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        spinnerAbility.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        checkBoxActivateNatures.setChecked(feederCallback.getConsiderNatureStatus());
        checkBoxActivateNatures.setOnCheckedChangeListener(onCheckBoxCheckHandler);

        checkBoxActivateAbilities.setChecked(feederCallback.getConsiderAbilityStatus());
        checkBoxActivateAbilities.setOnCheckedChangeListener(onCheckBoxCheckHandler);

        feedInterface(interfaceGoalPokemon);

        return view;
    }

    private void updateGoalNature() {
        InterfaceNature interfaceNature = (InterfaceNature) spinnerNature.getSelectedItem();
        updaterCallback.updateGoalNature(interfaceNature.id);
    }

    private void updateGoalAbility() {

        int spinnerPosition = spinnerAbility.getSelectedItemPosition();

        int abilitySlot = interfaceAbilities.get(spinnerPosition).abilitySlot;

        updaterCallback.updateGoalAbilitySlot(abilitySlot);

    }

    private void updateGoalIVs() {

        int[] IVs = new int[6];
        for (int i = 0; i < 6; i++) {
            IVs[i] = goalIVs[i].isChecked() ? 1 : 0;
        }

        updaterCallback.updateGoalIVs(IVs);
    }

    private void feedInterface(InterfaceGoalPokemon interfaceGoalPokemon) {
        feedDisplayedName(interfaceGoalPokemon.getPokemonName());
        feedDisplayedIcon(interfaceGoalPokemon.getPokemonId());

        feedNatureSpinnerSelection(interfaceGoalPokemon.getNatureId());

        feedAbilitySpinner();
        feedAbilitySpinnerSelection(interfaceGoalPokemon.getAbilitySlot());
    }

    private void feedDisplayedName(String name) {

        //If not initialized (""): doesn't update

        if (!name.equals(""))
            selectedName.setText(name);
    }

    private void feedDisplayedIcon(int id) {


        //If not initialized (-1): doesn't update

        if (id > 0) { //TODO: fazer 0 < x < limite
            String iconId = "pkmn_big_" + String.format("%03d", id);
            selectedIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", getActivity().getPackageName()));
        }
    }

    private void feedNatureSpinnerSelection(int natureId) {

        for (int i = 0; i < interfaceNatures.size(); i++) {

            if (interfaceNatures.get(i).id == natureId) {
                spinnerNature.setTag(i);
                spinnerNature.setSelection(i);
                return;
            }

        }
        Log.d("GoalFragment", "Received a pokemon with invalid nature");

    }

    private void feedAbilitySpinner() {
        if (spinnerAbility == null) return;

        LinkedHashMap<Integer, String> abilities = feederCallback.getListOfGoalAbilities();

        interfaceAbilities = new ArrayList<>();

        //Transforms the slot -> name HashMap into a InterfaceAbility to be used as a list
        for (HashMap.Entry<Integer, String> entry : abilities.entrySet())
            interfaceAbilities.add(new InterfaceAbility(entry.getValue(), entry.getKey()));

        spinnerAbility.setAdapter(new AbilitySpinnerAdapter(interfaceAbilities, getContext()));
    }

    private void feedAbilitySpinnerSelection(int abilitySlot) {

        //If the slot is valid
        if (abilitySlot > 0) {
            //Searches the interfaceAbilities for a one that corresponds to the goal slot
            int position = -1;
            for (int i = 0; i < interfaceAbilities.size(); i++) {
                if (interfaceAbilities.get(i).abilitySlot == abilitySlot) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                //If it has been found, sets the spinner to that position
                spinnerAbility.setTag(position);
                spinnerAbility.setSelection(position);
            } else {
                Log.d("GOAL", "AbilitySlot " + String.valueOf(abilitySlot) +
                        " wasn't found in InterfaceAbilities.");
            }
        }
    }

    private void openSelectPokemonFragment(View view) {
        FragmentManager fm = getFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
        selectPokemonFragment.setTargetFragment(this, 0);
        selectPokemonFragment.show(fm, "");
    }

    @Override
    public void onPokemonSelected(int id) {
        updaterCallback.updateGoalId(id);
        feedInterface(feederCallback.getInterfaceGoalPokemon());
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
    public ArrayList<Integer> getCompatiblePokemonList() {
        return new ArrayList<>(0); //Dummy call, this fragment doesn't need to feed this info
    }

    @Override
    public ArrayList<Integer> getPokemonFamilyList() {
        return new ArrayList<>(0); //Dummy call, this fragment doesn't need to feed this info
    }

    @Override
    public ArrayList<Integer> getBasicPokemonList() {
        return feederCallback.getBasicPokemonList();
    }

    @Override
    public ArrayList<Integer> getPokemonIds() {
        return feederCallback.getPokemonIds();
    }

    @Override
    public ArrayList<String> getPokemonNames() {
        return feederCallback.getPokemonNames();
    }

    private Bundle addPositionAsArguments(View v) {
        int callerViewPosition[] = new int[2];
        v.getLocationOnScreen(callerViewPosition);
        Bundle b = new Bundle();
        b.putInt("x", callerViewPosition[0]);
        b.putInt("y", callerViewPosition[1]);
        return b;
    }

    private void removeRippleEffectFromCheckBox(CheckBox checkBox) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawable = checkBox.getBackground();
            if (drawable instanceof RippleDrawable) {
                drawable = ((RippleDrawable) drawable).findDrawableByLayerId(0);
                checkBox.setBackground(drawable);
            }
        }
    }

    public void setCallbacks(Fragment callbacks) {
        this.feederCallback = (FeedDataGoalIVs) callbacks;
        this.updaterCallback = (UpdateGoal) callbacks;
    }

    interface OnGoalUpdate {


    }

    interface FeedDataGoalIVs {

        ArrayList<InterfaceNature> getInterfaceNatures();

        LinkedHashMap<Integer, String> getListOfGoalAbilities();

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        InterfaceGoalPokemon getInterfaceGoalPokemon();

        ArrayList<Integer> getBasicPokemonList();

        boolean getConsiderNatureStatus();

        boolean getConsiderAbilityStatus();
    }

    interface UpdateGoal {
        void updateGoalId(int id);

        void updateGoalNature(int natureId);

        void updateGoalAbilitySlot(int abilitySlot);

        void updateGoalIVs(int[] IVs);

        void updateAbilityStatus(boolean b);

        void updateNatureStatus(boolean b);
    }

    public static class InterfaceGoalPokemon {

        int[] IVs;
        int pokemonId;
        String pokemonName;
        int natureId;
        int abilitySlot;

        public InterfaceGoalPokemon(int[] IVs, int pokemonId, String pokemonName, int natureId, int abilitySlot) {
            this.IVs = IVs;
            this.pokemonId = pokemonId;
            this.pokemonName = pokemonName;
            this.natureId = natureId;
            this.abilitySlot = abilitySlot;
        }

        public int[] getIVs() {
            return IVs;
        }

        public int getPokemonId() {
            return pokemonId;
        }

        public String getPokemonName() {
            return pokemonName;
        }

        public int getNatureId() {
            return natureId;
        }

        public int getAbilitySlot() {
            return abilitySlot;
        }
    }

    public static class AbilitySpinnerAdapter extends BaseAdapter {
        ArrayList<InterfaceAbility> interfaceAbilities;
        LayoutInflater inflater;
        DisplayMetrics metrics;
        Context context;

        public AbilitySpinnerAdapter(ArrayList<InterfaceAbility> interfaceAbilities, Context context) {
            this.interfaceAbilities = interfaceAbilities;
            if(interfaceAbilities.size() == 0) {
                interfaceAbilities.add(new InterfaceAbility("No Ability", -1)); //TODO: fazer pegar do sistema
            }
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            metrics = context.getResources().getDisplayMetrics();
        }

        @Override
        public int getCount() {
            return interfaceAbilities.size();
        }

        @Override
        public Object getItem(int position) {
            return interfaceAbilities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View natureView = convertView;
            LayoutHolder holder;

            if (convertView == null) {
                natureView = inflater.inflate(R.layout.dynamic_layout_ability, parent, false);
                holder = new LayoutHolder();

                holder.layout = natureView.findViewById(R.id.dynAbility_layout);
                holder.viewAbilityName = (TextView) natureView.findViewById(R.id.dynAbility_name);
                holder.viewAbilitySlot = (TextView) natureView.findViewById(R.id.dynAbility_slot);

                natureView.setTag(holder);

            } else {
                holder = (LayoutHolder) natureView.getTag();
            }

            holder.viewAbilityName.setText(interfaceAbilities.get(position).abilityName);


            switch (interfaceAbilities.get(position).abilitySlot) {
                case 1:
                    holder.viewAbilitySlot.setText("1st Slot");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteBgDisabledHint));
                    break;
                case 2:
                    holder.viewAbilitySlot.setText("2nd Slot");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteBgDisabledHint));
                    break;
                case 3:
                    holder.viewAbilitySlot.setText("Hidden Slot");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    break;
                default:
                    holder.viewAbilitySlot.setText("");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteBgDisabledHint));
                    break;
            }


            return natureView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            View natureView = convertView;
            LayoutHolder holder;

            if (convertView == null) {
                natureView = inflater.inflate(R.layout.dynamic_layout_ability, parent, false);
                holder = new LayoutHolder();

                holder.layout = natureView.findViewById(R.id.dynAbility_layout);
                holder.viewAbilityName = (TextView) natureView.findViewById(R.id.dynAbility_name);
                holder.viewAbilitySlot = (TextView) natureView.findViewById(R.id.dynAbility_slot);

                natureView.setTag(holder);

            } else {
                holder = (LayoutHolder) natureView.getTag();
            }

            holder.viewAbilityName.setText(interfaceAbilities.get(position).abilityName);


            switch (interfaceAbilities.get(position).abilitySlot) {
                case 1:
                    holder.viewAbilitySlot.setText("1st Slot");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteBgDisabledHint));
                    break;
                case 2:
                    holder.viewAbilitySlot.setText("2nd Slot");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteBgDisabledHint));
                    break;
                case 3:
                    holder.viewAbilitySlot.setText("Hidden Slot");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                    break;
                default:
                    holder.viewAbilitySlot.setText("");
                    holder.viewAbilitySlot.setTextColor(ContextCompat.getColor(context, R.color.colorWhiteBgDisabledHint));
                    break;
            }


            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(40));
            holder.layout.setLayoutParams(layoutParams);
            return natureView;
        }

        public int dpToPx(float valueInDp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        }

        class LayoutHolder {
            View layout;
            TextView viewAbilityName;
            TextView viewAbilitySlot;
        }
    }

}

