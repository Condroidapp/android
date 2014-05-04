package cz.quinix.condroid;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cz.quinix.condroid.model.Annotation;
import cz.quinix.condroid.model.Convention;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Path;

public interface CondroidApi {

    @GET("/event")
    List<Convention> listEvents();

    @GET("/program/{id}")
    Map<String, List<Annotation>> listAnnotations(@Path("id") int id, @Header("If-Modified-Since") String ifModifiedSince);
}
