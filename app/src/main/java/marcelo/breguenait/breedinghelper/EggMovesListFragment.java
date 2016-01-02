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
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class EggMovesListFragment extends Fragment {
    RecyclerView mRecyclerView;
    AVLoadingIndicatorView loadingIcon;
    TextView noMovesText;
    ArrayList<MoveInfo> moveInfoList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_moves_list, container, false);

        noMovesText = (TextView) v.findViewById(R.id.fragmentMoves_textNoMoves);
        noMovesText.setText(R.string.message_no_egg_moves);

        loadingIcon = (AVLoadingIndicatorView) v.findViewById(R.id.fragmentMoves_loadingIcon);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.movesRecyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(new EggMoveAdapter(getContext(),null, mRecyclerView));


        return v;
    }

    public void switchData(ArrayList<MoveInfo> moveInfoList) {
        if(moveInfoList == null || moveInfoList.size() == 0)
            noMovesText.setVisibility(View.VISIBLE);
        else
            noMovesText.setVisibility(View.GONE);

        if(moveInfoList != null) {
            for (int i = 0; i < moveInfoList.size(); i++) {
                if (moveInfoList.get(i).getParentIds().isEmpty())
                    moveInfoList.remove(i);
            }
        }
        mRecyclerView.swapAdapter(new EggMoveAdapter(getContext(), moveInfoList, mRecyclerView), true);
        mRecyclerView.getAdapter().notifyDataSetChanged();
        loadingIcon.setVisibility(View.GONE);
    }
}

