package cz.quinix.condroid.ui.listeners;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.ShowAnnotation;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
        activity.startActivity(Intent.createChooser(this.getShareActionIntent(annotation), "Sdílet"));
    }

    public Intent getShareActionIntent(Annotation annotation) {
        DateFormat dayFormat = new SimpleDateFormat(
                "EE dd.MM. HH:mm", new Locale("cs", "CZ"));
        DateFormat hourFormat = new SimpleDateFormat(
                "HH:mm", new Locale("cs", "CZ"));

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, annotation.getTitle() +
                ((annotation.getLocation() != null && !annotation.getLocation().equals("")) ? ", @" + annotation.getLocation() : "") +
                ((annotation.getStartTime() != null && annotation.getEndTime() != null) ? ", " + dayFormat.format(annotation.getStartTime()) + " - " + hourFormat.format(annotation.getEndTime()) : "")
        );
        //intent.putExtra(Intent.EXTRA_SUBJECT, "Právě jsem na pořadu ");
        return intent;
    }

}
