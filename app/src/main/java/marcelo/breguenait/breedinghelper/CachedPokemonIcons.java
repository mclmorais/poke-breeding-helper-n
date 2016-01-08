package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

import databasemanager.MyDatabase;

public class CachedPokemonIcons {


    private static CachedPokemonIcons instance;
    private final HashMap<Integer, Drawable> iconsMap = new HashMap<>();
    private final Context context;

    private CachedPokemonIcons(Context context) {
        this.context = context;
        fillIconsList();
    }

    public static CachedPokemonIcons getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new CachedPokemonIcons(context);
    }

    private void fillIconsList() {

        ArrayList<Integer> ids = MyDatabase.getInstance().getPokemonIds();

        for (Integer id : ids) {
            String iconId = "pkmn_" + String.format("%03d", id);
            Drawable d = ContextCompat.getDrawable(context, context.getResources().getIdentifier(iconId, "drawable", context.getPackageName()));
            iconsMap.put(id, d);
        }

    }

    Drawable getIcon(int id) {
        return iconsMap.get(id);
    }
}
