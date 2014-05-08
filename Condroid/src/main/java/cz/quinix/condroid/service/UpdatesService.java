package cz.quinix.condroid.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.inject.Inject;

import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.Preferences;
import roboguice.service.RoboService;
import roboguice.util.RoboAsyncTask;

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
public class UpdatesService extends RoboService {
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
        if (c.getActiveNetworkInfo() == null || !c.getActiveNetworkInfo().isConnected()) {
            this.stopSelf();
            return;
        }

        if (preferences.getBoolean("update_check", false) && !preferences.getBoolean("updates_found", false)) {
            new ServiceAsync(this).execute();
        } else {
            Preferences.stopUpdateService(this);
            this.stopSelf();
            return;
        }
    }

    private class ServiceAsync extends RoboAsyncTask<Void> {

        @Inject private DataProvider provider;

        public ServiceAsync(Context context) {
            super(context);
        }


        @Override
        public Void call() throws Exception {
            Log.d("Condroid", "Update service active");
            Convention convention = provider.getCon();
            if (convention == null || convention.getId() < 1) {
                Preferences.stopUpdateService(UpdatesService.this);
                stopSelf();
                return null;
            }
            HttpClient client = new DefaultHttpClient();
            HttpHead head = new HttpHead();
            try {
                head.setURI(new URI(convention.getDatasource()));
            } catch (URISyntaxException e) {
                Log.e("Condroid", "URL parsing", e);
            }
            SimpleDateFormat internationalFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

            head.setHeader("If-Modified-Since", internationalFormat.format(convention.getLastUpdate())); //if new/updates
            head.setHeader("X-If-Count-Not-Match", "" + provider.getAnnotationsCount()); //if deletes
            head.setHeader("X-Device-Info", CondroidActivity.getDeviceInfoString(UpdatesService.this));
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(UpdatesService.this).edit();

            try {
                Log.d("Condroid", "Executing HEAD request");
                HttpResponse response = client.execute(head);

                if (response.getStatusLine().getStatusCode() == 200) {
                    editor.putBoolean("updates_found", true);

                    try {
                        editor.putLong("updates_found_time", internationalFormat.parse(response.getFirstHeader("Last-Modified").getValue()).getTime());
                    } catch (NullPointerException e) {
                        editor.putLong("updates_found_time", new Date().getTime());
                    }
                    Preferences.stopUpdateService(UpdatesService.this);
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
                stopSelf();
            }
            return null;

        }
    }
}
