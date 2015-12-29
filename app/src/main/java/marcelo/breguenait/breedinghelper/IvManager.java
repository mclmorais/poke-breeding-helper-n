package marcelo.breguenait.breedinghelper;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

enum Item {
    NO_ITEM,
    DESTINY_KNOT,
    POWER_HP,
    POWER_ATK,
    POWER_DEF,
    POWER_SATK,
    POWER_SDEF,
    POWER_SPD
}

enum Nature {
    UNSET,
    ADAMANT,
    BASHFUL,
    BOLD,
    BRAVE,
    CALM,
    CAREFUL,
    DOCILE,
    GENTLE,
    HARDY,
    HASTY,
    IMPISH,
    JOLLY,
    LAX,
    LONELY,
    MILD,
    MODEST,
    NAIVE,
    NAUGHTY,
    QUIET,
    QUIRKY,
    RASH,
    RELAXED,
    SASSY,
    SERIOUS,
    TIMID
}

enum EggGroup {
    UNKNOWN,
    NONE,
    MONSTER,
    HUMAN_LIKE,
    WATER_1,
    WATER_2,
    WATER_3,
    BUG,
    MINERAL,
    FLYING,
    AMORPHOUS,
    FIELD,
    FAIRY,
    DITTO,
    GRASS,
    DRAGON,
    UNDISCOVERED,
    GENDER_UNKNOWN
}

enum Gender {
    MALE,
    FEMALE,
    GENDERLESS,
    DITTO
}

enum GenderRestriction {
    NONE,
    MALE_ONLY,
    FEMALE_ONLY,
    GENDERLESS,
    DITTO
}

enum AbilitySlot {
    FIRST, SECOND, HIDDEN, ERROR
}

class EquippedItems {
    private Item maleItem = Item.DESTINY_KNOT; //Temporário até eu pensar melhor o que vou fazer com esses itens
    private Item femaleItem = Item.NO_ITEM;

    public Item getMaleItem() {
        return maleItem;
    }

    public void setMaleItem(Item maleItem) {
        this.maleItem = maleItem;
    }

    public Item getFemaleItem() {
        return femaleItem;
    }

    public void setFemaleItem(Item femaleItem) {
        this.femaleItem = femaleItem;
    }

    public boolean destinyKnotIsEquipped() {
        return ((maleItem == Item.DESTINY_KNOT) || (femaleItem == Item.DESTINY_KNOT));
    }

    Item getPowerItem(int IV) {
        Item item;
        if (IV == 0) item = Item.POWER_HP;
        else if (IV == 1) item = Item.POWER_ATK;
        else if (IV == 2) item = Item.POWER_DEF;
        else if (IV == 3) item = Item.POWER_SATK;
        else if (IV == 4) item = Item.POWER_SDEF;
        else if (IV == 5) item = Item.POWER_SPD;
        else throw new IllegalArgumentException("Invalid IV number received.");

        return item;
    }


}

class PokemonInfo {
    /*Relevant variables for the future*/
    final int id;             //The national dex number of the pokemon
    final Gender gender;
    final int[] IVs;
    final Nature nature;
    final EggGroup eggGroup1;
    final EggGroup eggGroup2;
    final int ability;


    private PokemonInfo(Builder b) {
        this.id = b.id;
        this.gender = b.gender;
        this.IVs = b.IVs;
        this.nature = b.nature;
        this.eggGroup1 = b.eggGroup1;
        this.eggGroup2 = b.eggGroup2;
        this.ability = b.ability;
    }

    static class Builder {
        private int id = 0;          //The national dex number of the pokemon
        private Gender gender = Gender.MALE;
        private int[] IVs = {0, 0, 0, 0, 0, 0};
        private Nature nature = Nature.UNSET;
        private EggGroup eggGroup1 = EggGroup.UNKNOWN;
        private EggGroup eggGroup2 = EggGroup.UNKNOWN;
        private int ability;

        public Builder() {
            id = 0;
            gender = Gender.MALE;
            nature = Nature.UNSET;
        }

