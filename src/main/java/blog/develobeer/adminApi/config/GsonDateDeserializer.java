package blog.develobeer.adminApi.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// UTC로 노출하기 위해 사용한다.
public class GsonDateDeserializer implements JsonSerializer<Date>,JsonDeserializer<Date> {

    private final DateFormat dateFormat;

    public GsonDateDeserializer() {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override public synchronized JsonElement serialize(Date date,Type type,JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(dateFormat.format(date));
    }

    @Override public synchronized Date deserialize(JsonElement jsonElement,Type type,JsonDeserializationContext jsonDeserializationContext) {
        try {
            return dateFormat.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}