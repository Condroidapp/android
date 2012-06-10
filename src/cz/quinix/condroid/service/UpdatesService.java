package cz.quinix.condroid.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.Preferences;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Honza
 * Date: 1.6.12
 * Time: 0:09
 * To change this template use File | Settings | File Templates.
 */
public class UpdatesService extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return Service.START_STICKY;
    }

    private void handleCommand(Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        ConnectivityManager c = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        if (!c.getActiveNetworkInfo().isConnected()) {
            this.stopSelf();
            return;
        }

        if (preferences.getBoolean("update_check", false) && !preferences.getBoolean("updates_found", false)) {
            Log.d("Condroid", "Update service active");
            Convention convention = DataProvider.getInstance(this).getCon();
            if (convention == null || convention.getCid() < 1) {
                Preferences.stopUpdateService(this);
                stopSelf();
                return;
            }
            HttpClient client = new DefaultHttpClient();
            HttpHead head = new HttpHead();
            try {
                head.setURI(new URI("http://condroid.fan-project.com/api/2/annotations/" + convention.getCid()));
            } catch (URISyntaxException e) {
                Log.e("Condroid", "URL parsing", e);
            }
            SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            head.setHeader("If-Modified-Since", format.format(convention.getLastUpdate()));
            //head.setHeader("X-If-Count-Not-Match", ""+DataProvider.getInstance(this).getAnnotationsCount());
            SharedPreferences.Editor editor = preferences.edit();
            try {
                Log.d("Condroid", "Executing HEAD request");
                HttpResponse response = client.execute(head);

                if (response.getStatusLine().getStatusCode() == 200) {
                    editor.putBoolean("updates_found", true);

                    try {
                        editor.putString("updates_found_time", formatter.format(format.parse(response.getFirstHeader("Last-Modified").getValue())));
                    } catch (NullPointerException e) {
                        editor.putString("updates_found_time", formatter.format(new Date()));
                    }
                    Preferences.stopUpdateService(this);
                    Log.d("Condroid", "Found updates, stopping myself");
                }
                Log.d("Condroid", "HTTP Code " + response.getStatusLine().getStatusCode());
                editor.putLong("last_update", new Date().getTime());
            } catch (IOException e) {
                Log.e("Condroid", "IO", e);
            } catch (java.text.ParseException e) {
                Log.e("Condroid", "Parsing", e);
            } finally {
                editor.commit();
                Log.d("Condroid", "Execute ok");
                this.stopSelf();
            }
        } else {
            Preferences.stopUpdateService(this);
            this.stopSelf();
            return;
        }
    }
}
