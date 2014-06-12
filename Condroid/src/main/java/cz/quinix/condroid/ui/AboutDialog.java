package cz.quinix.condroid.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import cz.quinix.condroid.R;

public class AboutDialog {

    private Context context;

    public AboutDialog(Context context) {
        this.context = context;
        AlertDialog.Builder ab;
        if (Build.VERSION.SDK_INT > 10) {
            ab = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        } else {
            ab = new AlertDialog.Builder(context);
        }

        View v = LayoutInflater.from(context).inflate(R.layout.about_dialog, null);
        ab.setTitle(R.string.appNameAbout);


        ab.setPositiveButton("OK", new OkListener());
        ab.setNeutralButton("Přispět", new DonateListener());
        ab.setNegativeButton("Feedback", new FeedbackListener());
        ImageView follow = ((ImageView) v.findViewById(R.id.iFollow));
        follow.setOnClickListener(new FollowListener());
        ab.setIcon(R.drawable.icon);

        ab.setView(v);

        ab.show();

    }

    public Context getContext() {
        return context;
    }

    class OkListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            SharedPreferences pr = PreferenceManager.getDefaultSharedPreferences(AboutDialog.this.getContext());
            SharedPreferences.Editor e = pr.edit();
            e.putBoolean("aboutShown", true);
            e.commit();
        }

    }

    class FeedbackListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            /* Create the Intent */
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

            /* Fill it with Data */
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{getContext().getString(R.string.tAboutMail)});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[Condroid] - Feedback");
            //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

            /* Send it off to the Activity-Chooser */
            getContext().startActivity(emailIntent);
        }

    }

    class DonateListener implements DialogInterface.OnClickListener {

        public void onClick(DialogInterface dialog, int which) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations" +
                    "&business=2Z394R5DLKGU4&lc=CZ&item_name=Condroid&currency_code=CZK" +
                    "&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHostedGuest"));
            getContext().startActivity(intent);

        }

    }

    class FollowListener implements android.view.View.OnClickListener {

        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://mobile.twitter.com/Condroid_CZ"));
            getContext().startActivity(intent);
        }

    }

}
