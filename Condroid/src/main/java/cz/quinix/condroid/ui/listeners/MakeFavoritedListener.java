package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.activities.ShowAnnotation;

public class MakeFavoritedListener implements OnClickListener {

    private Activity activity;
    private DataProvider provider;

    public MakeFavoritedListener(Activity activity, DataProvider provider) {
        super();
        this.activity = activity;
        this.provider = provider;
    }

    public void onClick(View v) {
        this.invoke(((ShowAnnotation) activity).getAnnotation());
    }

    public boolean invoke(Annotation annotation) {

        return provider.doFavorite(
                annotation.getPid());
    }

}
