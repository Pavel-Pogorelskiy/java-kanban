package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import entility.Epic;
import entility.SubTask;
import entility.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.KVServer;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManagerTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest <HttpTaskManager> {
    KVServer server;
    Gson gson = Managers.getGson();

    @BeforeEach
    void startKVServer() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = (HttpTaskManager) Managers.getDefault();
        init();
    }

    @AfterEach
    void stopKVServer() {
        server.stop();
    }

    @Test
    void save() {
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.saveEpic(epic2);
        taskManager.saveSubTask(subTask3);
        taskManager.saveSubTask(subTask4);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        String allTaskJson = gson.toJson(taskManager.getAllTasks());
        String allSubTaskJson = gson.toJson(taskManager.getAllSubTasks());
        String allEpicJson = gson.toJson(taskManager.getAllEpics());
        List<Integer> idHistory = taskManager.getHistory().stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        String history = gson.toJson(idHistory);
        int trueNumber = 0;
        for (String jsonText : server.getData().values()) {
            if (allTaskJson.equals(jsonText) || allSubTaskJson.equals(jsonText) || allEpicJson.equals(jsonText)
                    || history.equals(jsonText)) {
                trueNumber++;
            }
        }
        assertTrue(trueNumber == 4,"Отличаются данные вводные с сохраненными на сервере");
        }

    @Test
    void load() {
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.saveEpic(epic2);
        taskManager.saveSubTask(subTask3);
        taskManager.saveSubTask(subTask4);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        List <Task> tasks = taskManager.getAllTasks();
        List <SubTask> subTasks = taskManager.getAllSubTasks();
        List <Epic> epics = taskManager.getAllEpics();
        List <Task> history = taskManager.getHistory();
        taskManager.getTaskMemory().clear();
        taskManager.getSubTaskMemory().clear();
        taskManager.getEpicMemory().clear();
        taskManager.getHistoryManager().getHistory().clear();
        taskManager.load();
        assertEquals(tasks, taskManager.getAllTasks(), "Неправильная загрузка задач");
        assertEquals(subTasks, taskManager.getAllSubTasks(), "Неправильная загрузка подзадач");
        assertEquals(epics, taskManager.getAllEpics(), "Неправильная загрузка эпиков");
        assertEquals(history, taskManager.getHistory(), "Неправильная загрузка истории");
    }
}