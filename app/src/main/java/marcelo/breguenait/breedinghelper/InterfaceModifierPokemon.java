package marcelo.breguenait.breedinghelper;

/**
 * Created by Marcelo on 06/01/2016.
 */
public class InterfaceModifierPokemon {

    private int pokemonId;
    private int genderId;
    private int[] IVs;
    private int natureId;
    private int abilitySlot;

    public InterfaceModifierPokemon(int pokemonId, int genderId, int[] IVs, int natureId, int abilitySlot) {
        this.pokemonId = pokemonId;
        this.genderId = genderId;
        this.IVs = IVs;
        this.natureId = natureId;
        this.abilitySlot = abilitySlot;
    }

    public InterfaceModifierPokemon() {

        this.pokemonId = -1;
        this.genderId = -1;
        this.IVs = new int[]{-1,-1,-1,-1,-1,-1};
        this.natureId = -1;
        this.abilitySlot = -1;
    }

    public int getPokemonId() {
        return pokemonId;
    }

    public int getGenderId() {
        return genderId;
    }

    public int[] getIVs() {
        return IVs;
    }

    public int getNatureId() {
        return natureId;
    }

    public int getAbilitySlot() {
        return abilitySlot;
    }
}
