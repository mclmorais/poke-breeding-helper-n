package marcelo.breguenait.breedinghelper;

import android.app.Application;

/**
 * Created by Marcelo on 18/12/2014.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        PokemonData.initialize(getApplicationContext());
        MyDatabase.initialize(getApplicationContext());
        CachedPokemonIcons.initialize(getApplicationContext());
    }
}
