package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter writer, final Duration duration) throws IOException {
        if (duration != null)
            writer.value(duration.toMinutes());
        else
            writer.nullValue();
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return Duration.ofMinutes(Integer.parseInt(reader.nextString()));
    }
}
