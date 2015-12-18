package marcelo.breguenait.breedinghelper;

/**
 * Created by Marcelo on 17/12/2015.
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AutofitRecyclerView extends RecyclerView {
    private GridLayoutManager manager;
    private int columnWidth = -1;

    int getColumnWidth() {
       return android.R.attr.columnWidth;
    }

    public AutofitRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public AutofitRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        if (attrs != null) {
//            int[] attrsArray = {
//                    android.R.attr.columnWidth
//            };
//            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
//            columnWidth = array.getDimensionPixelSize(0, -1);
//            array.recycle();
//        }
//
//        manager = new WrappableGridLayoutManager(getContext(), 1);
//        setLayoutManager(manager);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
//        if (columnWidth > 0) {
//            int spanCount = Math.max(1, getMeasuredWidth() / columnWidth);
//            manager.setSpanCount(spanCount);
//            WrappableGridLayoutManager lm = (WrappableGridLayoutManager) getLayoutManager();
//            lm.remeasure2(spanCount);
//            setLayoutManager(new WrappableGridLayoutManager(getContext(), spanCount));
//            //lm.remeasure(spanCount);
//
//            postInvalidate();
//        }
    }
}