package service;

import entility.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history = new CustomLinkedList();
    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }
    @Override
    public void addHistory(Task task) {
        if (history.map.containsKey(task.getId())) {
            history.removeNode(history.map.get(task.getId()));
        }
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.map.get(id));
    }
}