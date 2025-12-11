package project.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
}