        public Builder id(int id) {
            this.id = id;

            this.eggGroup1 = PokemonData.getInstance().getFirstEggGroup(id);
            this.eggGroup2 = PokemonData.getInstance().getSecondEggGroup(id);

            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder IVs(int[] IVs) {
            this.IVs = Arrays.copyOf(IVs, IVs.length);
            return this;
        }

        public Builder nature(Nature nature) {
            this.nature = nature;
            return this;
        }

        public Builder ability(int a) {
            this.ability = a;
            return this;
        }

        PokemonInfo build() {
            return new PokemonInfo(this);
        }
    }
}

class ChanceData {
    PokemonInfo firstPokemon = null, secondPokemon = null;
    int firstPokemonNumber = 0;
    int secondPokemonNumber = 0;
    double chance = 0.0d;


    ChanceData(PokemonInfo firstPokemon, PokemonInfo secondPokemon, int firstPokemonNumber, int secondPokemonNumber, double chance) {
        this.firstPokemon = firstPokemon;
        this.secondPokemon = secondPokemon;
        this.firstPokemonNumber = firstPokemonNumber;
        this.secondPokemonNumber = secondPokemonNumber;
        this.chance = chance;
    }
}

public class IvManager {

    private final EquippedItems equippedItems = new EquippedItems();
    private final SparseArray<Integer> destinyKnotCombinations = new SparseArray<>(6);
    private final SparseArray<Integer> bareCombinations = new SparseArray<>(20);
    private boolean hasEverstone = true;
    private boolean considerNature = false;
    private boolean considerAbility = false;
    private List<PokemonInfo> storedPokemonList = new ArrayList<>();
    private List<ChanceData> bestCombinationsList = new ArrayList<>();
    private PokemonInfo goalPokemon = null;

    IvManager() {
        fillCombinations();
    }

    public boolean hasEverstone() {
        return hasEverstone;
    }

    public void setEverstone(boolean hasEverstone) {
        this.hasEverstone = hasEverstone;
        updateBestCombination();
    }

    boolean considerNature() {
        return considerNature;
    }

    void setConsiderNature(boolean b) {
        considerNature = b;
        updateBestCombination();
    }

    public boolean considerAbility() {
        return considerAbility;
    }

    public void setConsiderAbility(boolean considerAbility) {
        this.considerAbility = considerAbility;
        updateBestCombination();
    }

    public final List<ChanceData> getBestCombinations() {
        return bestCombinationsList;
    }

    public void setEmptyGoalPokemon() {

        int[] IVs = new int[6];
        this.goalPokemon = new PokemonInfo.Builder()
                .id(0)
                .gender(Gender.MALE)
                .ability(0)
                .nature(Nature.UNSET)
                .IVs(IVs)
                .build();

    }

    public PokemonInfo getGoalPokemon() {
        return goalPokemon;
    }

    public void setGoalPokemon(PokemonInfo goalPokemon) {
        this.goalPokemon = goalPokemon;
        if (goalPokemon != null)
            updateBestCombination();
    }

    public void storePokemon(PokemonInfo pokemon) {
        storedPokemonList.add(pokemon);
        updateBestCombination(); //FUCKING SIDE EFFECT!!!!!!!!!!!!!!!
    }

    public void editPokemon(PokemonInfo pokemon, int position) {
        storedPokemonList.remove(position);
        storedPokemonList.add(position, pokemon);
        updateBestCombination();
    }

    public void removePokemon(int position) {
        storedPokemonList.remove(position);
        updateBestCombination();
    }

    public Item getMaleItem() {
        return equippedItems.getMaleItem();
    }

    public void setMaleItem(Item i) {
        equippedItems.setMaleItem(i);
        updateBestCombination();
    }

    public List<PokemonInfo> getStoredPokemonList() {
        return storedPokemonList;
    }

    public void setStoredPokemonList(List<PokemonInfo> p) {
        storedPokemonList = p;
    }

    public PokemonInfo getStoredPokemon(int pos) {
        return storedPokemonList.get(pos);
    }

