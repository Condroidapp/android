package cz.quinix.condroid.loader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.util.Xml;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.R;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.model.Convention;
import cz.quinix.condroid.ui.ProgramActivity;
import retrofit.RestAdapter;
import roboguice.util.RoboAsyncTask;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class ConventionLoader extends AListenedAsyncTask<Void, Convention> {

    public ConventionLoader(ITaskListener listener) {
        super(listener);
    }

    @Override
    public List<Convention> call() throws Exception {
        CondroidApi service = getCondroidService();
        return service.listEvents();
    }




}
