package entility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private TaskManager taskManager;
    private Epic epic1;
    private Epic epicNull;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        epic1.setId(1);
        epicNull = null;
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1", 1);
        subTask1.setId(2);
        subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2", 1);
        subTask2.setId(3);
        subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 1);
        subTask3.setId(4);
    }

    @Test
    public void StatusEpicToNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.statusEpic(epicNull)
        );
        assertEquals("Расчитать статус невозможно, т.к. переданный  Epic равен null", ex.getMessage());
    }

    @Test
    public void StatusEpicToNotSubtask() {
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        assertNull(epic1.getSubtaskIds(), "В эпике имеются SubTasks");
        taskManager.statusEpic(epic1);
        Assertions.assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void EpicToAllSubTaskNew() {
        HashMap <Integer, SubTask> subTasks = new HashMap<>();
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        subTasks.put(subTask3.getId(), subTask3);
        taskManager.setSubTaskMemory(subTasks);
        ArrayList <Integer> subTaskId = new ArrayList<>();
        subTaskId.add(subTask1.getId());
        subTaskId.add(subTask2.getId());
        subTaskId.add(subTask3.getId());
        epic1.setSubtaskIds(subTaskId);
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        taskManager.setEpicMemory(epic);
        assertNotNull(epic1.getSubtaskIds(), "В эпике не имеются SubTasks");
        taskManager.statusEpic(epic1);
        assertEquals(Status.NEW, epic1.getStatus());
    }

    @Test
    public void EpicToAllSubTaskDone() {
        HashMap <Integer, SubTask> subTasks = new HashMap<>();
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        subTask3.setStatus(Status.DONE);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        subTasks.put(subTask3.getId(), subTask3);
        taskManager.setSubTaskMemory(subTasks);
        ArrayList <Integer> subTaskId = new ArrayList<>();
        subTaskId.add(subTask1.getId());
        subTaskId.add(subTask2.getId());
        subTaskId.add(subTask3.getId());
        epic1.setSubtaskIds(subTaskId);
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        taskManager.setEpicMemory(epic);
        assertNotNull(epic1.getSubtaskIds(), "В эпике не имеются SubTasks");
        taskManager.statusEpic(epic1);
        assertEquals(Status.DONE, epic1.getStatus());
    }

    @Test
    public void EpicToSubTaskNewAndDone() {
        HashMap <Integer, SubTask> subTasks = new HashMap<>();
        subTask2.setStatus(Status.DONE);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        taskManager.setSubTaskMemory(subTasks);
        ArrayList <Integer> subTaskId = new ArrayList<>();
        subTaskId.add(subTask1.getId());
        subTaskId.add(subTask2.getId());
        epic1.setSubtaskIds(subTaskId);
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        taskManager.setEpicMemory(epic);
        assertNotNull(epic1.getSubtaskIds(), "В эпике не имеются SubTasks");
        taskManager.statusEpic(epic1);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void EpicToAllSubTaskInProgress() {
        HashMap <Integer, SubTask> subTasks = new HashMap<>();
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.IN_PROGRESS);
        subTask3.setStatus(Status.IN_PROGRESS);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        subTasks.put(subTask3.getId(), subTask3);
        taskManager.setSubTaskMemory(subTasks);
        ArrayList <Integer> subTaskId = new ArrayList<>();
        subTaskId.add(subTask1.getId());
        subTaskId.add(subTask2.getId());
        subTaskId.add(subTask3.getId());
        epic1.setSubtaskIds(subTaskId);
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        taskManager.setEpicMemory(epic);
        assertNotNull(epic1.getSubtaskIds(), "В эпике не имеются SubTasks");
        taskManager.statusEpic(epic1);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void EpicToSubTaskInProgressAndDone() {
        HashMap <Integer, SubTask> subTasks = new HashMap<>();
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask2.setStatus(Status.DONE);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        taskManager.setSubTaskMemory(subTasks);
        ArrayList <Integer> subTaskId = new ArrayList<>();
        subTaskId.add(subTask1.getId());
        subTaskId.add(subTask2.getId());
        epic1.setSubtaskIds(subTaskId);
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        taskManager.setEpicMemory(epic);
        assertNotNull(epic1.getSubtaskIds(), "В эпике не имеются SubTasks");
        taskManager.statusEpic(epic1);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }

    @Test
    public void EpicToSubTaskNewAndInProgress() {
        HashMap <Integer, SubTask> subTasks = new HashMap<>();
        subTask2.setStatus(Status.IN_PROGRESS);
        subTasks.put(subTask1.getId(), subTask1);
        subTasks.put(subTask2.getId(), subTask2);
        taskManager.setSubTaskMemory(subTasks);
        ArrayList <Integer> subTaskId = new ArrayList<>();
        subTaskId.add(subTask1.getId());
        subTaskId.add(subTask2.getId());
        epic1.setSubtaskIds(subTaskId);
        HashMap <Integer, Epic> epic = new HashMap<>();
        epic.put(epic1.getId(), epic1);
        taskManager.setEpicMemory(epic);
        assertNotNull(epic1.getSubtaskIds(), "В эпике не имеются SubTasks");
        taskManager.statusEpic(epic1);
        assertEquals(Status.IN_PROGRESS, epic1.getStatus());
    }
}