package cz.quinix.condroid;

import java.util.List;

import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import retrofit.http.GET;
import retrofit.http.Path;

public interface CondroidApi {

    @GET("/event")
    List<Convention> listEvents();

    @GET("/program/{id}")
    List<Annotation> listAnnotations(@Path("id") int id);
}
