package cz.quinix.condroid.ui.adapters;

import java.util.List;

public interface IAdapterDataProvider<Type> {

	List<Type> getData(int page);

}
