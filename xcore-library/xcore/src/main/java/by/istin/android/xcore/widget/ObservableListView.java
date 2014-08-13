package by.istin.android.xcore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;

public class ObservableListView extends XListView {

    private Callbacks mCallback;

    private View mHeaderView;

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mCallback.recomputeScrollingMetrics(ObservableListView.this);
        }
    };

    public ObservableListView(Context context) {
        super(context);
    }

    public ObservableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public static interface Callbacks {

        void onScrollChanged(ObservableListView listView, int deltaX, int deltaY);

        void recomputeScrollingMetrics(ObservableListView listView);

        int getHeaderHeight(ObservableListView listView);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mCallback != null) {
            mCallback.onScrollChanged(this, l - oldl, t - oldt);
        }
    }

    public void addCallbacks(Callbacks listener) {
        if (mCallback != listener) {
            mCallback = listener;
            removeStubHeader();
            ViewTreeObserver vto = getViewTreeObserver();
            if (vto.isAlive()) {
                vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
            }
            int headerHeight = mCallback.getHeaderHeight(this);
            mHeaderView = new View(getContext());
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, headerHeight);
            mHeaderView.setLayoutParams(layoutParams);
            addHeaderView(mHeaderView, null, false);
        }

    }

    public int getHeaderTop() {
        return mHeaderView == null ? 0 : mHeaderView.getTop();
    }

    private void removeStubHeader() {
        if (mHeaderView != null) {
            removeHeaderView(mHeaderView);
            mHeaderView = null;
        }
    }

    public void removeCallback(Callbacks listener) {
        mCallback = null;
        removeStubHeader();
        ViewTreeObserver vto = getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
    }
}
