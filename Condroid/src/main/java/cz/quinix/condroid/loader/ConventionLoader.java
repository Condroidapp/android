package cz.quinix.condroid.loader;

import java.util.List;

import cz.quinix.condroid.CondroidApi;
import cz.quinix.condroid.abstracts.AListenedAsyncTask;
import cz.quinix.condroid.abstracts.ITaskListener;
import cz.quinix.condroid.model.Convention;

public class ConventionLoader extends AListenedAsyncTask<Void, List<Convention>> {

    public ConventionLoader(ITaskListener listener) {
        super(listener);
    }

    @Override
    public List<Convention> call() throws Exception {
        CondroidApi service = getCondroidService();
        return service.listEvents();
    }




}
