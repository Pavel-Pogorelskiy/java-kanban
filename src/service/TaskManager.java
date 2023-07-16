package service;

import entility.Epic;
import entility.SubTask;
import entility.Task;

import java.util.ArrayList;
import java.util.List;

public interface  TaskManager {
    ArrayList<Task> getAllTasks();
    void saveTask(Task task);
    void updateTask(Task task);
    void deleteAllTask();
    Task getTaskById(int id);
    void deleteTaskById(int id);
    ArrayList <Epic> getAllEpics();
    void saveEpic(Epic epic);
    void updateEpic(Epic epic);
    ArrayList <SubTask> subTasksByEpic(Epic epic);
    void deleteAllEpic();
    Epic getEpicById(int id);
    void deleteEpicById(int id);
    void statusEpic(Epic epic);
    ArrayList <SubTask> getAllSubTasks();
    void saveSubTask(SubTask subTask);
    void updateSubTask(SubTask subTask);
    void deleteAllSubTask();
    SubTask getSubTaskById(int id);
    void deleteSubTaskById(int id);
    Integer numberId();

    List<Task> getHistory();
}
