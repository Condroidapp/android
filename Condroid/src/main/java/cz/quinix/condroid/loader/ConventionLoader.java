package cz.quinix.condroid.loader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.R;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.ProgramActivity;
import retrofit.RestAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ConventionLoader extends ListenedAsyncTask<Void, Void> {

    public ConventionLoader(AsyncTaskListener listener) {
        super(listener);
    }

    @Override
    protected void showDialog() {
        if (parentActivity != null) {
            pd = ProgressDialog.show(parentActivity, "", parentActivity.getString(R.string.loading), true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    ConventionLoader.this.cancel(true);
                    if (parentActivity instanceof ProgramActivity) {
                        ((ProgramActivity) parentActivity).stopAsyncTask();
                    }
                }
            });
        }
    }



    @Override
    protected List<Convention> doInBackground(Void... source) {
        CondroidApi service = getCondroidService();
        return service.listEvents();
    }


}
