package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.ProgramActivity;
import cz.quinix.condroid.ui.ShowAnnotation;

public class MakeFavoritedListener implements OnClickListener {

    private Activity activity;

    public MakeFavoritedListener(Activity activity) {
        super();
        this.activity = activity;
    }

    public void onClick(View v) {
        this.invoke(((ShowAnnotation) activity).getAnnotation(), v);
    }

    public void invoke(Annotation annotation, View v) {
        boolean favorited = DataProvider.getInstance(
                activity.getApplicationContext()).doFavorite(
                annotation.getPid());
        if (v != null) {
            ImageView favorite = (ImageView) v.findViewById(R.id.iFavorite);

            if (favorited) {
                favorite.setImageResource(R.drawable.star_active);
            } else {
                favorite.setImageResource(R.drawable.star);
            }
        }
        ProgramActivity.refreshDataset = true;
    }

}
