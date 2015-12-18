/**
 * Created by Marcelo on 13/12/2015.
 */
package marcelo.breguenait.breedinghelper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class EggMovesListFragment extends Fragment {
    RecyclerView mRecyclerView;
    ArrayList<MoveInfo> moveInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moves_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.movesRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(new EggMoveAdapter(getContext(),null, mRecyclerView));



        return v;
    }

    public void switchData(ArrayList<MoveInfo> moveInfoList) {
        mRecyclerView.swapAdapter(new EggMoveAdapter(getContext(), moveInfoList, mRecyclerView), true);
        mRecyclerView.getAdapter().notifyDataSetChanged();
    }
}

