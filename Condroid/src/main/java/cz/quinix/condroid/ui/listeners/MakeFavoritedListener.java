package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.inject.Inject;

import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.activities.ShowAnnotation;
import roboguice.RoboGuice;

public class MakeFavoritedListener {

    @Inject private DataProvider provider;

    public MakeFavoritedListener(Context context) {
        RoboGuice.getInjector(context).injectMembers(this);
    }

    public boolean invoke(Annotation annotation) {
        return this.invoke(annotation.getPid());
    }

    public boolean invoke(int pid) {
        return provider.doFavorite(pid);
    }

}
