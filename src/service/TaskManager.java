package service;
import entility.Epic;
import entility.SubTask;
import entility.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int id;
    private HashMap<Integer, SubTask> subTaskMemory = new HashMap<>();
    private HashMap<Integer, Task> taskMemory = new HashMap<>();
    private HashMap<Integer, Epic> epicMemory = new HashMap<>();

    public Integer numberId() {
        id++;
        return id;
    }

    // Методы для Task

    public ArrayList <Task> getAllTasks() {
        return new ArrayList (taskMemory.values());
    }

    public void saveTask(Task task) {
        task.setId(numberId());
        taskMemory.put(task.getId(), task);
    }

    public void updateTask(Task task) {
        if (taskMemory.get(task.getId()) == null) {
            return;
        }
        taskMemory.put(task.getId(), task);
    }

    public void deleteAllTask() {
        taskMemory.clear();
    }

    public Task getTaskById(int id) {
        return taskMemory.get(id);
    }

    public void deleteTaskById(int id) {
        taskMemory.remove(id);
    }

    // Методы для Epic

    public ArrayList <Epic> getAllEpics() {
        return new ArrayList (epicMemory.values());
    }

    public void saveEpic(Epic epic) {
        epic.setId(numberId());
        epicMemory.put(epic.getId(), epic);
        statusEpic(epic);
    }

    public void updateEpic(Epic epic) {
        if (epicMemory.get(epic.getId()) == null) {
            return;
        }
        epicMemory.put(epic.getId(), epic);
    }

    public ArrayList <SubTask> subTasksByEpic(Epic epic) {
        int epicId = epic.getId();
        ArrayList <SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subTaskMemory.values()) {
            if (epicId == subTask.getEpicId()) {
                subTasks.add(subTask);
            }
        }
        return subTasks;
    }

    public void deleteAllEpic() {
        epicMemory.clear();
        subTaskMemory.clear();
    }

    public Epic getEpicById(int id) {
        return epicMemory.get(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epicMemory.get(id);
        ArrayList <Integer> subTaskIds = epic.getSubtaskIds();
        for (int i = 0; i < subTaskIds.size(); i++) {
            int subTaskId = subTaskIds.get(i);
            subTaskMemory.remove(subTaskId);
        }
        epicMemory.remove(id);
    }

    public void statusEpic(Epic epic) {
        if (epic.getSubtaskIds() == null) {
            epic.setStatus("NEW");
        } else {
            ArrayList<String> status = new ArrayList<>();
            ArrayList<Integer> subTaskId = epic.getSubtaskIds();
            for (int i = 0; i < subTaskId.size(); i++) {
            SubTask subTask = subTaskMemory.get(subTaskId.get(i));
            status.add(subTask.getStatus());
            }
            if (status.contains("IN_PROGRESS")||(status.contains("NEW")&&status.contains("DONE"))) {
                epic.setStatus("IN_PROGRESS");
            } else if (status.contains("NEW")) {
                epic.setStatus("NEW");
            } else {
                epic.setStatus("DONE");
            }
        }
    }

    // Методы для SubTask

    public ArrayList <SubTask> getAllSubTasks() {
        return new ArrayList (subTaskMemory.values());
    }

    public void saveSubTask(SubTask subTask) {
        subTask.setId(numberId());
        subTaskMemory.put(subTask.getId(), subTask);
        Epic epic = epicMemory.get(subTask.getEpicId());
        ArrayList <Integer> subTaskIds = epic.getSubtaskIds();
        if (subTaskIds == null) {
            ArrayList <Integer> id = new ArrayList<>();
            id.add(subTask.getId());
            epic.setSubtaskIds(id);
            statusEpic(epic);
        } else {
            subTaskIds.add(subTask.getId());
            statusEpic(epic);
        }

    }

    public void updateSubTask(SubTask subTask) {
        if (subTaskMemory.get(subTask.getId()) == null) {
            return;
        }
        subTaskMemory.put(subTask.getId(), subTask);
        statusEpic(epicMemory.get(subTask.getEpicId()));
    }

    public void deleteAllSubTask() {
        subTaskMemory.clear();
    }

    public SubTask getSubTaskById(int id) {
        return subTaskMemory.get(id);
    }

    public void deleteSubTaskById(int id) {
        SubTask subTask = subTaskMemory.get(id);
        int epicId = subTask.getEpicId();
        Epic epic = epicMemory.get(epicId);
        ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
        subTaskIds.removeIf(n -> n == id);
        subTaskMemory.remove(id);
        statusEpic(epic);
    }
}
