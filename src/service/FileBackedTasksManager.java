package service;

import entility.Epic;
import entility.SubTask;
import entility.Task;
import entility.Type;
import exception.ManagerSaveException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    /*public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultBackedTask();
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
        System.out.println(taskManager.getPrioritizedTask());
        TaskManager loadTaskManager = FileBackedTasksManager.loadFromFile(
                new File("src/resource/BackedInformation.csv"));
        System.out.println(loadTaskManager.getPrioritizedTask());
    }*/

    private File file ;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    protected void save() {
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
            throw new ManagerSaveException("Ошибка записи файла");
        }
    }

    static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String content = Files.readString(Path.of(String.valueOf(file)));
            String[] textString = content.split("\n");
            for (int i = 1; i < textString.length; i++) {
                if (textString[i].isEmpty()) {
                    List<Integer> history = CSVFormatHandler.historyFromString(textString[i + 1]);
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
                        manager.prioritizedTasks.put(task.getStartTime(), task);
                    } else if (task.getType() == Type.EPIC) {
                        manager.epicMemory.put(task.getId(), (Epic) task);
                    } else {
                        manager.subTaskMemory.put(task.getId(), (SubTask) task);
                        manager.prioritizedTasks.put(task.getStartTime(), (SubTask) task);
                        if (manager.epicMemory.containsKey(((SubTask) task).getEpicId())) {
                            if (manager.epicMemory.get(((SubTask) task).getEpicId()).getSubtaskIds() == null) {
                                ArrayList<Integer> idSubTask = new ArrayList<>();
                                idSubTask.add(task.getId());
                                manager.epicMemory.get(((SubTask) task).getEpicId()).setSubtaskIds(idSubTask);
                            } else {
                                ArrayList<Integer> idSubTasks = manager.epicMemory.get(((SubTask) task).
                                        getEpicId()).getSubtaskIds();
                                idSubTasks.add(task.getId());
                                manager.epicMemory.get(((SubTask) task).getEpicId()).setSubtaskIds(idSubTasks);
                            }
                        }
                    }
                }
            }
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения");
        }
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

    public void setFile(File file) {
        this.file = file;
    }
}
