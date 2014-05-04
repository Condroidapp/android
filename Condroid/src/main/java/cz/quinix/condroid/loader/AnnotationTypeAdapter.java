package cz.quinix.condroid.loader;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import cz.quinix.condroid.model.AnnotationType;

/**
 * Created by Jan on 4. 5. 2014.
 */
public class AnnotationTypeAdapter implements JsonDeserializer<AnnotationType> {
    @Override
    public AnnotationType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsJsonPrimitive().getAsString();

        String[] types = value.trim().split("\\+");
        AnnotationType result = new AnnotationType();

        if (types.length > 0) {
            result.mainType = types[0].trim();
        }
        if (types.length > 1) {
            for (int i = 1; i < types.length; i++) {
                result.secondaryTypes.add(types[i].trim());
            }
        }
        return result;
    }
}
