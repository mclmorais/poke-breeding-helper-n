package marcelo.breguenait.breedinghelper;

import java.util.Arrays;

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

//This class is only here for retrocompatibility, will be removed after a while
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

            this.eggGroup1 = EggGroup.WATER_1;
            this.eggGroup2 = EggGroup.WATER_2;

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






