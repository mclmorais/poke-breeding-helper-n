package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.SwitchCompat;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import breedingmanager.NatureManager;
import databasemanager.DatabaseConstants;
import marcelo.breguenait.breedinghelper.databinding.FragmentGoalPokemonBinding;


public class GoalPokemonFragment extends Fragment implements SelectPokemonFragment.OnPokemonSelectedListener,
        SelectPokemonFragment.FeedDataSelectPokemon {

    private FragmentGoalPokemonBinding binding;
    ArrayList<NatureManager.NatureVerbose> natureVerboses;
    ArrayList<InterfaceAbility> interfaceAbilities;
    private FeedDataGoalIVs feederCallback;
    private UpdateGoal updaterCallback;
    private final Spinner.OnItemSelectedListener onSpinnerItemSelectedHandler = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent == binding.spinnerGoalIVsNatures) {
                if (binding.spinnerGoalIVsNatures.getTag() != null && !binding.spinnerGoalIVsNatures.getTag().equals(position)) {
                    binding.spinnerGoalIVsNatures.setTag(-1);
                    updateGoalNature();
                }
            } else if (parent == binding.spinnerGoalIVsAbilities) {
                if (binding.spinnerGoalIVsAbilities.getTag() != null && !binding.spinnerGoalIVsAbilities.getTag().equals(position)) {
                    binding.spinnerGoalIVsAbilities.setTag(-1);
                    updateGoalAbility();
                }
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    private final CompoundButton.OnCheckedChangeListener onCheckBoxCheckHandler = (buttonView, isChecked) -> {
        if (buttonView == binding.checkBoxGoalIVsActivateNatures) {
            updaterCallback.updateNatureStatus(isChecked);
            String s;
            if (isChecked)
                s = requireActivity().getString(R.string.message_nature_considered);
            else
                s = requireActivity().getString(R.string.message_nature_ignored);
            Toast.makeText(requireActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        } else if (buttonView == binding.checkBoxGoalIVsActivateAbilities) {
            updaterCallback.updateAbilityStatus(isChecked);
            String s;
            if (isChecked)
                s = requireActivity().getString(R.string.message_ability_considered);
            else
                s = requireActivity().getString(R.string.message_ability_ignored);

            Toast.makeText(requireActivity().getApplicationContext(), s, Toast.LENGTH_SHORT).show();
        }

        for (CheckBox goalIVsCheckBox : getGoalIVs()) {
            if (buttonView == goalIVsCheckBox) {
                updateGoalIVs();
                return;
            }
        }
    };

    private CheckBox[] getGoalIVs(){
        return new CheckBox[]{binding.checkBoxGoalHP, binding.checkBoxGoalATK, binding.checkBoxGoalDEF, binding.checkBoxGoalSATK, binding.checkBoxGoalSDEF, binding.checkBoxGoalSPD};
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) return null;

        binding = FragmentGoalPokemonBinding.inflate(inflater, container, false);

        InterfaceGoalPokemon interfaceGoalPokemon = feederCallback.getInterfaceGoalPokemon();

        binding.buttonSelector.setOnClickListener(this::openSelectPokemonFragment);

        for (int i = 0; i < getGoalIVs().length; i++) {
            removeRippleEffectFromCheckBox(getGoalIVs()[i]);
            getGoalIVs()[i].setChecked(interfaceGoalPokemon.getIVs()[i] == 1);
            getGoalIVs()[i].setOnCheckedChangeListener(onCheckBoxCheckHandler);
        }

        natureVerboses = feederCallback.getInterfaceNatures();
        binding.spinnerGoalIVsNatures.setAdapter(new NatureSpinnerAdapter(natureVerboses, getContext()));
        binding.spinnerGoalIVsNatures.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        binding.spinnerGoalIVsAbilities.setOnItemSelectedListener(onSpinnerItemSelectedHandler);

        binding.checkBoxGoalIVsActivateNatures.setChecked(feederCallback.getConsiderNatureStatus());
        binding.checkBoxGoalIVsActivateNatures.setOnCheckedChangeListener(onCheckBoxCheckHandler);

        binding.checkBoxGoalIVsActivateAbilities.setChecked(feederCallback.getConsiderAbilityStatus());
        binding.checkBoxGoalIVsActivateAbilities.setOnCheckedChangeListener(onCheckBoxCheckHandler);

        feedInterface(interfaceGoalPokemon);

        return binding.getRoot();
    }

    private void updateGoalNature() {
        NatureManager.NatureVerbose natureVerbose = (NatureManager.NatureVerbose) binding.spinnerGoalIVsNatures.getSelectedItem();
        updaterCallback.updateGoalNature(natureVerbose.id);
    }

    private void updateGoalAbility() {

        int spinnerPosition = binding.spinnerGoalIVsAbilities.getSelectedItemPosition();

        int abilitySlot = interfaceAbilities.get(spinnerPosition).abilitySlot;

        updaterCallback.updateGoalAbilitySlot(abilitySlot);

    }

    private void updateGoalIVs() {

        int[] IVs = new int[6];
        for (int i = 0; i < 6; i++) {
            IVs[i] = getGoalIVs()[i].isChecked() ? 1 : 0;
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

        if (!name.equals(""))
            binding.textViewPokemonName.setText(name);
    }

    private void feedDisplayedIcon(int id) {

        if (DatabaseConstants.pokemonIdIsValid(id)) { //TODO: fazer 0 < x < limite
            String iconId = "pkmn_big_" + String.format("%03d", id);
            binding.imageViewSelectedPokemonIcon.setImageResource(getResources().getIdentifier(iconId, "drawable", requireActivity().getPackageName()));
        }
    }

    private void feedNatureSpinnerSelection(int natureId) {

        for (int i = 0; i < natureVerboses.size(); i++) {

            if (natureVerboses.get(i).id == natureId) {
                binding.spinnerGoalIVsNatures.setTag(i);
                binding.spinnerGoalIVsNatures.setSelection(i);
                return;
            }

        }
        Log.d("GoalFragment", "Received a pokemon with invalid nature");
    }

    private void feedAbilitySpinner() {
        if (binding.spinnerGoalIVsAbilities == null) return;

        LinkedHashMap<Integer, String> abilities = feederCallback.getListOfGoalAbilities();

        interfaceAbilities = new ArrayList<>();

        for (HashMap.Entry<Integer, String> entry : abilities.entrySet())
            interfaceAbilities.add(new InterfaceAbility(entry.getValue(), entry.getKey()));

        binding.spinnerGoalIVsAbilities.setAdapter(new AbilitySpinnerAdapter(interfaceAbilities, getContext()));
    }

    private void feedAbilitySpinnerSelection(int abilitySlot) {

        if (DatabaseConstants.abilitySlotIsValid(abilitySlot)) {
            int position = -1;
            for (int i = 0; i < interfaceAbilities.size(); i++) {
                if (interfaceAbilities.get(i).abilitySlot == abilitySlot) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                binding.spinnerGoalIVsAbilities.setTag(position);
                binding.spinnerGoalIVsAbilities.setSelection(position);
            } else {
                Log.d("GOAL", "AbilitySlot " + abilitySlot +
                        " wasn't found in InterfaceAbilities.");
            }
        }
    }

    private void openSelectPokemonFragment(View view) {
        FragmentManager fm = getParentFragmentManager();
        SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
        Bundle b = addPositionAsArguments(view);
        selectPokemonFragment.setArguments(b);
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
        int[] callerViewPosition = new int[2];
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

        ArrayList<NatureManager.NatureVerbose> getInterfaceNatures();

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

        final int[] IVs;
        final int pokemonId;
        final String pokemonName;
        final int natureId;
        final int abilitySlot;

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
        final ArrayList<InterfaceAbility> interfaceAbilities;
        final LayoutInflater inflater;
        final DisplayMetrics metrics;
        final Context context;

        public AbilitySpinnerAdapter(ArrayList<InterfaceAbility> interfaceAbilities, Context context) {
            this.interfaceAbilities = interfaceAbilities;
            if (interfaceAbilities.size() == 0) {
                interfaceAbilities.add(new InterfaceAbility("No Ability", -1));
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
                holder.viewAbilityName = natureView.findViewById(R.id.dynAbility_name);
                holder.viewAbilitySlot = natureView.findViewById(R.id.dynAbility_slot);

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
                holder.viewAbilityName = natureView.findViewById(R.id.dynAbility_name);
                holder.viewAbilitySlot = natureView.findViewById(R.id.dynAbility_slot);

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

        public int dpToPx(@SuppressWarnings("SameParameterValue") float valueInDp) {
            return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        }

        class LayoutHolder {
            View layout;
            TextView viewAbilityName;
            TextView viewAbilitySlot;
        }
    }

}