    public void updateBestCombination() {
        if (goalPokemon == null) {
            bestCombinationsList = null;
            return;
        }
        if (bestCombinationsList != null)
            bestCombinationsList.clear();

        List<ChanceData> chances = new ArrayList<>();

        //Runs through all possible different combinations of pokémon
        for (int i = 0; i < storedPokemonList.size(); i++) {
            PokemonInfo firstPokemon = storedPokemonList.get(i);
            if (firstPokemon.id <= 0) continue;

            for (int j = (i + 1); j < storedPokemonList.size(); j++) {
                PokemonInfo secondPokemon = storedPokemonList.get(j);
                if (secondPokemon.id <= 0) continue;

                if (checkCompatibility(firstPokemon, secondPokemon)) {
                    double chance = getChance(firstPokemon.IVs, secondPokemon.IVs, goalPokemon.IVs);
                    chance = checkNatureChance(firstPokemon.nature, secondPokemon.nature, chance);
                    chance = checkAbilityChance(firstPokemon, secondPokemon, chance);
                    chances.add(new ChanceData(storedPokemonList.get(i), storedPokemonList.get(j), i, j, chance));
                }
            }
        }

        class ChanceComparator implements Comparator<ChanceData> {
            @Override
            public int compare(ChanceData e1, ChanceData e2) {
                return Double.compare(e1.chance, e2.chance);
            }
        }

        if (!chances.isEmpty()) {
            Collections.sort(chances, new ChanceComparator());
            Collections.reverse(chances);

            for (int i = chances.size() - 1; i >= 0; i--) {
                if (Double.compare(chances.get(i).chance, 1e-5) < 0) {
                    chances.remove(i);
                }
            }

            bestCombinationsList = chances;
        }
    }

    private double checkNatureChance(Nature firstNature, Nature secondNature, double chance) {

        if (considerNature && goalPokemon.nature != Nature.UNSET) {
            if (hasEverstone) {
                if (firstNature != goalPokemon.nature && secondNature != goalPokemon.nature) {
                    chance *= (1.0 / 25.0);
                }
            } else chance *= (1.0 / 25.0);
        }

        return chance;
    }

    private double checkAbilityChance(PokemonInfo firstPokemon, PokemonInfo secondPokemon, double chance) {

        AbilitySlot goalSlot, firstPokemonSlot, secondPokemonSlot;

        if (considerAbility /*&& goalPokemon.ability > 0*/) {

            goalSlot = checkAbilitySlot(goalPokemon.ability, goalPokemon.id);
            firstPokemonSlot = checkAbilitySlot(firstPokemon.ability, firstPokemon.id);
            secondPokemonSlot = checkAbilitySlot(secondPokemon.ability, secondPokemon.id);


            if (firstPokemonSlot == goalSlot) {
                if (firstPokemon.gender == Gender.FEMALE) {
                    //If the first pokemon has the same nature and is female, chance is 80%
                    return chance * 0.8;
                } else if ((firstPokemon.gender == Gender.MALE || firstPokemon.gender == Gender.GENDERLESS)
                        && secondPokemon.gender == Gender.DITTO) {
                    //If the first pokemon is male/genderless, has the nature and is with a ditto, chance is 80%
                    return chance * 0.8;
                }
            }

            if (secondPokemonSlot == goalSlot) {
                if (secondPokemon.gender == Gender.FEMALE) {
                    //If the second pokemon has the nature and is female, chance is 80%
                    return chance * 0.8;
                } else if ((secondPokemon.gender == Gender.MALE || secondPokemon.gender == Gender.GENDERLESS)
                        && firstPokemon.gender == Gender.DITTO) {
                    //If the second pokemon has the nature and is male/genderless with a ditto, chance is 80%
                    return chance * 0.8;
                }
            }

            //If none of the appropriate conditions applied, chance is 20% for non-hidden or 0% for hidden
            if (goalSlot != AbilitySlot.HIDDEN) {
                return chance * 0.2;
            }

            return 0.0;

        } else {
            return chance;
        }
    }

    AbilitySlot checkAbilitySlot(int ability, int id) {
        if (ability == PokemonData.getInstance().getFirstAbilityId(id)) {
            return AbilitySlot.FIRST;
        } else if (ability == PokemonData.getInstance().getSecondAbilityId(id)) {
            return AbilitySlot.SECOND;
        } else if (ability == PokemonData.getInstance().getHiddenAbilityId(id)) {
            return AbilitySlot.HIDDEN;
        } else
            return AbilitySlot.ERROR;
    }

