package by.istin.android.xcore.ui.binder;

import android.content.Context;

/**
 * Created by uladzimir_klyshevich on 8/13/15.
 */
public interface ICollectionView<WrappedView> {

    Context getContext();

    WrappedView getWrappedView();

}
