package service;

import entility.Epic;
import entility.SubTask;
import entility.Task;
import entility.Type;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefaultBackedTask();
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
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(7);
        System.out.println(taskManager.getHistory());
        TaskManager loadtTaskManager = FileBackedTasksManager.loadFromFile(
                new File("src/resource/BackedInformation.csv"));
        System.out.println(loadtTaskManager.getAllTasks());
        System.out.println(loadtTaskManager.getAllEpics());
        System.out.println(loadtTaskManager.getAllSubTasks());
        System.out.println(loadtTaskManager.getHistory());
    }

    private File file = new File("src/resource/BackedInformation.csv");

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(CSVFormatHandler.getHeader());
            writer.newLine();
            for (Task task : taskMemory.values()) {
                 writer.write(CSVFormatHandler.toString(task));
                 writer.newLine();
            }
            for (Task epic : epicMemory.values()) {
                writer.write(CSVFormatHandler.toString(epic));
                writer.newLine();
            }

            for (Task subTask : subTaskMemory.values()) {
                writer.write(CSVFormatHandler.toString(subTask));
                writer.newLine();
            }

            writer.newLine();
            writer.write(CSVFormatHandler.historyToString(historyManager));

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка записи файла");
        }
    }

    static FileBackedTasksManager loadFromFile(File file) throws IOException {
        FileBackedTasksManager manager = new FileBackedTasksManager();
        String content = Files.readString(Path.of(String.valueOf(file)));
        String[] textString = content.split("\n");
        for (int i = 1; i < textString.length; i++) {
            if (textString[i].isEmpty()) {
                List <Integer> history = CSVFormatHandler.historyFromString(textString[i + 1]);
                for (int j = 0; j < history.size(); j++) {
                    int id = history.get(j);
                    if (manager.taskMemory.containsKey(id)) {
                        manager.historyManager.addHistory(manager.taskMemory.get(id));
                    } else if (manager.epicMemory.containsKey(id)) {
                        manager.historyManager.addHistory(manager.epicMemory.get(id));
                    } else {
                        manager.historyManager.addHistory(manager.subTaskMemory.get(id));
                    }
                }
                break;
            } else {
                Task task = CSVFormatHandler.fromString(textString[i]);
                if (task.getType() == Type.TASK) {
                    manager.taskMemory.put(task.getId(), task);
                } else if (task.getType() == Type.EPIC) {
                    manager.epicMemory.put(task.getId(), (Epic) task);
                } else {
                    manager.subTaskMemory.put(task.getId(), (SubTask) task);
                }
            }
        }
        return manager;
    }

    @Override
    public Integer numberId() {
        return super.numberId();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteAllTask() {
        super.deleteAllTask();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public void saveEpic(Epic epic) {
        super.saveEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public List<SubTask> subTasksByEpic(Epic epic) {
         return super.subTasksByEpic(epic);
    }

    @Override
    public void deleteAllEpic() {
        super.deleteAllEpic();
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void statusEpic(Epic epic) {
        super.statusEpic(epic);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public void saveSubTask(SubTask subTask) {
        super.saveSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteAllSubTask() {
        super.deleteAllSubTask();
        save();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void deleteSubTaskById(int id) {
        super.deleteSubTaskById(id);
        save();
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}
