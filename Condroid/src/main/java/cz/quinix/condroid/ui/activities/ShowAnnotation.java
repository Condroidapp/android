package cz.quinix.condroid.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.github.rtyley.android.sherlock.roboguice.activity.RoboSherlockActivity;
import com.google.inject.Inject;

import org.joda.time.Duration;
import org.joda.time.Instant;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import cz.quinix.condroid.R;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.AnnotationType;
import cz.quinix.condroid.ui.listeners.MakeFavoritedListener;
import cz.quinix.condroid.ui.listeners.SetReminderListener;
import cz.quinix.condroid.ui.listeners.ShareProgramListener;

public class ShowAnnotation extends RoboSherlockActivity {

    private Annotation annotation;
    private static DateFormat hourFormat = new SimpleDateFormat("HH:mm");
    private static DateFormat dayFormat = new SimpleDateFormat(
            "EE dd.MM.", new Locale("cs", "CZ"));

    @Inject
    private DataProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.annotation = (Annotation) this.getIntent().getSerializableExtra(
                "annotation");

        this.setContentView(R.layout.annotation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.setupIcons();
        this.addTypes();

        TextView title = (TextView) this.findViewById(R.id.annot_title);
        title.setText(this.annotation.getTitle());


        if (this.annotation.getAuthor() != null && !this.annotation.getAuthor().trim().equals("")) {
            TextView author = (TextView) this.findViewById(R.id.annot_author);
            author.setText(this.annotation.getAuthor());
        } else {
            this.findViewById(R.id.iAuthor).setVisibility(View.GONE);
            this.findViewById(R.id.annot_author).setVisibility(View.GONE);
        }
        TextView pid = (TextView) this.findViewById(R.id.annot_pid);
        pid.setText("#" + this.annotation.getPid());

        this.setupDates();

        TextView line = (TextView) this.findViewById(R.id.tLine);
        line.setText(this.provider.getProgramLine(this.annotation.getLid()).getName());
        if (this.annotation.getLocation() != null && !this.annotation.getLocation().trim().equals("")) {
            TextView location = (TextView) this.findViewById(R.id.annot_location);
            location.setText(this.annotation.getLocation());
        } else {
            findViewById(R.id.lLocation).setVisibility(View.GONE);
        }


        TextView text = (TextView) this.findViewById(R.id.annot_text);
        text.setText(this.annotation.getAnnotation());


    }

    private void setupDates() {


        if (annotation.getStart() != null && annotation.getEnd() != null) {
            findViewById(R.id.lTimeLayout).setVisibility(View.VISIBLE);

            TextView tStart = (TextView) findViewById(R.id.tStart);
            TextView tEnd = (TextView) findViewById(R.id.tEnd);

            tStart.setText(hourFormat.format(annotation.getStart()));
            tEnd.setText(hourFormat.format(annotation.getEnd()));

            if (!this.isDateToday(annotation.getStart())) {
                TextView day = (TextView) findViewById(R.id.tDay);
                day.setText(dayFormat.format(annotation.getStart()));
                day.setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.tDay).setVisibility(View.GONE);
            }


            if (isRunning()) {
                findViewById(R.id.lRunningNow).setVisibility(View.VISIBLE);
                findViewById(R.id.lStartsInMinutes).setVisibility(View.GONE);
            } else if (isStartingShortly(60)) {
                findViewById(R.id.lRunningNow).setVisibility(View.GONE);
                findViewById(R.id.lStartsInMinutes).setVisibility(View.VISIBLE);

                TextView info = (TextView) findViewById(R.id.tStartsShortly);

                Calendar c = Calendar.getInstance();
                c.setTime(annotation.getStart());

                int minutes = this.getMinutesToStart();

                String text = getString(R.string.startsInXMinutes);
                text += " " + this.getResources().getQuantityString(R.plurals.minutes, minutes, minutes);

                info.setText(text);
            } else {
                findViewById(R.id.lRunningNow).setVisibility(View.GONE);
                findViewById(R.id.lStartsInMinutes).setVisibility(View.GONE);
            }

        } else {
            findViewById(R.id.lTimeLayout).setVisibility(View.GONE);

            findViewById(R.id.lRunningNow).setVisibility(View.GONE);
            findViewById(R.id.lStartsInMinutes).setVisibility(View.GONE);
        }


    }

    private boolean isStartingShortly(int minutes) {

        int toStart = getMinutesToStart();

        return toStart < minutes && toStart > 0;
    }

    private int getMinutesToStart() {
        Duration duration = new Duration(new Instant().getMillis(), annotation.getStart().getTime());

        return (int) duration.getStandardMinutes();
    }

    private boolean isRunning() {
        Date now = new Date();

        return this.annotation.getStart().before(now) && this.annotation.getEnd().after(now);
    }

    private void setupIcons() {
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");

        TextView iAuthor = (TextView) findViewById(R.id.iAuthor);
        TextView iLine = (TextView) findViewById(R.id.iLine);
        TextView iLocation = (TextView) findViewById(R.id.iLocation);

        iAuthor.setText(R.string.fa_user);
        iLine.setText(R.string.fa_bars);
        iLocation.setText(R.string.fa_map_marker);

        iAuthor.setTypeface(type);
        iLine.setTypeface(type);
        iLocation.setTypeface(type);


    }

    private void addTypes() {
        TextView typeField = (TextView) this.findViewById(R.id.annot_type);
        ViewGroup parent = (ViewGroup) typeField.getParent();
        try {
            typeField.setText(this.getString(AnnotationType.getTextualType(annotation.getType().mainType)));
        } catch (IllegalStateException e) {
            typeField.setVisibility(View.GONE);
        }

        String[] aT = annotation.getAdditionalTypes();

        if (aT.length > 0) {
            for (String anAT : aT) {
                if (anAT.length() > 0) {
                    TextView newField = new TextView(this.getApplicationContext());
                    try {
                        newField.setText(this.getString(AnnotationType.getTextualType(anAT)));
                    } catch (IllegalStateException e) {
                        continue;
                    }

                    newField.setBackgroundResource(R.color.condroidGreen);
                    newField.setTextColor(getResources().getColor(R.color.white));
                    newField.setPadding(typeField.getPaddingLeft(), typeField.getPaddingTop(), typeField.getPaddingRight(), typeField.getPaddingBottom());
                    newField.setLayoutParams(typeField.getLayoutParams());

                    parent.addView(newField);
                }
            }
        }
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    boolean isDateToday(Date date) {
        Calendar today = Calendar.getInstance(TimeZone.getDefault(), new Locale("cs", "CZ"));
        today.setTime(new Date());

        Calendar compared = Calendar.getInstance();
        compared.setTime(date);

        //its today
        return compared.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && compared.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && compared.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = this.getSupportMenuInflater();
        mi.inflate(R.menu.show_annotation, menu);
        ShareActionProvider mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.mShare).getActionProvider();
        mShareActionProvider.setShareIntent(new ShareProgramListener(this).getShareActionIntent(this.annotation));

        if (provider.getFavorited().contains(Integer.valueOf(annotation.getPid()))) {
            menu.findItem(R.id.mFavorite).setIcon(R.drawable.star_yellow);
        }

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.mReminder:
                new SetReminderListener(this, provider).invoke(this.annotation);
                return true;
            case R.id.mFavorite:
                if (new MakeFavoritedListener(this, provider).invoke(this.annotation)) {
                    item.setIcon(R.drawable.star_yellow);
                } else {
                    item.setIcon(R.drawable.star_white);
                }
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
