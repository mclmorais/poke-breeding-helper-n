package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

import databasemanager.DatabaseConstants;
import databasemanager.SqlDatabase;

public class CachedPokemonIcons {


    private static CachedPokemonIcons instance;
    private final HashMap<Integer, Drawable> iconsMap = new HashMap<>();
    //private final Context context;
    Drawable missingno;

    private CachedPokemonIcons(Context context) {
        //this.context = context;
        fillIconsList(context);
    }

    public static CachedPokemonIcons getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new CachedPokemonIcons(context);
    }

    private void fillIconsList(Context context) {

        ArrayList<Integer> ids = SqlDatabase.getInstance().getPokemonIds();

        for (Integer id : ids) {
            String iconId = "pkmn_" + String.format("%03d", id);
            Drawable d = ContextCompat.getDrawable(context, context.getResources().getIdentifier(iconId, "drawable", context.getPackageName()));
            iconsMap.put(id, d);
        }

        missingno = ContextCompat.getDrawable(context, R.drawable.pkmn_missingno);

    }

    Drawable getIcon(int id) {
        if (DatabaseConstants.pokemonIdIsValid(id))
            return iconsMap.get(id);
        else
            return missingno;
    }
}
