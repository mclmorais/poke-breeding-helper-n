package marcelo.breguenait.breedinghelper;

import android.app.Application;

import databasemanager.JsonDatabase;
import databasemanager.SqlDatabase;

public class BreedingHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SqlDatabase.initialize(getApplicationContext());
        JsonDatabase.initialize(getApplicationContext());
        //JsonDatabase.initialize(getApplicationContext());
        CachedPokemonIcons.initialize(getApplicationContext());
    }
}
