package breedingmanager;

import android.util.SparseArray;

public class IvChanceCalculator {
    private final SparseArray<Integer> destinyKnotCombinations = new SparseArray<>(6);
    private final SparseArray<Integer> bareCombinations = new SparseArray<>(20);

    public IvChanceCalculator() {
        fillCombinations();
    }

    public double getIvChance(int[] firstIVs,
                              int[] secondIVs,
                              int[] goalIVs,
                              boolean destinyKnotEquipped) {
        double chance = 0.0d;

        final double MAX = 1.0d;
        final double MIN = 0.0d;
        final double HALF = 0.5d;
        final double RANDOM = (1.0d / 32.0d);

        /*Selects which set of combinations will be used for the chance calculation*/
        SparseArray<Integer> possibleCombinations;
        if (destinyKnotEquipped)
            possibleCombinations = destinyKnotCombinations;
        else
            possibleCombinations = bareCombinations;

        /*Iterates through each available combination*/
        for (int currentCombination = 0;
             currentCombination < possibleCombinations.size();
             currentCombination++) {
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
                    else if (firstIVs[currentIV] == 1 && secondIVs[currentIV] == 1)
                        chanceCurrentIV = MAX;
                    /*Else if only the first parent has the current IV*/
                    else if (firstIVs[currentIV] == 1 && secondIVs[currentIV] == 0) {
                        /* If neither has the item, the chance of getting the good IV from the
                        first is half.*/
                        chanceCurrentIV = HALF;
                    }
                    /*Else if only the second has the current IV*/
                    else if (secondIVs[currentIV] == 1 && firstIVs[currentIV] == 0) {
                        /* If neither has the item, the chance of getting the good IV from the
                        second is half.*/
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
