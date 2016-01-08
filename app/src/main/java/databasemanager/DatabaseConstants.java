package databasemanager;


public class DatabaseConstants {
    public static final int FEMALE_ID = 1;
    public static final int MALE_ID = 2;
    public static final int GENDERLESS_ID = 3;

    public static final int DITTO_ID = 132;

    public static final int MALE_ONLY = 0;
    public static final int GENDERLESS_ONLY = -1;
    public static final int FEMALE_ONLY = 8;

    private static final int MAX_POKEMON_ID = 721;
    private static final int MAX_NATURE_ID = 25;
    private static final int MAX_ABILITY_ID = 191;
    private static final int MAX_EGG_GROUP_ID = 15;

    public static boolean pokemonIdIsValid(int pokemonId) {
        return (pokemonId > 0 && pokemonId <= MAX_POKEMON_ID);
    }

    public static boolean eggGroupIsValid(int eggGroupId) {
        return (eggGroupId > 0 && eggGroupId <= MAX_EGG_GROUP_ID);
    }

    public static boolean natureIsValid(int natureId) {
        return (natureId > 0 && natureId <= MAX_NATURE_ID);
    }

    public static boolean abilitySlotIsValid(int abilitySlot) {
        return (abilitySlot > 0 && abilitySlot <= 3);
    }


}
