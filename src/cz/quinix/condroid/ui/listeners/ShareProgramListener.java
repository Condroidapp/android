package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.ShowAnnotation;

public class ShareProgramListener implements View.OnClickListener {

    Activity activity;


    public ShareProgramListener(Activity activity) {
        super();
        this.activity = activity;
    }


    public void onClick(View v) {
        this.invoke(((ShowAnnotation) activity).getAnnotation());
    }

    public void invoke(Annotation annotation) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, annotation.getTitle() + ", @" + annotation.getLocation() + "");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Právě jsem na pořadu ");
        activity.startActivity(Intent.createChooser(intent, "Sdílet"));
    }

}
