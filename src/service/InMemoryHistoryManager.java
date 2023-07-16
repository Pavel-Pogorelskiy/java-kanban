package service;

import entility.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int MEMORY_HISTORY = 10;
    private final List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return List.copyOf(history);
    }

    @Override
    public void addHistory(Task task) {
        if (task == null) {
            return;
        }
        if (history.size() >= MEMORY_HISTORY) {
            history.remove(0);
        }
        history.add(task);
    }
}
