package marcelo.breguenait.breedinghelper;

import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by Marcelo on 13/12/2015.
 */
public class MyAsyncTask extends AsyncTask {


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("STARTING!");
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        MovesManager m = (MovesManager) objects[0];
        int id = (int) objects[1];
        m.showMoves(id);

        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        System.out.println("DONEZO!");
    }

}
