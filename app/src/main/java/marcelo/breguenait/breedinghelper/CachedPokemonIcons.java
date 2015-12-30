package marcelo.breguenait.breedinghelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Marcelo on 30/12/2015.
 */
public class CachedPokemonIcons {


    private HashMap<Integer, Drawable> iconsMap = new HashMap<>();

    private Context context;


    private static CachedPokemonIcons instance;

    public static CachedPokemonIcons getInstance() {
        return instance;
    }

    public static void initialize(Context context) {
        instance = new CachedPokemonIcons(context);
    }

    private CachedPokemonIcons(Context context) {
        this.context = context;
        fillIconsList();
    }

    void fillIconsList() {

        ArrayList<Integer> ids = MyDatabase.getInstance().getPokemonIds();

        for (Integer id : ids) {
            String iconId = "pkmn_" + String.format("%03d", id);
            Drawable d = ContextCompat.getDrawable(context, context.getResources().getIdentifier(iconId, "drawable", context.getPackageName()));
            iconsMap.put(id,d);
        }

    }

    Drawable getIcon(int id) {
        return iconsMap.get(id);
    }
}
