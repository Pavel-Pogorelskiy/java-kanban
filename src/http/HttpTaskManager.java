package http;

import client.KVTaskClient;
import com.google.gson.Gson;
import entility.Epic;
import entility.SubTask;
import entility.Task;
import server.KVServer;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTasksManager {
    private static final String TASK_KEY = "task";
    private static final String EPIC_KEY = "epic";
    private static final String SUBTASK_KEY = "subtask";
    private static final String HISTORY_KEY = "history";
    private KVTaskClient client;
    private Gson gson;
    public static void main(String[] args) throws IOException {
        KVServer server = new KVServer();
        server.start();
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        Task task3 = new Task("Задача №3", "Описание задачи №3",
                "25.08.2023 16:00", 20);
        Epic epic1 = new Epic("Эпика №1", "Описание эпика №1");
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                3, "29.08.2023 13:00", 240);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 3);
        SubTask subTask4 = new SubTask("Подзадача 3", "Описание подзадачи 3",
                3, "01.09.2023 13:00", 60);
        Epic epic2 = new Epic("Эпика №2", "Описание эпика №2");
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 6);
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
        taskManager.load();
    }

    public HttpTaskManager(int port) {
        super(null);
        this.client = new KVTaskClient(port);
        gson = Managers.getGson();
    }

    @Override
    public void save() {
        client.save(TASK_KEY, gson.toJson(getAllTasks()));
        client.save(EPIC_KEY, gson.toJson(getAllEpics()));
        client.save(SUBTASK_KEY, gson.toJson(getAllSubTasks()));
        List<Integer> historyIds = historyManager.getHistory().stream()
                .map(Task::getId)
                .collect(Collectors.toList());
        client.save(HISTORY_KEY, gson.toJson(historyIds));
    }

    public void load() {
        String tasksJson = client.load(TASK_KEY);
        String [] tasks = tasksJson.split("},");
        for (int i = 0; i < tasks.length; i++) {
            StringBuilder str = new StringBuilder(tasks[i]);
            if (i == 0) {
                str.delete(0,1);
            }
            if (i == tasks.length - 1) {
                str.delete(str.length() - 2,str.length());
            }
            str.append("}");
            Task task = gson.fromJson(str.toString(), Task.class);
            taskMemory.put(task.getId(), task);
            prioritizedTasks.put(task.getStartTime(), task);
        }
        String epicsJson = client.load(EPIC_KEY);
        String [] epics = epicsJson.split("},");
        for (int i = 0; i < epics.length; i++) {
            StringBuilder str = new StringBuilder(epics[i]);
            if (i == 0) {
                str.delete(0,1);
            }
            if (i == epics.length - 1) {
                str.delete(str.length() - 2,str.length());
            }
            str.append("}");
            Epic epic = gson.fromJson(str.toString(), Epic.class);
            epicMemory.put(epic.getId(), epic);
        }
        String subTasksJson = client.load(SUBTASK_KEY);
        String [] subTasks = subTasksJson.split("},");
        for (int i = 0; i < subTasks.length; i++) {
            StringBuilder str = new StringBuilder(subTasks[i]);
            if (i == 0) {
                str.delete(0,1);
            }
            if (i == subTasks.length - 1) {
                str.delete(str.length() - 2,str.length());
            }
            str.append("}");
            SubTask subTask = gson.fromJson(str.toString(), SubTask.class);
            subTaskMemory.put(subTask.getId(), subTask);
            prioritizedTasks.put(subTask.getStartTime(), subTask);
        }
        String historyJson = client.load(HISTORY_KEY);
        StringBuilder str = new StringBuilder(historyJson);
        str.delete(0,1);
        str.delete(str.length() - 1,str.length());
        String [] idStories = str.toString().split(",");
        for (int i = 0; i < idStories.length; i++) {
            if (taskMemory.containsKey(Integer.parseInt(idStories[i]))) {
                historyManager.addHistory(taskMemory.get(Integer.parseInt(idStories[i])));
            } else if (epicMemory.containsKey(Integer.parseInt(idStories[i]))) {
                historyManager.addHistory(epicMemory.get(Integer.parseInt(idStories[i])));
            } else {
                historyManager.addHistory(subTaskMemory.get(Integer.parseInt(idStories[i])));
            }
        }
    }
}
