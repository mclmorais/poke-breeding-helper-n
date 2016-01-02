package marcelo.breguenait.breedinghelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;


public class SelectPokemonFragment extends PopupDialogFragment {
    CardView mRevealView;
    private OnPokemonSelectedListener updaterCallback;
    private EditText editTextFilter;
    private SelectPokemonAdapter interfaceSelectorAdapter;

    private FeedDataSelectPokemon feederCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {

            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                updaterCallback = (OnPokemonSelectedListener) activity;
            else
                updaterCallback = (OnPokemonSelectedListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPokemonSelectedListener");
        }

        try {

            Fragment targetFragment = getTargetFragment();
            if (targetFragment == null)
                feederCallback = (FeedDataSelectPokemon) activity;
            else
                feederCallback = (FeedDataSelectPokemon) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(getTargetFragment().toString()
                    + " must implement FeedDataSelectPokemon");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        //setRetainInstance(true);

        // create ContextThemeWrapper from the original Activity Context with the custom theme
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.AppTheme);

        // clone the inflater using the ContextThemeWrapper
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_select_pokemon, container, false);

        mRevealView = (CardView) view.findViewById(R.id.revealable);

        interfaceSelectorAdapter = new SelectPokemonAdapter(
                getActivity().getApplicationContext(),
                feederCallback.getPokemonIds(),
                feederCallback.getPokemonNames());

        interfaceSelectorAdapter.setCompatiblePokemonList(feederCallback.getCompatiblePokemonList()); //TODO: ver se nao da pra fazer essa call somente se o fragment for pedir isso
        interfaceSelectorAdapter.setBasicPokemonList(feederCallback.getBasicPokemonList());
        interfaceSelectorAdapter.setPokemonFamilyList(feederCallback.getPokemonFamilyList());

