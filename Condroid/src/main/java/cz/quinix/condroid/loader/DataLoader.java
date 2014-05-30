package cz.quinix.condroid.loader;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.R;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import retrofit.RetrofitError;

import java.util.List;
import java.util.Map;

public class DataLoader extends AProgressedTask<Integer, Map<String, List<Annotation>>> {

    //private ProgressDialog pd;

    public int responseCode = 200;
    private Convention convention;
    private String lastUpdate;
    //private int pdActual;

    public DataLoader(ITaskListener listener, Activity parent, Convention convention, String lastUpdate) {
        super(listener, parent);
        this.convention = convention;
        this.lastUpdate = lastUpdate;
        pdString = parent.getString(R.string.loading);
        this.showDialog();
    }

    @Override
    public Map<String, List<Annotation>> call() throws Exception {
        CondroidApi service = getCondroidService();
        if(this.lastUpdate != null) {
            try {
                return service.listAnnotations(this.convention.getId(), lastUpdate);
            } catch (RetrofitError e) {
                if(e.getResponse() != null && e.getResponse().getStatus() == 304) {
                    return null;
                }
                throw  e;
            }
        }
        return service.listAnnotations(this.convention.getId());
    }

}
