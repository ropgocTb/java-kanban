package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class LocalTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm");

    @Override
    public void write(JsonWriter writer, final LocalDateTime time) throws IOException {
        if (time != null)
            writer.value(time.format(timeFormatter));
        else
            writer.nullValue();
    }

    @Override
    public LocalDateTime read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return LocalDateTime.parse(reader.nextString(), timeFormatter);
    }
}
