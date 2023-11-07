import client.KVTaskClient;
import com.google.gson.Gson;
import entility.Epic;
import entility.Status;
import entility.SubTask;
import entility.Task;
import server.KVServer;
import service.Managers;
import service.TaskManager;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        /*TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        Task task3 = new Task("Задача №3", "Описание задачи №3",
                "25.08.2023 16:00", 20);
        Epic epic1 = new Epic("Эпика №1", "Описание эпика №1");
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                3,"29.08.2023 12:30", 240);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                3,"24.08.2023 13:00", 20);
        Epic epic2 = new Epic("Эпика №2", "Описание эпика №2");
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 3);
        Gson gson = new Gson();
        String nado = gson.toJson(task1);
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.saveEpic(epic2);
        taskManager.saveSubTask(subTask3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getHistory());
        System.out.println(taskManager.getPrioritizedTask());
        taskManager.saveTask(task3);
        System.out.println(nado);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        System.out.println(taskManager.getHistory());
        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(2);
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getHistory());*/
    }
}