    /**
     * Checks if two given pokemons are a compatible pair when trying to breed the goal pokemon.
     * The function will look if the pair is either: <br>
     * 1. Female from the family + Male from egg group <br>
     * 2. Ditto + Pokemon from family
     *
     * @param firstPokemon  First pokemon to be compared
     * @param secondPokemon Second pokemon to be compared
     * @return True if they are one of the accepted pairs
     */
    private boolean checkCompatibility(PokemonInfo firstPokemon, PokemonInfo secondPokemon) {
        if (goalPokemon == null) return false;

        if (firstPokemon.gender == Gender.MALE && secondPokemon.gender == Gender.FEMALE) {
            boolean femaleCompatible = checkFamilyCompatibility(secondPokemon);
            boolean maleCompatible = checkEggGroupCompatibility(firstPokemon);

            return (maleCompatible && femaleCompatible);
        } else if (firstPokemon.gender == Gender.FEMALE && secondPokemon.gender == Gender.MALE) {
            boolean femaleCompatible = checkFamilyCompatibility(firstPokemon);
            boolean maleCompatible = checkEggGroupCompatibility(secondPokemon);

            return (maleCompatible && femaleCompatible);
        } else if (firstPokemon.gender == Gender.DITTO) {
            if (secondPokemon.gender == Gender.MALE ||
                    secondPokemon.gender == Gender.FEMALE ||
                    secondPokemon.gender == Gender.GENDERLESS) {
                return checkFamilyCompatibility(secondPokemon);
            }
        } else if (secondPokemon.gender == Gender.DITTO) {
            if (firstPokemon.gender == Gender.MALE ||
                    firstPokemon.gender == Gender.FEMALE ||
                    firstPokemon.gender == Gender.GENDERLESS) {
                return checkFamilyCompatibility(firstPokemon);
            }
        }

        return false;
    }

    /**
     * Checks if the given pokemon belongs to the same family as the goal pokemon (i.e. the same
     * evolutionary line or another close relation)
     *
     * @param potential The pokemon to be compared to the goal
     * @return True if it is compatible, False if it's not
     */
    private boolean checkFamilyCompatibility(PokemonInfo potential) {
        if (goalPokemon == null) throw new NullPointerException();

        int goalBreeds = PokemonData.getInstance().getBasicPokemon(goalPokemon.id);
        int potentialBreeds = PokemonData.getInstance().getBasicPokemon(potential.id);

        boolean breedsSamePokemon = false;

        if (potentialBreeds == goalBreeds)
            breedsSamePokemon = true;

        return breedsSamePokemon;
    }

    /**
     * Checks if the given pokemon belongs to the same egg group as the goal.
     *
     * @param potential The pokemon to be compared to the goal
     * @return True if it is compatible, false if it's not
     */
    private boolean checkEggGroupCompatibility(PokemonInfo potential) {
        if (goalPokemon == null) throw new NullPointerException();

        boolean valid = false;

        if (potential.eggGroup1 == goalPokemon.eggGroup1 || potential.eggGroup1 == goalPokemon.eggGroup2) {
            valid = true;
        }

        if (potential.eggGroup2 != EggGroup.NONE && potential.eggGroup2 != EggGroup.UNKNOWN) {
            if (potential.eggGroup2 == goalPokemon.eggGroup1 || potential.eggGroup2 == goalPokemon.eggGroup2) {
                valid = true;
            }
        }

        return valid;
    }


