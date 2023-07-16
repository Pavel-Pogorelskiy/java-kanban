package service;
import entility.Epic;
import entility.Status;
import entility.SubTask;
import entility.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private HashMap<Integer, SubTask> subTaskMemory = new HashMap<>();
    private HashMap<Integer, Task> taskMemory = new HashMap<>();
    private HashMap<Integer, Epic> epicMemory = new HashMap<>();

    @Override
    public Integer numberId() {
        id++;
        return id;
    }

    // Методы для Task

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList(taskMemory.values());
    }

    @Override
    public void saveTask(Task task) {
        task.setId(numberId());
        taskMemory.put(task.getId(), task);
    }

    @Override
    public void updateTask(Task task) {
        if (taskMemory.get(task.getId()) == null) {
            return;
        }
        taskMemory.put(task.getId(), task);
    }

    @Override
    public void deleteAllTask() {
        taskMemory.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addHistory(taskMemory.get(id));
        return taskMemory.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        taskMemory.remove(id);
    }

    // Методы для Epic

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList(epicMemory.values());
    }

    @Override
    public void saveEpic(Epic epic) {
        epic.setId(numberId());
        epicMemory.put(epic.getId(), epic);
        statusEpic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicMemory.get(epic.getId()) == null) {
            return;
        }
        epicMemory.put(epic.getId(), epic);
    }

    @Override
    public ArrayList<SubTask> subTasksByEpic(Epic epic) {
        int epicId = epic.getId();
        ArrayList<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : subTaskMemory.values()) {
            if (epicId == subTask.getEpicId()) {
                subTasks.add(subTask);
            }
        }
        return subTasks;
    }

    @Override
    public void deleteAllEpic() {
        epicMemory.clear();
        subTaskMemory.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        subTaskMemory.get(epicMemory.get(id));
        historyManager.addHistory(epicMemory.get(id));
        return epicMemory.get(id);
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epicMemory.get(id);
        ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
        for (int i = 0; i < subTaskIds.size(); i++) {
            int subTaskId = subTaskIds.get(i);
            subTaskMemory.remove(subTaskId);
        }
        epicMemory.remove(id);
    }

    @Override
    public void statusEpic(Epic epic) {
        if (epic.getSubtaskIds() == null || epic.getSubtaskIds().size() == 0) {
            epic.setStatus(Status.NEW);
        } else {
            ArrayList<Status> status = new ArrayList<>();
            ArrayList<Integer> subTaskId = epic.getSubtaskIds();
            for (int i = 0; i < subTaskId.size(); i++) {
                SubTask subTask = subTaskMemory.get(subTaskId.get(i));
                status.add(subTask.getStatus());
            }
            if (status.contains(Status.IN_PROGRESS) || (status.contains(Status.NEW) && status.contains(Status.DONE))) {
                epic.setStatus(Status.IN_PROGRESS);
            } else if (status.contains(Status.NEW)) {
                epic.setStatus(Status.NEW);
            } else {
                epic.setStatus(Status.DONE);
            }
        }
    }

    // Методы для SubTask

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList(subTaskMemory.values());
    }

    @Override
    public void saveSubTask(SubTask subTask) {
        subTask.setId(numberId());
        subTaskMemory.put(subTask.getId(), subTask);
        Epic epic = epicMemory.get(subTask.getEpicId());
        ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
        if (subTaskIds == null) {
            ArrayList<Integer> id = new ArrayList<>();
            id.add(subTask.getId());
            epic.setSubtaskIds(id);
            statusEpic(epic);
        } else {
            subTaskIds.add(subTask.getId());
            statusEpic(epic);
        }

    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTaskMemory.get(subTask.getId()) == null) {
            return;
        }
        subTaskMemory.put(subTask.getId(), subTask);
        statusEpic(epicMemory.get(subTask.getEpicId()));
    }

    @Override
    public void deleteAllSubTask() {
        for (Epic epic : epicMemory.values()) {
            ArrayList<Integer> subTaskId = epic.getSubtaskIds();
            subTaskId.clear();
            statusEpic(epic);
        }
        subTaskMemory.clear();
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.addHistory(subTaskMemory.get(id));
        return subTaskMemory.get(id);
    }

    @Override
    public void deleteSubTaskById(int id) {
        SubTask subTask = subTaskMemory.get(id);
        Epic epic = epicMemory.get(subTask.getEpicId());
        ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
        subTaskIds.removeIf(n -> n == id);
        subTaskMemory.remove(id);
        statusEpic(epic);
    }
    @Override
    public List <Task> getHistory() {
        return historyManager.getHistory();
    }
}
