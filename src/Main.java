import entility.Epic;
import entility.SubTask;
import entility.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        Task task1 = new Task("Задача №1", "Описание задачи №1");
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        Epic epic1 = new Epic("Эпика №1", "Описание эпика №1");
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", 3);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 3);
        Epic epic2 = new Epic("Эпика №2", "Описание эпика №2");
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 6);
        manager.saveTask(task1);
        manager.saveTask(task2);
        manager.saveEpic(epic1);
        manager.saveSubTask(subTask1);
        manager.saveSubTask(subTask2);
        manager.saveEpic(epic2);
        manager.saveSubTask(subTask3);
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        Task updatedTask1 = manager.getTaskById(1);
        updatedTask1.setStatus("DONE");
        manager.updateTask(updatedTask1);
        SubTask updatedSubTask1 = manager.getSubTaskById(4);
        updatedSubTask1.setStatus("DONE");
        manager.updateSubTask(updatedSubTask1);
        SubTask updatedSubTask3 = manager.getSubTaskById(7);
        updatedSubTask3.setStatus("DONE");
        manager.updateSubTask(updatedSubTask3);
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
        manager.deleteTaskById(1);
        manager.deleteEpicById(3);
        System.out.println(manager.getAllTasks());
        System.out.println(manager.getAllEpics());
        System.out.println(manager.getAllSubTasks());
    }
}
