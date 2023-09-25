package service;

import entility.Epic;
import entility.SubTask;
import entility.Task;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface  TaskManager {
    List<Task> getAllTasks();
    void saveTask(Task task);
    void updateTask(Task task);
    void deleteAllTask();
    Task getTaskById(int id);
    void deleteTaskById(int id);
    List <Epic> getAllEpics();
    void saveEpic(Epic epic);
    void updateEpic(Epic epic);
    List <SubTask> subTasksByEpic(Epic epic);
    void deleteAllEpic();
    Epic getEpicById(int id);
    void deleteEpicById(int id);
    void statusEpic(Epic epic);
    List <SubTask> getAllSubTasks();
    void saveSubTask(SubTask subTask);
    void updateSubTask(SubTask subTask);
    void deleteAllSubTask();
    SubTask getSubTaskById(int id);
    void deleteSubTaskById(int id);
    Integer numberId();
    List<Task> getHistory();
    void setEpicMemory(HashMap<Integer, Epic> epicMemory);
    void setSubTaskMemory(HashMap<Integer, SubTask> subTaskMemory);
    HashMap<Integer, SubTask> getSubTaskMemory();
    HashMap<Integer, Task> getTaskMemory();
    void setTaskMemory(HashMap<Integer, Task> taskMemory);
    HashMap<Integer, Epic> getEpicMemory();
    List<Task> getPrioritizedTask();
    void startAndEndTimeEpic(Epic epic);
    LocalDateTime numberDataByTaskNotData();
    LocalDateTime getNumberDate();

    Map<LocalDateTime, Task> getPrioritizedTasks();
    void validate(Task task);
}
