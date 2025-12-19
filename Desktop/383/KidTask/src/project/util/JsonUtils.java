package project.util;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class JsonUtils {

    // LocalDate için özel ayar eklenmiş Gson oluşturucu
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    // --- GENERIC SAVE ---
    public static <T> void save(String filePath, List<T> data) {
        try (FileWriter writer = new FileWriter(filePath)) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- GENERIC LOAD ---
    public static <T> List<T> load(String filePath, Type type) {
        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    // --- Type helper for Lists ---
    public static <T> Type listOf(Class<T> cls) {
        return TypeToken.getParameterized(List.class, cls).getType();
    }

    // --- LocalDate Adapter (Hatayı çözen kısım) ---
    private static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.toString()); // YYYY-MM-DD olarak kaydet
        }

        @Override
        public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDate.parse(json.getAsString()); // String'den tekrar LocalDate yap
        }
    }
}