package marcelo.breguenait.breedinghelper;

import android.app.Application;

import databasemanager.MyDatabase;

public class BreedingHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MyDatabase.initialize(getApplicationContext());
        CachedPokemonIcons.initialize(getApplicationContext());
    }
}
