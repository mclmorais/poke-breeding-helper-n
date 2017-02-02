package marcelo.breguenait.breedinghelper;

import android.app.Application;

import databasemanager.JsonDatabaseManager;
import databasemanager.JsonFileReader;
import databasemanager.SqlDatabase;

public class BreedingHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SqlDatabase.initialize(this);

        CachedPokemonIcons.initialize(this);
        JsonFileReader jsonFileReader = new JsonFileReader(this);
        JsonDatabaseManager.initialize(this);



    }
}
