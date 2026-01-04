package marcelo.breguenait.breedinghelper;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

import android.view.MenuItem;

import marcelo.breguenait.breedinghelper.databinding.ActivityInitialBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityInitialBinding binding;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInitialBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentManager = getSupportFragmentManager();

        // Set up toolbar from layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
            }
        }
        
        // Set up drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Set up navigation view
        binding.nvView.setNavigationItemSelectedListener(this);

        // Load default fragment (breeding fragment)
        if (savedInstanceState == null) {
            loadBreedingFragment();
        }
    }

    private void loadBreedingFragment() {
        Fragment fragment = new BreedingMainFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, fragment);
        transaction.commit();
    }

    private void loadMovedexFragment() {
        Fragment fragment = new MovedexFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, fragment);
        transaction.commit();
    }

    private void loadSettingsFragment() {
        Fragment fragment = new SettingsHolderFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flContent, fragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_second_fragment) {
            loadBreedingFragment();
        } else if (id == R.id.nav_first_fragment) {
            loadMovedexFragment();
        } else if (id == R.id.nav_settings) {
            loadSettingsFragment();
        } else if (id == R.id.nav_issue_report) {
            // Handle issue report - you can implement this later
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Fragment that loads the main breeding interface and acts as coordinator
    public static class BreedingMainFragment extends Fragment implements
            GoalPokemonFragment.FeedDataGoalIVs,
            GoalPokemonFragment.UpdateGoal,
            ChanceFragment.FeederLuckData,
            ChanceFragment.UpdateLuckInterface,
            StoredPokemonFragment.FeedDataStoredPokemon,
            StoredPokemonFragment.UpdateStoredPokemonList,
            SelectPokemonFragment.OnPokemonSelectedListener,
            SelectPokemonFragment.FeedDataSelectPokemon,
            ChanceOptionsFragment.OnLuckOptionsChange,
            CreatePokemonFragment.FeedDataCreatePokemon,
            CreatePokemonFragment.UpdateCreatePokemon {
        
        private breedingmanager.StorageManager storageManager;
        private breedingmanager.NatureManager natureManager;
        private breedingmanager.AbilityManager abilityManager;
        private int shinyOptions = 0;
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            storageManager = new breedingmanager.StorageManager();
            natureManager = new breedingmanager.NatureManager();
            abilityManager = new breedingmanager.AbilityManager();
        }
        
        @Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
            android.view.View view = inflater.inflate(R.layout.activity_main, container, false);
            
            // Load fragments into the containers and set callbacks
            if (getChildFragmentManager().findFragmentById(R.id.frameGoalIVsFragmentContainer) == null) {
                GoalPokemonFragment goalFragment = new GoalPokemonFragment();
                goalFragment.setCallbacks(this);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frameGoalIVsFragmentContainer, goalFragment);
                transaction.commit();
            }
            
            if (getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer) == null) {
                ChanceFragment luckFragment = new ChanceFragment();
                luckFragment.setCallbacks(this);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.frameLuckFragmentContainer, luckFragment);
                transaction.commit();
            }
            
            if (getChildFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer) == null) {
                StoredPokemonFragment storedFragment = new StoredPokemonFragment();
                storedFragment.setCallbacks(this);
                FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                transaction.replace(R.id.framePokemonListFragmentContainer, storedFragment);
                transaction.commit();
            }
            
            return view;
        }
        
        // Implement GoalPokemonFragment.FeedDataGoalIVs
        @Override
        public java.util.ArrayList<breedingmanager.NatureManager.NatureVerbose> getInterfaceNatures() {
            return natureManager.getInterfaceNatures();
        }
        
        @Override
        public java.util.LinkedHashMap<Integer, String> getListOfGoalAbilities() {
            int goalId = storageManager.getGoalId();
            if (!databasemanager.DatabaseConstants.pokemonIdIsValid(goalId)) {
                return new java.util.LinkedHashMap<>();
            }
            return abilityManager.getListOfAbilities(goalId);
        }
        
        @Override
        public java.util.ArrayList<Integer> getPokemonIds() {
            return storageManager.getPokemonIds();
        }
        
        @Override
        public java.util.ArrayList<String> getPokemonNames() {
            return storageManager.getPokemonNames();
        }
        
        @Override
        public GoalPokemonFragment.InterfaceGoalPokemon getInterfaceGoalPokemon() {
            breedingmanager.StoredPokemon goal = storageManager.getGoalObject();
            return new GoalPokemonFragment.InterfaceGoalPokemon(
                goal.getIVs(),
                goal.getPokemonId(),
                storageManager.getPokemonName(goal.getPokemonId()),
                goal.getNatureId(),
                goal.getAbilitySlot()
            );
        }
        
        @Override
        public java.util.ArrayList<Integer> getBasicPokemonList() {
            return storageManager.getBasicPokemonList();
        }
        
        @Override
        public boolean getConsiderNatureStatus() {
            return natureManager.getNatureModifier();
        }
        
        @Override
        public boolean getConsiderAbilityStatus() {
            return abilityManager.getAbilityModifier();
        }
        
        // Implement GoalPokemonFragment.UpdateGoal
        @Override
        public void updateGoalId(int id) {
            storageManager.setGoalId(id);
        }
        
        @Override
        public void updateGoalNature(int natureId) {
            storageManager.setGoalNature(natureId);
        }
        
        @Override
        public void updateGoalAbilitySlot(int abilitySlot) {
            storageManager.setGoalAbilitySlot(abilitySlot);
        }
        
        @Override
        public void updateGoalIVs(int[] IVs) {
            storageManager.setGoalIVs(IVs);
        }
        
        @Override
        public void updateAbilityStatus(boolean b) {
            abilityManager.setAbilityModifier(b);
        }
        
        @Override
        public void updateNatureStatus(boolean b) {
            natureManager.setNatureModifier(b);
        }
        
        // Implement ChanceFragment interfaces
        @Override
        public java.util.ArrayList<breedingmanager.ChancePokemonMatch> getChancesList() {
            // TODO: Implement calculateBestMatches in StorageManager or use existing method
            return new java.util.ArrayList<>();
        }
        
        @Override
        public ChanceFragment.InterfaceChancePokemon getInterfaceChancePokemon(java.util.UUID uuid) {
            return storageManager.getInterfaceChancePokemon(uuid);
        }
        
        @Override
        public int getInterfacePokemonPosition(java.util.UUID uuid) {
            // Find position in stored pokemon list
            java.util.ArrayList<breedingmanager.StoredPokemon> list = storageManager.getStoredPokemonObjects();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getUUID().equals(uuid)) {
                    return i;
                }
            }
            return -1;
        }
        
        @Override
        public boolean isDestinyKnotActive() {
            return storageManager.hasDestinyKnot();
        }
        
        @Override
        public void setDestinyKnot(boolean b) {
            storageManager.setDestinyKnot(b);
        }
        
        @Override
        public int loadShinyOptions() {
            return 0;
        }
        
        @Override
        public boolean careAboutNatures() {
            return natureManager.getNatureModifier();
        }
        
        @Override
        public void setEverstone(boolean b) {
            storageManager.setEverstone(b);
        }
        
        @Override
        public boolean updateEverstoneStatus() {
            return storageManager.hasEverstone();
        }
        
        // Implement StoredPokemonFragment.FeedDataStoredPokemon
        @Override
        public java.util.LinkedHashMap<Integer, String> getListOfAbilities(int pokemonId) {
            return abilityManager.getListOfAbilities(pokemonId);
        }
        
        @Override
        public java.util.ArrayList<StoredPokemonFragment.InterfaceStoredPokemon> getInterfaceStoredPokemonList() {
            return storageManager.getInterfaceStoredPokemonList();
        }
        
        @Override
        public int getGenderRate(int pokemonId) {
            return databasemanager.JsonDatabaseManager.getInstance().getGenderRate(pokemonId);
        }
        
        @Override
        public java.util.ArrayList<Integer> getCompatiblePokemonList() {
            return storageManager.getCompatiblePokemonList(storageManager.getGoalId());
        }
        
        @Override
        public java.util.ArrayList<Integer> getPokemonFamilyList() {
            return storageManager.getPokemonFamilyList(storageManager.getGoalId());
        }
        
        @Override
        public StoredPokemonViewerFragment.InterfaceViewerPokemon getInterfaceViewerPokemon(java.util.UUID uuid) {
            return storageManager.getInterfaceViewerPokemon(uuid);
        }
        
        @Override
        public String getPokemonName(int pokemonId) {
            return storageManager.getPokemonName(pokemonId);
        }
        
        @Override
        public InterfaceModifierPokemon getInterfaceModifierPokemon(java.util.UUID uuid) {
            return storageManager.getInterfaceModifierPokemon(uuid);
        }
        
        // Implement StoredPokemonFragment.UpdateStoredPokemonList
        @Override
        public void storePokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
            storageManager.storePokemon(pokemonId, genderId, IVs, natureId, abilitySlot);
        }
        
        @Override
        public void updateStoredPokemon(java.util.UUID uuid, int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
            storageManager.updateStoredPokemon(uuid, pokemonId, genderId, IVs, natureId, abilitySlot);
        }
        
        @Override
        public void removeStoredPokemon(java.util.UUID uuid) {
            storageManager.removePokemon(uuid);
        }
        
        // Implement SelectPokemonFragment.OnPokemonSelectedListener
        @Override
        public void onPokemonSelected(int id) {
            // Delegate to GoalPokemonFragment if it exists
            Fragment goalFragment = getChildFragmentManager().findFragmentById(R.id.frameGoalIVsFragmentContainer);
            if (goalFragment instanceof GoalPokemonFragment) {
                ((GoalPokemonFragment) goalFragment).onPokemonSelected(id);
            }
        }
        
        @Override
        public boolean showEggGroupFilter() {
            return false;
        }
        
        @Override
        public boolean showOnlyBasic() {
            return true;
        }
        
        // Note: FeedDataSelectPokemon methods are already implemented above
        // (getPokemonIds, getPokemonNames, getCompatiblePokemonList, getPokemonFamilyList, getBasicPokemonList)
        
        // Implement ChanceOptionsFragment.OnLuckOptionsChange
        @Override
        public int getShinyStatus() {
            // Delegate to ChanceFragment if it exists
            Fragment chanceFragment = getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
            if (chanceFragment instanceof ChanceFragment) {
                return ((ChanceFragment) chanceFragment).getShinyOptions();
            }
            return shinyOptions;
        }
        
        @Override
        public void changeShinyStatus(int bit, boolean add) {
            // Delegate to ChanceFragment if it exists
            Fragment chanceFragment = getChildFragmentManager().findFragmentById(R.id.frameLuckFragmentContainer);
            if (chanceFragment instanceof ChanceFragment) {
                ((ChanceFragment) chanceFragment).changeShinyStatus(bit, add);
            } else {
                // Store locally if fragment not available yet
                if (add) {
                    shinyOptions |= bit;
                } else {
                    shinyOptions &= ~bit;
                }
            }
        }
        
        // Implement CreatePokemonFragment.FeedDataCreatePokemon
        @Override
        public InterfaceModifierPokemon getLastInterfaceModifierPokemon() {
            // Delegate to StoredPokemonFragment if it exists
            Fragment storedFragment = getChildFragmentManager().findFragmentById(R.id.framePokemonListFragmentContainer);
            if (storedFragment instanceof StoredPokemonFragment) {
                return ((StoredPokemonFragment) storedFragment).getLastInterfaceModifierPokemon();
            }
            // Return default/empty pokemon
            return new InterfaceModifierPokemon(-1, -1, new int[]{0, 0, 0, 0, 0, 0}, -1, -1);
        }
        
        // Note: storePokemon is already implemented above for UpdateStoredPokemonList
        // and CreatePokemonFragment.UpdateCreatePokemon has the same signature, so it's shared
    }

    // Fragment for Move Dex
    public static class MovedexFragment extends Fragment implements
            SelectPokemonFragment.OnPokemonSelectedListener,
            SelectPokemonFragment.FeedDataSelectPokemon {
        
        private androidx.viewpager.widget.ViewPager viewPager;
        private com.google.android.material.tabs.TabLayout tabLayout;
        private androidx.appcompat.widget.Toolbar toolbar;
        private android.widget.ImageView floatingIcon;
        private com.google.android.material.floatingactionbutton.FloatingActionButton buttonSelectPokemon;
        private breedingmanager.MoveManager moveManager;
        private breedingmanager.StorageManager storageManager;
        private int selectedPokemonId = -1;
        
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            moveManager = new breedingmanager.MoveManager();
            storageManager = new breedingmanager.StorageManager();
        }
        
        @Override
        public android.view.View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, Bundle savedInstanceState) {
            android.view.View view = inflater.inflate(R.layout.activity_move_dex, container, false);
            
            // Find views
            viewPager = view.findViewById(R.id.moveDex_viewPager);
            tabLayout = view.findViewById(R.id.moveDex_tabLayout);
            toolbar = view.findViewById(R.id.moveDex_toolbar);
            floatingIcon = view.findViewById(R.id.moveDex_floatingIcon);
            buttonSelectPokemon = view.findViewById(R.id.moveDex_buttonSelectPokemon);
            
            // Set up toolbar
            if (getActivity() instanceof AppCompatActivity) {
                ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
                if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
                // Make toolbar button open drawer
                toolbar.setNavigationOnClickListener(v -> {
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null && activity.binding != null) {
                        if (activity.binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            activity.binding.drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            activity.binding.drawerLayout.openDrawer(GravityCompat.START);
                        }
                    }
                });
            }
            
            // Set up ViewPager with adapter
            MoveDexPagerAdapter adapter = new MoveDexPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(adapter);
            
            // Connect TabLayout to ViewPager
            tabLayout.setupWithViewPager(viewPager);
            
            // Set tab titles
            tabLayout.getTabAt(0).setText(R.string.label_level_up);
            tabLayout.getTabAt(1).setText(R.string.label_machine);
            tabLayout.getTabAt(2).setText(R.string.label_egg_moves);
            
            // Set up button click listener
            if (buttonSelectPokemon != null) {
                buttonSelectPokemon.setOnClickListener(this::openSelectPokemonFragment);
            }
            
            return view;
        }
        
        private void openSelectPokemonFragment(android.view.View view) {
            FragmentManager fm = getParentFragmentManager();
            SelectPokemonFragment selectPokemonFragment = new SelectPokemonFragment();
            Bundle b = addPositionAsArguments(view);
            selectPokemonFragment.setArguments(b);
            // Set this fragment as the target so SelectPokemonFragment can find it
            selectPokemonFragment.setTargetFragment(this, 0);
            selectPokemonFragment.show(fm, "SelectPokemonFragment");
        }
        
        private Bundle addPositionAsArguments(android.view.View v) {
            int[] callerViewPosition = new int[2];
            v.getLocationOnScreen(callerViewPosition);
            Bundle b = new Bundle();
            b.putInt("x", callerViewPosition[0]);
            b.putInt("y", callerViewPosition[1]);
            return b;
        }
        
        // Implement SelectPokemonFragment.OnPokemonSelectedListener
        @Override
        public void onPokemonSelected(int id) {
            selectedPokemonId = id;
            updateMoveLists(id);
            // Update floating icon with pokemon image
            if (floatingIcon != null && databasemanager.DatabaseConstants.pokemonIdIsValid(id)) {
                // TODO: Set pokemon icon image
            }
        }
        
        @Override
        public boolean showEggGroupFilter() {
            return false;
        }
        
        @Override
        public boolean showOnlyBasic() {
            return false; // Show all pokemon in move dex
        }
        
        // Implement SelectPokemonFragment.FeedDataSelectPokemon
        @Override
        public java.util.ArrayList<Integer> getPokemonIds() {
            return storageManager.getPokemonIds();
        }
        
        @Override
        public java.util.ArrayList<String> getPokemonNames() {
            return storageManager.getPokemonNames();
        }
        
        @Override
        public java.util.ArrayList<Integer> getCompatiblePokemonList() {
            return new java.util.ArrayList<>(); // Not used for move dex
        }
        
        @Override
        public java.util.ArrayList<Integer> getPokemonFamilyList() {
            return new java.util.ArrayList<>(); // Not used for move dex
        }
        
        @Override
        public java.util.ArrayList<Integer> getBasicPokemonList() {
            return storageManager.getBasicPokemonList();
        }
        
        private void updateMoveLists(int pokemonId) {
            if (!databasemanager.DatabaseConstants.pokemonIdIsValid(pokemonId)) return;
            
            databasemanager.JsonDatabaseManager database = databasemanager.JsonDatabaseManager.getInstance();
            int languageId = 9; // TODO: Make dynamic
            
            // Get level moves (methodId = 1)
            java.util.List<breedingmanager.MoveVerbose> levelMoves = 
                database.getPokemonMoves(pokemonId, 16, 1, languageId);
            
            // Get machine moves (methodId = 4)
            java.util.List<breedingmanager.MoveVerbose> machineMoves = 
                database.getPokemonMoves(pokemonId, 16, 4, languageId);
            
            // Get egg moves
            java.util.ArrayList<breedingmanager.MoveVerbose> eggMoves = moveManager.getEggMoves(pokemonId);
            
            // Update fragments - access them through the adapter
            MoveDexPagerAdapter adapter = (MoveDexPagerAdapter) viewPager.getAdapter();
            if (adapter != null) {
                // Get fragments from adapter
                Fragment levelFragment = adapter.getFragment(0);
                if (levelFragment instanceof LevelMovesListFragment) {
                    ((LevelMovesListFragment) levelFragment).switchData(new java.util.ArrayList<>(levelMoves));
                }
                
                Fragment machineFragment = adapter.getFragment(1);
                if (machineFragment instanceof MachineMovesListFragment) {
                    ((MachineMovesListFragment) machineFragment).switchData(new java.util.ArrayList<>(machineMoves));
                }
                
                Fragment eggFragment = adapter.getFragment(2);
                if (eggFragment instanceof EggMovesListFragment) {
                    ((EggMovesListFragment) eggFragment).switchData(eggMoves);
                }
            }
        }
        
        // ViewPager adapter for move dex tabs
        private class MoveDexPagerAdapter extends androidx.fragment.app.FragmentPagerAdapter {
            private final Fragment[] fragments = new Fragment[3];
            
            public MoveDexPagerAdapter(androidx.fragment.app.FragmentManager fm) {
                super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            }
            
            @Override
            public Fragment getItem(int position) {
                if (fragments[position] == null) {
                    switch (position) {
                        case 0:
                            fragments[position] = new LevelMovesListFragment();
                            break;
                        case 1:
                            fragments[position] = new MachineMovesListFragment();
                            break;
                        case 2:
                            fragments[position] = new EggMovesListFragment();
                            break;
                        default:
                            fragments[position] = new LevelMovesListFragment();
                            break;
                    }
                }
                return fragments[position];
            }
            
            @Override
            public int getCount() {
                return 3;
            }
            
            public Fragment getFragment(int position) {
                if (position >= 0 && position < fragments.length) {
                    return fragments[position];
                }
                return null;
            }
        }
    }
}

