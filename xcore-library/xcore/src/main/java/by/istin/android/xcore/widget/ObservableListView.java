package by.istin.android.xcore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

public class ObservableListView extends XListView {

    private ArrayList<Callbacks> mCallbacks = new ArrayList<Callbacks>();

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            for (Callbacks c : mCallbacks) {
                c.recomputeScrollingMetrics(ObservableListView.this);
            }
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
        public void onScrollChanged(ObservableListView listView, int deltaX, int deltaY);
        public void recomputeScrollingMetrics(ObservableListView listView);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        for (Callbacks c : mCallbacks) {
            c.onScrollChanged(this, l - oldl, t - oldt);
        }
        ViewTreeObserver vto = getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    }

    public void addCallbacks(Callbacks listener) {
        if (!mCallbacks.contains(listener)) {
            mCallbacks.add(listener);
        }

    }

    public void removeCallbacks(Callbacks listener) {
        mCallbacks.remove(listener);
        if (mCallbacks.isEmpty()) {
            ViewTreeObserver vto = getViewTreeObserver();
            if (vto.isAlive()) {
                vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
            }
        }
    }
}
