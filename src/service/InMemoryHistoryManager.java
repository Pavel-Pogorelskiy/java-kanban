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
        if (history.getMap().containsKey(task.getId())) {
            history.removeNode(history.getMap().get(task.getId()));
        }
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.getMap().get(id));
    }
}