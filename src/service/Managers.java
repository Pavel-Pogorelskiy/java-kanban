package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import http.HttpTaskManager;

import java.io.File;
import java.time.LocalDateTime;

public final class Managers {
    private Managers() {

    }

    public static TaskManager getDefault() {
        return new HttpTaskManager(8080);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultBackedTask() {
        return new FileBackedTasksManager(new File("src/resource/BackedInformation.csv"));
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDataTimeAdapter());
        return gsonBuilder.create();
    }
}
