package cz.quinix.condroid.loader;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;

import java.util.List;
import java.util.Map;

public class DataLoader extends AProgressedTask<Integer, Map<String, List<Annotation>>> {

    //private ProgressDialog pd;

    private int resultCode = 0;
    private Convention convention;
    private String lastUpdate;
    //private int pdActual;

    public DataLoader(ITaskListener listener, SherlockFragmentActivity parent, Convention convention, String lastUpdate) {
        super(listener, parent);
        this.convention = convention;
        this.lastUpdate = lastUpdate;
        pdString = parent.getString(R.string.loading);
        this.showDialog();
    }

    public int getResultCode() {
        return this.resultCode;
    }

    protected List<?> doInBackground(String... params) {
        /*List<Annotation> messages = new ArrayList<Annotation>();
        XmlPullParser pull = Xml.newPullParser();
        Annotation annotation = null;
        try {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("X-Device-Info",CondroidActivity.getDeviceInfoString(parentActivity));
                if (params.length > 1 && params[1] != null) {
                    conn.setRequestProperty("If-Modified-Since", params[1]);
                    conn.setRequestProperty("X-If-Count-Not-Match", params[2]);
                }
                InputStream is = conn.getInputStream();

                try {
                    int s = Integer.parseInt(conn.getHeaderField("Content-Length"));
                    if (s < 150) {
                        resultCode = 1;
                        return messages;
                    }
                } catch (NumberFormatException e) {
                }
                if(conn.getHeaderField("Content-Type") == null || !conn.getHeaderField("Content-Type").contains("text/xml")) {
                    throw new IOException();
                }
                String fullSign = conn.getHeaderField("X-Full-Update");
                if (fullSign != null && fullSign.trim().equals("1")) {
                    resultCode = 2;
                }

                pull.setInput(is, null);

            } catch (IOException ex) {
                Log.e("Condroid", "No connection", ex);
                throw new XMLProccessException("Nelze se připojit k datovému zdroji. Jste připojeni k internetu?", ex);
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
            int counter = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (this.isCancelled()) {
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
                                annotation.setStart(pull.nextText().trim());
                            }
                            if (name.equalsIgnoreCase("end-time")) {
                                annotation.setEnd(pull.nextText().trim());
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
            Log.e("Condroid", "Annotations download", e);
            backgroundException = e;
            return null;
        }


        return messages;*/
        return null;

    }


    @Override
    public Map<String, List<Annotation>> call() throws Exception {
        CondroidApi service = getCondroidService();
        return service.listAnnotations(this.convention.getId(), lastUpdate);
    }
}
