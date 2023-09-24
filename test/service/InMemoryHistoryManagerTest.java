package service;

import entility.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.Managers;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;
    private Task task4;
    private Task task5;


    @BeforeEach
    void setUp(){
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Задача №1", "Описание задачи №1");
        task1.setId(1);
        task2 = new Task("Задача №2", "Описание задачи №2");
        task2.setId(2);
        task3 = new Task("Задача №3", "Описание задачи №3");
        task3.setId(3);
        task4 = new Task("Задача №4", "Описание задачи №4");
        task4.setId(4);
        task5 = new Task("Задача №5", "Описание задачи №5");
        task5.setId(5);
    }


    @Test
    public void emptyListHistory() {
        List <Task> history = historyManager.getHistory();
        assertNotNull(history, "История - это null");
        assertTrue(history.isEmpty(), "История не пустая");
    }

    @Test
    public void haveListHistorytoOne() {
        historyManager.addHistory(task1);
        List <Task> history = historyManager.getHistory();
        assertNotNull(history, "История - это null");
        assertTrue(history.size() == 1, "История пустая");
    }

    @Test
    public void checkDoubleTaskHistory() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.addHistory(task4);
        historyManager.addHistory(task5);
        historyManager.addHistory(task2);
        List <Task> history = historyManager.getHistory();
        assertTrue(history.size() == 5, "Произошло дублироование");
        assertEquals(task2, history.get(4));
    }

    @Test
    public void deleteFirstHistoryTask() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.addHistory(task4);
        historyManager.addHistory(task5);
        historyManager.remove(task1.getId());
        List <Task> history = historyManager.getHistory();
        assertTrue(history.size() == 4, "Удаление не произошло");
        assertEquals(task2, history.get(0), "Не правильное удаление");
    }

    @Test
    public void deleteCentralHistoryTask() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.addHistory(task4);
        historyManager.addHistory(task5);
        historyManager.remove(task3.getId());
        List <Task> history = historyManager.getHistory();
        assertTrue(history.size() == 4, "Удаление не произошло");
        assertEquals(task4, history.get(2), "Не правильное удаление");
    }

    @Test
    public void deleteLastHistoryTask() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        historyManager.addHistory(task4);
        historyManager.addHistory(task5);
        historyManager.remove(task3.getId());
        List <Task> history = historyManager.getHistory();
        assertTrue(history.size() == 4, "Удаление не произошло");
        IndexOutOfBoundsException e = assertThrows(
                IndexOutOfBoundsException.class,
                () -> history.get(4));
        assertEquals("Index 4 out of bounds for length 4", e.getMessage());
    }
    @Test
    public void deleteNotTaskToHistory() {
        historyManager.addHistory(task1);
        historyManager.addHistory(task2);
        historyManager.addHistory(task3);
        assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(task4.getId());
        assertEquals(3, historyManager.getHistory().size());
    }

    @Test
    public void deleteTaskToClearHistory() {
        assertEquals(0, historyManager.getHistory().size());
        historyManager.remove(task4.getId());
        assertEquals(0, historyManager.getHistory().size());
    }
}