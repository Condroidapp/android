package cz.quinix.condroid.ui.adapters;

import java.util.List;

/**
 * Created by Jan on 8. 5. 2014.
 */
public interface IAdapterDataProvider<Type> {

    List<Type> getData(int page);

}
