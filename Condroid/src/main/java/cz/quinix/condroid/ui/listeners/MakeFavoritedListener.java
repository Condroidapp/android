package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.activities.ShowAnnotation;

public class MakeFavoritedListener implements OnClickListener {

    private Activity activity;

    public MakeFavoritedListener(Activity activity) {
        super();
        this.activity = activity;
    }

    public void onClick(View v) {
        this.invoke(((ShowAnnotation) activity).getAnnotation());
    }

    public boolean invoke(Annotation annotation) {
        boolean favorited = DataProvider.getInstance(
                activity.getApplicationContext()).doFavorite(
                annotation.getPid());
        //ProgramActivity.notifyDataSetChanged = true;
        return favorited;
    }

}
