package cz.quinix.condroid.ui.adapters;

import java.util.List;

import cz.quinix.condroid.model.Annotation;

/**
 * Created by Jan on 8. 5. 2014.
 */
public interface IAdapterDataProvider<Type> {

    List<Type> getData(int page);

}