        GridView gridViewSelector = (GridView) view.findViewById(R.id.gridViewSelectPokemon);
        gridViewSelector.setAdapter(interfaceSelectorAdapter);
        gridViewSelector.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int p = (int) interfaceSelectorAdapter.getItem(i);
                updaterCallback.onPokemonSelected(p);
                closeFragment();
            }
        });

        editTextFilter = (EditText) view.findViewById(R.id.editTextSelectPokemon);
        editTextFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                interfaceSelectorAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CheckBox checkBoxCompatible = (CheckBox) view.findViewById(R.id.checkBoxSelectPokemonCompatible);


        if (getArguments().getBoolean("showOnlyCompatible", false)) {
            checkBoxCompatible.setChecked(true);
            checkBoxCompatible.setEnabled(false);
            interfaceSelectorAdapter.showOnlyCompatible(true);
        }

        if (updaterCallback.showOnlyBasic()) {
            interfaceSelectorAdapter.setShowOnlyBasic(updaterCallback.showOnlyBasic());
            checkBoxCompatible.setChecked(true);
            checkBoxCompatible.setText("Basic Pokémon"); //TODO: mudar pra sistema
        }

        checkBoxCompatible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                interfaceSelectorAdapter.showOnlyCompatible(b);
                interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());
            }
        });


        interfaceSelectorAdapter.getFilter().filter(editTextFilter.getText());


        Button buttonCancel = (Button) view.findViewById(R.id.fragmentSelectPokemonButtonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFragment();
            }
        });


        setDialogPosition();

        boolean showEggGroupFilter = updaterCallback.showEggGroupFilter();

        checkBoxCompatible.setEnabled(showEggGroupFilter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        int margin = dpToPx(64);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(screenWidth - margin, screenHeight - margin);
        }
        //  reveal();
    }

    void reveal() {

        int cx = getArguments().getInt("x");
        int cy = getArguments().getInt("y");

        int radius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

        SupportAnimator animator =
                ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(400);
        mRevealView.setVisibility(View.VISIBLE);
        animator.start();

//        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, 0, radius);
//        anim.setInterpolator(new AccelerateDecelerateInterpolator());
//        anim.setDuration(500);
//        anim.start();

    }


    //BreedingFragment baseActivity;
    public interface OnPokemonSelectedListener {
        void onPokemonSelected(int id);

        boolean showEggGroupFilter();

        boolean showOnlyBasic();

    }

    public interface FeedDataSelectPokemon {

        ArrayList<Integer> getPokemonIds();

        ArrayList<String> getPokemonNames();

        ArrayList<Integer> getCompatiblePokemonList();

        ArrayList<Integer> getPokemonFamilyList();

        ArrayList<Integer> getBasicPokemonList();

    }

    public static class SelectPokemonAdapter extends BaseAdapter implements Filterable {

        private static final int DITTO_ID = 132;

        private final LayoutInflater inflater;

        private boolean addDittoLater = false;

        private boolean showOnlyCompatible = false;
        private boolean showOnlyBasic = false;
        private ArrayList<Integer> compatiblePokemonList;
        private ArrayList<Integer> pokemonFamilyList;
        private ArrayList<Integer> basicPokemonList;

        private ArrayList<Integer> pokemonIds;
        private ArrayList<String> pokemonNames;

        private ArrayList<Integer> filteredPokemonIds;

        SelectPokemonAdapter(Context mContext, ArrayList<Integer> pokemonIds, ArrayList<String> pokemonNames) {

            compatiblePokemonList = new ArrayList<>();

            this.pokemonIds = pokemonIds;
            this.filteredPokemonIds = pokemonIds;
            this.pokemonNames = pokemonNames;

            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        void showOnlyCompatible(boolean b) {
            showOnlyCompatible = b;
        }

        void setShowOnlyBasic(boolean b) {
            showOnlyBasic = b;
        }


        public void setCompatiblePokemonList(ArrayList<Integer> compatiblePokemonList) {
            this.compatiblePokemonList = compatiblePokemonList;
        }

        public void setBasicPokemonList(ArrayList<Integer> basicPokemonList) {
            this.basicPokemonList = basicPokemonList;
        }

        public void setPokemonFamilyList(ArrayList<Integer> pokemonFamilyList) {
            this.pokemonFamilyList = pokemonFamilyList;
        }

        @Override
        public int getCount() {
            return filteredPokemonIds.size();
        }

        @Override
        public Object getItem(int position) {
            return filteredPokemonIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View pokemonDynamicLayout = convertView;
            LayoutHolder holder;

            if (convertView == null) {
                //If this view is new (instead of recycled)
                pokemonDynamicLayout = inflater.inflate(R.layout.dynamic_view_layout_pokemon_selector, parent, false);

                holder = new LayoutHolder();
                holder.icon = (ImageView) pokemonDynamicLayout.findViewById(R.id.imageViewIcon);
                holder.id = (TextView) pokemonDynamicLayout.findViewById(R.id.textViewId);
                pokemonDynamicLayout.setTag(holder);
            } else {
                holder = (LayoutHolder) convertView.getTag();
            }

            int pokemonId = filteredPokemonIds.get(position);

            holder.id.setText(String.format("%03d", pokemonId));
            holder.icon.setBackground(CachedPokemonIcons.getInstance().getIcon(pokemonId));

            return pokemonDynamicLayout;

        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    filteredPokemonIds = (ArrayList<Integer>) results.values;
                    if (filteredPokemonIds == null) filteredPokemonIds = new ArrayList<>();
                    notifyDataSetChanged();
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {

                    FilterResults results = new FilterResults();
                    ArrayList<Integer> filteredArray = new ArrayList<>();

                    int familyPosition = 0; //Used so that the family is not added in reverse order

                    constraint = constraint.toString().toLowerCase();

                    for (int i = 0; i < pokemonIds.size(); i++) {

                        int currentId = pokemonIds.get(i);

                        if (currentId <= 0) continue; //TODO: ver se precisa tambem

                        /*If only compatible pokemons should be shown, ignores pokemons that don't have
                        * at least one egg group compatible with the current goal pokemon.*/
                        if (showOnlyCompatible) {
                            if (!compatiblePokemonList.contains(currentId))
                                continue;
                        }

                        if (showOnlyBasic) {
                            if (!basicPokemonList.contains(currentId))
                                continue;
                        }


                        String nameToBeCompared = pokemonNames.get(i);


                        //IF CONSTRAINT IS EMPTY, ADDS ALL
                        if (nameToBeCompared.toLowerCase().contains(constraint.toString())) {

                            if (currentId == DITTO_ID) {
                                addDittoLater = true;
                                continue;
                            }

                            if (showOnlyCompatible && pokemonFamilyList.contains(currentId)) {
                                filteredArray.add(familyPosition, currentId);
                                familyPosition++;
                            } else
                                filteredArray.add(currentId);
                        }
                    }

                    if (showOnlyCompatible && addDittoLater)
                        filteredArray.add(0, DITTO_ID);


                    results.count = filteredArray.size();
                    results.values = filteredArray;

                    return results;
                }
            };
        }

        class LayoutHolder {
            ImageView icon;
            TextView id;
        }
    }
}
