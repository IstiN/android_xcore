package by.istin.android.xcore.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by uladzimir_klyshevich on 11/7/15.
 */
public class XRecycler<RecycleElement> {

    public interface RecyclerElementCreator<RecycleElement> {

        RecycleElement createNew(XRecycler pRecycler);

    }

    private int startCapacity = 30;

    private List<RecycleElement> freeElements = new ArrayList<>();

    private RecyclerElementCreator<RecycleElement> mRecycleElementRecyclerElementCreator;

    private Object mLock = new Object();

    public XRecycler(RecyclerElementCreator<RecycleElement> pCreator) {
        mRecycleElementRecyclerElementCreator = pCreator;
        addMore();
    }

    private void addMore() {
        for (int i = 0; i < startCapacity; i++) {
            freeElements.add(mRecycleElementRecyclerElementCreator.createNew(this));
        }
    }

    public RecycleElement get() {
        synchronized (mLock) {
            if (freeElements.isEmpty()) {
                addMore();
            }
            return freeElements.remove(0);
        }
    }

    public void recycled(RecycleElement pRecycleElement) {
        synchronized (mLock) {
            if (freeElements.size() < startCapacity) {
                freeElements.add(pRecycleElement);
            }
        }
    }

}
