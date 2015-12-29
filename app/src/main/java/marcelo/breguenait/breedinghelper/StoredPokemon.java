package marcelo.breguenait.breedinghelper;


import java.util.UUID;

public class StoredPokemon {

    /*Unique identifier of this stored Pokemon*/
    private UUID storedId; //TODO: create internally

    /*Dynamic data - User defined*/
    /*These fields should be set manually*/
    private int pokemonId;
    private int genderId;
    private int[] IVs;
    private int natureId;
    private int abilityId;

    /*Static data - received from the database based on dynamic data*/
    /*These fields should be set automatically*/
    private int eggGroup1Id;
    private int eggGroup2Id;
    private int evolutionChainId;
    private int abilitySlot;

    private StoredPokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilityId) {

        storedId = UUID.randomUUID();

        this.pokemonId = pokemonId;
        this.genderId = genderId;
        this.IVs = IVs;
        this.natureId = natureId;
        this.abilityId = abilityId;

        /* Calls singleton database here to fill:
        * eggGroup1Id
        * eggGroup2Id
        * isAbilityHidden
        * */
    }


    public int getAbilitySlot() {
        return abilitySlot;
    }

    public UUID getStoredId() {
        return storedId;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public void setPokemonId(int pokemonId) {
        this.pokemonId = pokemonId;
    }

    public int getGenderId() {
        return genderId;
    }

    public void setGenderId(int genderId) {
        this.genderId = genderId;
    }

    public int[] getIVs() {
        return IVs;
    }

    public void setIVs(int[] IVs) {
        this.IVs = IVs;
    }

    public int getNatureId() {
        return natureId;
    }

    public void setNatureId(int natureId) {
        this.natureId = natureId;
    }

    public int getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(int abilityId) {
        this.abilityId = abilityId;
    }

    public int getEggGroup1Id() {
        return eggGroup1Id;
    }

    public int getEggGroup2Id() {
        return eggGroup2Id;
    }

    public int getEvolutionChainId() {
        return evolutionChainId;
    }

    static class Builder {
        private int pokemonId = -1;
        private int genderId = -1;
        private int[] iVs = {-1, -1, -1, -1, -1, -1};
        private int natureId = -1;
        private int abilityId = -1;

        public Builder setPokemonId(int pokemonId) {
            this.pokemonId = pokemonId;
            return this;
        }

        public Builder setGenderId(int genderId) {
            this.genderId = genderId;
            return this;
        }

        public Builder setIVs(int[] iVs) {
            this.iVs = iVs;
            return this;
        }

        public Builder setNatureId(int natureId) {
            this.natureId = natureId;
            return this;
        }

        public Builder setAbilityId(int abilityId) {
            this.abilityId = abilityId;
            return this;
        }

        public StoredPokemon createStoredPokemon() {
            return new StoredPokemon(pokemonId, genderId, iVs, natureId, abilityId);
        }
    }
}
