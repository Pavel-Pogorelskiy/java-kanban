import entility.Epic;
import entility.Status;
import entility.SubTask;
import entility.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача №1", "Описание задачи №1");
        Task task2 = new Task("Задача №2", "Описание задачи №2");
        Epic epic1 = new Epic("Эпика №1", "Описание эпика №1");
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", 3);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 3);
        Epic epic2 = new Epic("Эпика №2", "Описание эпика №2");
        SubTask subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 6);
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
        /*Task updatedTask1 = taskManager.getTaskById(1);
        updatedTask1.setStatus(Status.DONE);
        taskManager.updateTask(updatedTask1);
        SubTask updatedSubTask1 = taskManager.getSubTaskById(4);
        updatedSubTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(updatedSubTask1);
        SubTask updatedSubTask3 = taskManager.getSubTaskById(7);
        updatedSubTask3.setStatus(Status.DONE);
        taskManager.updateSubTask(updatedSubTask3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());
        taskManager.deleteTaskById(1);
        taskManager.deleteEpicById(3);
        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubTasks());*/
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(1);
        taskManager.getSubTaskById(4);
        taskManager.getTaskById(5);
        taskManager.getEpicById(3);
        taskManager.getTaskById(2);
        taskManager.getTaskById(7);
        taskManager.getSubTaskById(6);
        taskManager.getTaskById(1);
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(2);
        taskManager.getTaskById(5);
        taskManager.getTaskById(3);
        taskManager.getTaskById(7);
        taskManager.getEpicById(3);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(4);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(5);
        System.out.println(taskManager.getHistory());
    }
}
