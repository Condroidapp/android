package cz.quinix.condroid.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jan on 4. 5. 2014.
 */
public class AnnotationType implements Serializable {

    public String mainType = "";
    public List<String> secondaryTypes = new ArrayList<String>();

}
