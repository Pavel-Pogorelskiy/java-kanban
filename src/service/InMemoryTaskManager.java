package service;
import entility.*;
import exception.TaskValidationException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected int id;
    protected LocalDateTime numberDate = LocalDateTime.of(3000, 01, 01, 0, 0);
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected HashMap<Integer, SubTask> subTaskMemory = new HashMap<>();
    protected HashMap<Integer, Task> taskMemory = new HashMap<>();
    protected HashMap<Integer, Epic> epicMemory = new HashMap<>();
    protected Map<LocalDateTime, Task> prioritizedTasks = new TreeMap<>(LocalDateTime::compareTo);
    //protected Map<LocalDateTime, SubTask> prioritizedSubTasks = new TreeMap<>(LocalDateTime::compareTo);

    @Override
    public Integer numberId() {
        id++;
        return id;
    }

    @Override
    public LocalDateTime numberDataByTaskNotData() {
        numberDate = numberDate.plusMinutes(1);
        return numberDate;
    }

    @Override
    public List<Task> getPrioritizedTask() {
        //List<Task> prioritizedTask = Stream.concat(prioritizedTasks.values().stream(),
                        //prioritizedSubTasks.values().stream())
        List<Task> prioritizedTask = prioritizedTasks.values().stream()
                //.sorted((subTask1, subTask2) -> subTask1.getStartTime().compareTo(subTask2.getStartTime()))
                .map((task) -> {
                    if (task.getEndTime() == null) {
                        task.setStartTime(null);
                    }
                    return task;
                })
                .collect(Collectors.toList());
        return prioritizedTask;
    }
    // Методы для Task

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList(taskMemory.values());
    }
    @Override
    public void saveTask(Task task) {
        if (task == null) {
            throw new NullPointerException("Сохранение невозможно, т.к. Task равен null");
        } else {
            task.setId(numberId());
            validate(task);
            taskMemory.put(task.getId(), task);
            if (task.getStartTime() == null) {
                task.setStartTime(numberDataByTaskNotData());
                prioritizedTasks.put(task.getStartTime(), task);
            } else {
                prioritizedTasks.put(task.getStartTime(), task);
            }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (task != null) {
            if (taskMemory.get(task.getId()) == null) {
                return;
            }
            if ((!taskMemory.get(task.getId()).getStartTime().equals(task.getStartTime()))
            || (taskMemory.get(task.getId()).getDuration() == task.getDuration())) {
                prioritizedTasks.remove(taskMemory.get(task.getId()).getStartTime());
                validate(task);
            }
            prioritizedTasks.put(task.getStartTime(), task);
            taskMemory.put(task.getId(), task);
        }
    }

    @Override
    public void deleteAllTask() {
        for (Task task : taskMemory.values()) {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task.getStartTime());
        }
        taskMemory.clear();
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.addHistory(taskMemory.get(id));
        return taskMemory.get(id);
    }

    @Override
    public void deleteTaskById(int id) {
        if (taskMemory.containsKey(id)) {
            prioritizedTasks.remove(taskMemory.get(id).getStartTime());
            taskMemory.remove(id);
            historyManager.remove(id);
        }
    }

    // Методы для Epic

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList(epicMemory.values());
    }

    @Override
    public void saveEpic(Epic epic) {
        if (epic == null) {
            throw new NullPointerException("Сохранение невозможно, т.к. Epic равен null");
        } else {
            epic.setId(numberId());
            epicMemory.put(epic.getId(), epic);
            statusEpic(epic);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            if (epicMemory.get(epic.getId()) == null) {
                return;
            }
            epicMemory.put(epic.getId(), epic);
        }
    }

    @Override
    public List<SubTask> subTasksByEpic(Epic epic) {
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
        for (Task epic : epicMemory.values()) {
            historyManager.remove(epic.getId());
        }
        for (Task subTask : subTaskMemory.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask.getStartTime());
        }
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
        if (epicMemory.containsKey(id)) {
            Epic epic = epicMemory.get(id);
            if (epic.getSubtaskIds() != null) {
                ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
                for (int i = 0; i < subTaskIds.size(); i++) {
                    int subTaskId = subTaskIds.get(i);
                    prioritizedTasks.remove(subTaskMemory.get(subTaskId).getStartTime());
                    subTaskMemory.remove(subTaskId);
                    historyManager.remove(subTaskId);
                }
            }
            epicMemory.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void statusEpic(Epic epic) {
        if (epic == null) {
            throw new NullPointerException("Расчитать статус невозможно, т.к. переданный  Epic равен null");
        } else {
            if (epic.getSubtaskIds() == null || epic.getSubtaskIds().size() == 0) {
                epic.setStatus(Status.NEW);
            } else {
                ArrayList<Status> status = new ArrayList<>();
                ArrayList<Integer> subTaskId = epic.getSubtaskIds();
                for (int i = 0; i < subTaskId.size(); i++) {
                    SubTask subTask = subTaskMemory.get(subTaskId.get(i));
                    status.add(subTask.getStatus());
                }
                if (status.contains(Status.IN_PROGRESS) || (status.contains(Status.NEW)
                        && status.contains(Status.DONE))) {
                    epic.setStatus(Status.IN_PROGRESS);
                } else if (status.contains(Status.NEW)) {
                    epic.setStatus(Status.NEW);
                } else {
                    epic.setStatus(Status.DONE);
                }
            }
        }
    }

    @Override
    public void startAndEndTimeEpic(Epic epic) {
        if (epic == null) {
            throw new NullPointerException("Расчитать время начала и конца невозможно, " +
                    "т.к. переданный  Epic равен null");
        } else if (epic.getSubtaskIds() == null) {
            epic.setEndTime(null);
            epic.setStartTime(null);
        } else {
            List<SubTask> subTasksStart = subTaskMemory.values().stream()
                    .filter((it) -> {
                        return (it.getEpicId() == epic.getId() && it.getEndTime() != null);
                    })
                    .collect(Collectors.toList());
            if (!subTasksStart.isEmpty()) {
                epic.setStartTime(subTasksStart.get(0).getStartTime());
                List<SubTask> subTasksEnd = subTasksStart.stream()
                        .sorted((subTask1, subTask2) -> subTask1.getEndTime().compareTo(subTask2.getEndTime()))
                        .collect(Collectors.toList());
                epic.setEndTime(subTasksEnd.get(subTasksEnd.size() - 1).getEndTime());
                Integer duration = subTasksStart.stream()
                        .map(it -> it.getDuration())
                        .collect(Collectors.summingInt(Integer::intValue));
                epic.setDuration(duration);
            }
        }
    }

    // Методы для SubTask

    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList(subTaskMemory.values());
    }

    @Override
    public void saveSubTask(SubTask subTask) {
        if (subTask == null) {
            throw new NullPointerException("Сохранение невозможно, т.к. SubTask равен null");
        } else if (!epicMemory.containsKey(subTask.getEpicId())) {
            throw new IllegalArgumentException("Сохранение невозможно, т.к. " +
                    "Epic c id =" + subTask.getEpicId() + " не существует");
        } else {
            subTask.setId(numberId());
            validate(subTask);
            if (subTask.getStartTime() == null) {
                subTask.setStartTime(numberDataByTaskNotData());
                prioritizedTasks.put(subTask.getStartTime(), subTask);
            } else {
                prioritizedTasks.put(subTask.getStartTime(), subTask);
            }
            subTaskMemory.put(subTask.getId(), subTask);
            Epic epic = epicMemory.get(subTask.getEpicId());
            ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
            if (subTaskIds == null) {
                ArrayList<Integer> id = new ArrayList<>();
                id.add(subTask.getId());
                epic.setSubtaskIds(id);
            } else {
                subTaskIds.add(subTask.getId());
            }
            statusEpic(epic);
            startAndEndTimeEpic(epic);
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask != null) {
            if (subTaskMemory.get(subTask.getId()) == null) {
                return;
            }
            if (!prioritizedTasks.get(subTaskMemory.get(subTask.getId()).getStartTime()).
                    equals(subTask.getStartTime()) || prioritizedTasks.get(subTaskMemory
                            .get(subTask.getId()).getStartTime()).getDuration() == (subTask.getDuration())) {
                prioritizedTasks.remove(subTask.getStartTime());
                validate(subTask);
            }
            prioritizedTasks.put(subTask.getStartTime(), subTask);
            subTaskMemory.put(subTask.getId(), subTask);
            statusEpic(epicMemory.get(subTask.getEpicId()));
        }
    }

    @Override
    public void deleteAllSubTask() {
        for (Epic epic : epicMemory.values()) {
            ArrayList<Integer> subTaskId = epic.getSubtaskIds();
            if (subTaskId == null) {
                continue;
            }
            subTaskId.clear();
            statusEpic(epic);
        }
        for (Task subTask : subTaskMemory.values()) {
            historyManager.remove(subTask.getId());
            prioritizedTasks.remove(subTask.getStartTime());
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
        if (subTaskMemory.containsKey(id)) {
            SubTask subTask = subTaskMemory.get(id);
            prioritizedTasks.remove(subTask.getStartTime());
            Epic epic = epicMemory.get(subTask.getEpicId());
            ArrayList<Integer> subTaskIds = epic.getSubtaskIds();
            subTaskIds.removeIf(n -> n == id);
            subTaskMemory.remove(id);
            statusEpic(epic);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void setEpicMemory(HashMap<Integer, Epic> epicMemory) {
        this.epicMemory = epicMemory;
    }

    @Override
    public void setSubTaskMemory(HashMap<Integer, SubTask> subTaskMemory) {
        this.subTaskMemory = subTaskMemory;
    }

    @Override
    public HashMap<Integer, SubTask> getSubTaskMemory() {
        return subTaskMemory;
    }

    @Override
    public HashMap<Integer, Task> getTaskMemory() {
        return taskMemory;
    }

    @Override
    public void setTaskMemory(HashMap<Integer, Task> taskMemory) {
        this.taskMemory = taskMemory;
    }

    @Override
    public HashMap<Integer, Epic> getEpicMemory() {
        return epicMemory;
    }
    @Override
    public void validate(Task task) {
        if (task.getStartTime() != null) {
            LocalDateTime startTime = task.getStartTime();
            LocalDateTime endTime = task.getEndTime();

            Integer result = prioritizedTasks.values().stream()
                    .filter(it -> it.getDuration() != null)
                    .map(it -> {
                        if (startTime.isBefore(it.getEndTime()) && endTime.isAfter(it.getStartTime())) {
                            return 1;
                        }
                        if (startTime.isAfter(it.getEndTime()) && endTime.isBefore(it.getStartTime())) {
                            return 1;
                        }
                        return 0;
                    })
                    .reduce(Integer::sum)
                    .orElse(0);
            if (result != 0) {
                throw new TaskValidationException("Сохранение (изменение) невозможно так как" +
                        " происходит пересечение с другими задачами");
            }
        }
    }

    public LocalDateTime getNumberDate() {
        return numberDate;
    }

    public Map<LocalDateTime, Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }
}