    private double getChance(int[] maleIVs, int[] femaleIVs, int[] goalIVs) {
        double chance = 0.0d;

        final double MAX = 1.0d;
        final double MIN = 0.0d;
        final double HALF = 0.5d;
        final double RANDOM = (1.0d / 32.0d);

        /*Selects which set of combinations will be used for the chance calculation*/
        SparseArray<Integer> possibleCombinations;
        if (equippedItems.destinyKnotIsEquipped())
            possibleCombinations = destinyKnotCombinations;
        else
            possibleCombinations = bareCombinations;

        /*Iterates through each available combination*/
        for (int currentCombination = 0; currentCombination < possibleCombinations.size(); currentCombination++) {
            double chanceCurrentCombination = 1.0d;

            /*Iterates through each IV for each of the combinations*/
            for (int currentIV = 0; currentIV < 6; currentIV++) {
                double chanceCurrentIV;

                /*If this IV is not wanted on the goal pokemon, it doesn't affect the chance of
                * getting it.*/
                if (goalIVs[currentIV] != 1) {
                    chanceCurrentIV = 1.0d;
                }
                /*Else if it is wanted*/
                else {
                    /*If the current IV is not one of the selected to be inherited from the parents,
                    * only the random chance of getting the maximum IV applies*/
                    if ((possibleCombinations.get(currentCombination) & (1 << currentIV)) == 0)
                        chanceCurrentIV = RANDOM;
                    /*Else if both parents have the current IV, the chance for this IV is maximum*/
                    else if (maleIVs[currentIV] == 1 && femaleIVs[currentIV] == 1)
                        chanceCurrentIV = MAX;
                    /*Else if only the male parent has the current IV*/
                    else if (maleIVs[currentIV] == 1 && femaleIVs[currentIV] == 0) {
                        /*If the male has the corresponding power item, the chance is maximum (will always get the good IV).
                        * If the female has the item instead, the chance is minimum (will always get the bad IV).
                        * If neither has the item, the chance of getting the good IV from the male is half.*/
                        if (equippedItems.getMaleItem() == equippedItems.getPowerItem(currentIV))
                            chanceCurrentIV = MAX;
                        else if (equippedItems.getFemaleItem() == equippedItems.getPowerItem(currentIV))
                            chanceCurrentIV = MIN;
                        else
                            chanceCurrentIV = HALF;
                    }
                    /*Else if only the female has the current IV*/
                    else if (femaleIVs[currentIV] == 1 && maleIVs[currentIV] == 0) {
                        /*If the female has the corresponding power item, the chance is maximum (will always get the good IV).
                        * If the male has the item instead, the chance is minimum (will always get the bad IV).
                        * If neither has the item, the chance of getting the good IV from the female is half.*/
                        if (equippedItems.getFemaleItem() == equippedItems.getPowerItem(currentIV))
                            chanceCurrentIV = MAX;
                        else if (equippedItems.getMaleItem() == equippedItems.getPowerItem(currentIV))
                            chanceCurrentIV = MIN;
                        else
                            chanceCurrentIV = HALF;
                    }
                    /*Else if neither parent has a good IV*/
                    else {
                        chanceCurrentIV = MIN;
                    }
                }
                /*After the current IV chance has been calculated,
                applies it to the current combination chance*/
                chanceCurrentCombination *= chanceCurrentIV;
            }
            /*After calculating all combinations, gets the total chance by calculating the mean*/
            chance += chanceCurrentCombination;
        }
        chance = chance / ((double) possibleCombinations.size());

        return chance;
    }

    private void fillCombinations() {
        /*When using the destiny knot, chance is calculated by getting
        * 5 out of the 6 possible IVs from the parents.
        * The total number of combinations is C(6,5) = 6.*/
        destinyKnotCombinations.put(0, Integer.parseInt("111110", 2));
        destinyKnotCombinations.put(1, Integer.parseInt("111101", 2));
        destinyKnotCombinations.put(2, Integer.parseInt("111011", 2));
        destinyKnotCombinations.put(3, Integer.parseInt("110111", 2));
        destinyKnotCombinations.put(4, Integer.parseInt("101111", 2));
        destinyKnotCombinations.put(5, Integer.parseInt("011111", 2));

        /*Without using the destiny knot, chance is calculated by getting
        * 3 out of 6 possible IVs from the parents.
        * The total number of combinations is C(6,3) = 20.*/
        bareCombinations.put(0, Integer.parseInt("111000", 2));
        bareCombinations.put(1, Integer.parseInt("110100", 2));
        bareCombinations.put(2, Integer.parseInt("110010", 2));
        bareCombinations.put(3, Integer.parseInt("110001", 2));
        bareCombinations.put(4, Integer.parseInt("101100", 2));
        bareCombinations.put(5, Integer.parseInt("101010", 2));
        bareCombinations.put(6, Integer.parseInt("101001", 2));
        bareCombinations.put(7, Integer.parseInt("100110", 2));
        bareCombinations.put(8, Integer.parseInt("100101", 2));
        bareCombinations.put(9, Integer.parseInt("100011", 2));
        bareCombinations.put(10, Integer.parseInt("011100", 2));
        bareCombinations.put(11, Integer.parseInt("011010", 2));
        bareCombinations.put(12, Integer.parseInt("011001", 2));
        bareCombinations.put(13, Integer.parseInt("010110", 2));
        bareCombinations.put(14, Integer.parseInt("010101", 2));
        bareCombinations.put(15, Integer.parseInt("010011", 2));
        bareCombinations.put(16, Integer.parseInt("001110", 2));
        bareCombinations.put(17, Integer.parseInt("001101", 2));
        bareCombinations.put(18, Integer.parseInt("001011", 2));
        bareCombinations.put(19, Integer.parseInt("000111", 2));
    }
}



