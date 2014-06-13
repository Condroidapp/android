package cz.quinix.condroid.ui;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.actionbarsherlock.internal.widget.IcsToast;

import cz.quinix.condroid.R;
import cz.quinix.condroid.util.IabHelper;
import cz.quinix.condroid.util.IabResult;
import cz.quinix.condroid.util.Purchase;

public class AboutDialog {

    private Activity context;
    String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAigAoSVGbSqLE2Dt7BjghiEZp4q8TR0m1Et3EGU6Z6h06/SBbN1w4I661Xp6JKSOB9Wy5VG/H1WtBgfA+91LQl5utnphAVxQPUhAsbjHnzAta804d5Bn6wb3FvKvby4HkdyyisjSf/vcKY4dfCD3GQEHSqGdF+JYBepJqX5xg7CVYP05TMXPjpHJvcNo6S0a2eCtLC5AFjxYp1nuP/p8vunkW5YswdIvsc9M40LeAGInR9zilgCd3xECN/KsPxyKMNLVKK26QAmTKESpyhXRFXdO9k+JEtGGcK7faeVvlCNinozhugwnKQEgk2DdfiuyXr77PQdhq2+TX5sIb16TQCQIDAQAB";
    private IabHelper mHelper;

    public AboutDialog(Activity context) {
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
        mHelper = new IabHelper(AboutDialog.this.context, publicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d("condroid", "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
            }
        });

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




            mHelper.launchPurchaseFlow(AboutDialog.this.context, "small_beer", 10001,
                    new IabHelper.OnIabPurchaseFinishedListener() {
                        @Override
                        public void onIabPurchaseFinished(IabResult result, Purchase info) {
                            IcsToast.makeText(context, info.getSku(), Toast.LENGTH_LONG).show();
                        }
                    }, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");

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
