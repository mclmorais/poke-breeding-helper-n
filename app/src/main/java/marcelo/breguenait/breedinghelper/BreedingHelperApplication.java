package marcelo.breguenait.breedinghelper;

import android.app.Application;

import databasemanager.JsonDataProvider;
import databasemanager.SqlDatabase;

public class BreedingHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SqlDatabase.initialize(getApplicationContext());
        JsonDataProvider.initialize(getApplicationContext());
        //JsonDataProvider.initialize(getApplicationContext());
        CachedPokemonIcons.initialize(getApplicationContext());
    }
}
