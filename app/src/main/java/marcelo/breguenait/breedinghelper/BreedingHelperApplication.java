package marcelo.breguenait.breedinghelper;

import android.app.Application;

import databasemanager.JsonDataInterpreter;
import databasemanager.JsonDataProvider;
import databasemanager.SqlDatabase;

public class BreedingHelperApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SqlDatabase.initialize(this);
        JsonDataProvider.initialize(this);
        CachedPokemonIcons.initialize(this);
        JsonDataInterpreter jsonDataInterpreter = new JsonDataInterpreter(this);
    }
}
