package cz.quinix.condroid.loader;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.util.Xml;
import cz.quinix.condroid.R;
import cz.quinix.condroid.XMLProccessException;
import cz.quinix.condroid.abstracts.AsyncTaskListener;
import cz.quinix.condroid.abstracts.CondroidActivity;
import cz.quinix.condroid.abstracts.ListenedAsyncTask;
import cz.quinix.condroid.database.DataProvider;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.ui.ProgramActivity;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataLoader extends ListenedAsyncTask<String, Integer> {

    //private ProgressDialog pd;
    private String pdString;
    private int pdMax;
    private int resultCode = 0;
    //private int pdActual;

    public DataLoader(AsyncTaskListener listener) {
        super(listener);
        pdString = parentActivity.getString(R.string.loading);
        this.showDialog();
    }

    @Override
    protected void onPostExecute(List<?> result) {
        pd.dismiss();
        super.onPostExecute(result);
    }

    public int getResultCode() {
        return this.resultCode;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int value = values[0];
        if (values.length == 2) {
            this.pd.dismiss();
            pdString = parentActivity.getString(R.string.downloading);
            this.pdMax = values[1];
            this.showDialog();
        } else if (pd.getMax() > 0) {
            pd.setProgress(value);
        }
    }

    @Override
    protected void showDialog() {
        if (this.pd != null && this.pd.isShowing()) {
            pd.dismiss();
        }
        if (parentActivity != null) {
            this.pd = new ProgressDialog(parentActivity);
            this.pd.setMessage(pdString);
            if (this.pdMax > 0) {
                this.pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pd.setMax(pdMax);
            }
            pd.setCancelable(true);
            pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    dialogInterface.dismiss();
                    DataLoader.this.cancel(true);
                    if(parentActivity instanceof ProgramActivity) {
                        ((ProgramActivity) parentActivity).stopAsyncTask();
                    }
                }
            });
            pd.show();
        }
    }

    @Override
    protected List<?> doInBackground(String... params) {
        List<Annotation> messages = new ArrayList<Annotation>();
        XmlPullParser pull = Xml.newPullParser();
        Annotation annotation = null;
        try {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Device-Info", Build.MODEL+" ("+Build.PRODUCT+");"+ CondroidActivity.getUniqueDeviceIdentifier(parentActivity));
                if(params.length > 1) {
                    conn.setRequestProperty("If-Modified-Since",params[1]);
                    conn.setRequestProperty("X-If-Count-Not-Match",params[2]);
                }
                InputStream is = conn.getInputStream();

                try {
                    int s = Integer.parseInt(conn.getHeaderField("Content-Length"));
                    if(s < 150) {
                        resultCode = 1;
                        return messages;
                    }
                } catch (NumberFormatException e) {

                }
                String fullSign = conn.getHeaderField("X-Full-Update");
                if(fullSign != null && fullSign.trim().equals("1")) {
                    resultCode = 2;
                }

                pull.setInput(is, null);

            } catch (Exception ex) {
                throw new XMLProccessException("Stažení seznamu anotací se nezdařilo.", ex);
            }
            int eventType;
            this.publishProgress(-1);
            try {
                eventType = pull.getEventType();
            } catch (XmlPullParserException e) {
                resultCode = -1;
                throw new XMLProccessException("Zpracování seznamu anotací se nezdařilo", e);
            }
            try {
                int counter = 0;
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(this.isCancelled()) {
                        Log.d("Condroid", "DataLoader cancel");
                        return null;
                    }
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;


                        case XmlPullParser.START_TAG:
                            String name = pull.getName();
                            if (name.equalsIgnoreCase("annotations")) {
                                if (pull.getAttributeCount() > 0) {
                                    for (int i = 0; i < pull.getAttributeCount(); i++) {
                                        if (pull.getAttributeName(i).equalsIgnoreCase("count")) {
                                            try {
                                                int x = Integer.parseInt(pull.getAttributeValue(i));
                                                if (x > 0)
                                                    this.publishProgress(0, x);
                                            } catch (NumberFormatException e) {
                                            }
                                        }
                                        if (pull.getAttributeName(i).equalsIgnoreCase("last-update")) {
                                            DateTimeFormatter format = ISODateTimeFormat
                                                    .dateTimeNoMillis();
                                            try {
                                                DataProvider.getInstance(parentActivity).getCon().setLastUpdate(format.parseDateTime(pull.getAttributeValue(i).trim()).toDate());
                                            } catch (Exception e) {
                                                Log.e("Condroid", "Last update parse", e);
                                            }

                                        }

                                    }
                                }
                            }
                            if (name.equalsIgnoreCase("programme")) {
                                annotation = new Annotation();
                            } else {
                                if (name.equalsIgnoreCase("pid")) {
                                    annotation.setPid(pull.nextText().trim());
                                }
                                if (name.equalsIgnoreCase("author")) {
                                    annotation.setAuthor(pull.nextText().trim());
                                }
                                if (name.equalsIgnoreCase("title")) {
                                    annotation.setTitle(Html.fromHtml(pull.nextText().trim()).toString());
                                }
                                if (name.equalsIgnoreCase("type")) {
                                    annotation.setType(pull.nextText().trim());
                                }
                                if (name.equalsIgnoreCase("program-line")) {
                                    annotation.setProgramLine(pull.nextText().trim());
                                }
                                if (name.equalsIgnoreCase("location")) {
                                    annotation.setLocation(pull.nextText().trim());
                                }
                                if (name.equalsIgnoreCase("annotation")) {
                                    annotation.setAnnotation(Html.fromHtml(pull.nextText().trim()).toString());
                                }
                                if (name.equalsIgnoreCase("start-time")) {
                                    annotation.setStartTime(pull.nextText().trim());
                                }
                                if (name.equalsIgnoreCase("end-time")) {
                                    annotation.setEndTime(pull.nextText().trim());
                                }
                            }
                            break;

                        case XmlPullParser.END_TAG:
                            name = pull.getName();
                            if (name.equalsIgnoreCase("programme")
                                    && annotation != null) {
                                messages.add(annotation);
                                this.publishProgress(counter++);
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = pull.next();

                }
            } catch (Exception e) {
                resultCode = -1;
                throw new XMLProccessException("Zpracování zdroje se nezdařilo.", e);

            }
        } catch (XMLProccessException e) {
            resultCode = -1;
            Log.e("Condroid", "Exception during XML data recieve.", e);
            throw e;
        }

        return messages;

    }


}
