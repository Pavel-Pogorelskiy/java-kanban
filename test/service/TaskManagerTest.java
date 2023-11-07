package service;

import entility.Epic;
import entility.Status;
import entility.SubTask;
import entility.Task;
import org.junit.jupiter.api.Test;
import exception.TaskValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest <T extends TaskManager> {
    protected T taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Task taskNull;
    protected Epic epicNull;
    protected SubTask subTaskNull;
    protected Epic epic1;
    protected Epic epic2;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected SubTask subTask3;
    protected SubTask subTask4;


    protected void init() {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        task2 = new Task("Задача №2", "Описание задачи №2");
        task3 = new Task("Задача №3", "Описание задачи №3",
                "25.08.2023 16:00", 20);
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                3, "29.08.2023 13:00", 240);
        subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                3);
        subTask4 = new SubTask("Подзадача 3", "Описание подзадачи 3",
                3, "01.09.2023 13:00", 60);
        epic2 = new Epic("Эпика №2", "Описание эпика №2");
        subTask3 = new SubTask("Подзадача 3", "Описание подзадачи 3", 6);
        taskNull = null;
        epicNull = null;
        subTaskNull = null;
    }

    @Test
    void saveTaskNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.saveTask(taskNull)
        );
        assertEquals("Сохранение невозможно, т.к. Task равен null", ex.getMessage());
    }

    @Test
    void saveSubTaskNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.saveSubTask(subTaskNull)
        );
        assertEquals("Сохранение невозможно, т.к. SubTask равен null", ex.getMessage());
    }

    @Test
    void saveEpicNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.saveEpic(epicNull)
        );
        assertEquals("Сохранение невозможно, т.к. Epic равен null", ex.getMessage());
    }

    @Test
    void saveSubTaskToNoEpic() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> taskManager.saveSubTask(subTask1)
        );
        assertEquals("Сохранение невозможно, т.к. Epic c id =" + subTask1.getEpicId() + " не существует", ex.getMessage());
    }

    @Test
    void saveTask() {
        taskManager.saveTask(task1);
        assertTrue(taskManager.getTaskMemory().containsValue(task1), "Task не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(task1), "Task не сохраняется в TreeMap");
    }

    @Test
    void saveEpic() {
        taskManager.saveEpic(epic1);
        assertNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике имеются подзадачи");
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        assertTrue(taskManager.getEpicMemory().containsValue(epic1), "Epic не сохраняется в HashMap");
        assertNull(taskManager.getEpicMemory().get(1).getStartTime());
        assertNull(taskManager.getEpicMemory().get(1).getEndTime());
        assertNull(taskManager.getEpicMemory().get(1).getDuration());
    }

    @Test
    void saveSubTaskNEW() {
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertTrue(taskManager.getSubTaskMemory().containsValue(subTask1), "SubTask не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask1), "SubTask не сохраняется в TreeMap");
        assertNotNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике не имеются подзадачи");
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
    }

    @Test
    void saveSubTaskInProgress() {
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        subTask1.setStatus(Status.IN_PROGRESS);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertTrue(taskManager.getSubTaskMemory().containsValue(subTask1), "SubTask не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask1), "SubTask не сохраняется в TreeMap");
        assertNotNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике не имеются подзадачи");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
    }

    @Test
    void saveSubTaskDone() {
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        subTask1.setStatus(Status.DONE);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertTrue(taskManager.getSubTaskMemory().containsValue(subTask1), "SubTask не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask1), "SubTask не сохраняется в TreeMap");
        assertNotNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике не имеются подзадачи");
        assertEquals(Status.DONE, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
    }

    @Test
    void saveTwoSubTaskNEW() {
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertTrue(taskManager.getSubTaskMemory().containsValue(subTask1), "SubTask не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask1), "SubTask не сохраняется в TreeMap");
        assertNotNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике не имеются подзадачи");
        assertEquals(2, taskManager.getEpicMemory().get(1).getSubtaskIds().size());
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
    }

    @Test
    void saveTwoSubTaskNewAndDone() {
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        subTask1.setStatus(Status.DONE);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertTrue(taskManager.getSubTaskMemory().containsValue(subTask1), "SubTask не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask1), "SubTask не сохраняется в TreeMap");
        assertNotNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике не имеются подзадачи");
        assertEquals(2, taskManager.getEpicMemory().get(1).getSubtaskIds().size());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
    }

    @Test
    void saveTwoSubTaskDone() {
        taskManager.saveEpic(epic1);
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertTrue(taskManager.getSubTaskMemory().containsValue(subTask1), "SubTask не сохраняется в HashMap");
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask1), "SubTask не сохраняется в TreeMap");
        assertNotNull(taskManager.getEpicMemory().get(1).getSubtaskIds(), "В эпике не имеются подзадачи");
        assertEquals(2, taskManager.getEpicMemory().get(1).getSubtaskIds().size());
        assertEquals(Status.DONE, taskManager.getEpicMemory().get(1).getStatus(), "Не верный расчет статуса эпика");
    }

    @Test
    void getTaskById() {
        taskManager.saveTask(task1);
        assertEquals(task1, taskManager.getTaskById(1), "Получение не верной задачи по id");
    }

    @Test
    void getTaskByNotId() {
        taskManager.saveTask(task1);
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.getTaskById(2)
        );
    }

    @Test
    void getSubTaskById() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubTaskById(2), "Получение не верной задачи по id");
    }

    @Test
    void getSubTaskByNotId() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.getSubTaskById(1)
        );
    }

    @Test
    void getEpicById() {
        taskManager.saveEpic(epic1);
        assertEquals(epic1, taskManager.getEpicById(1), "Получение не верной задачи по id");
    }

    @Test
    void getEpicByNotId() {
        taskManager.saveEpic(epic1);
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.getEpicById(2)
        );
    }

    @Test
    void getAllTask() {
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        Task[] tasks = new Task[]{task1, task2};
        Task[] tasksMemory = taskManager.getAllTasks().toArray(new Task[0]);
        assertNotNull(taskManager.getAllTasks(), "Список пуст");
        assertEquals(2, tasksMemory.length, "Не совпадает количество сохарненных задач");
        assertArrayEquals(tasks, tasksMemory);
    }

    @Test
    void getAllNotTask() {
        assertEquals(0, taskManager.getAllTasks().size(), "Список не пуст");
    }

    @Test
    void getAllEpic() {
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        Epic[] epics = new Epic[]{epic1, epic2};
        Epic[] epicMemory = taskManager.getAllEpics().toArray(new Epic[0]);
        assertNotNull(taskManager.getAllEpics(), "Список пуст");
        assertEquals(2, epicMemory.length, "Не совпадает количество сохарненных задач");
        assertArrayEquals(epics, epicMemory);
    }

    @Test
    void getAllNotEpic() {
        assertEquals(0, taskManager.getAllEpics().size(), "Список не пуст");
    }

    @Test
    void getAllSubTasks() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        SubTask[] subTasks = new SubTask[]{subTask1, subTask2};
        SubTask[] subTaskMemory = taskManager.getAllSubTasks().toArray(new SubTask[0]);
        assertNotNull(taskManager.getAllSubTasks(), "Список пуст");
        assertEquals(2, subTaskMemory.length, "Не совпадает количество сохарненных задач");
        assertArrayEquals(subTasks, subTaskMemory);
    }

    @Test
    void getAllNotSubTasks() {
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список не пуст");
    }

    @Test
    void deleteAllTask() {
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        assertNotNull(taskManager.getTaskMemory().values(), "Список пуст");
        taskManager.deleteAllTask();
        assertEquals(0, taskManager.getTaskMemory().size(), "Список не пуст не удалились задачи");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Список не пуст не удалились задачи");
    }

    @Test
    void deleteAllEpicToNotSubTask() {
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        assertNotNull(taskManager.getTaskMemory().values(), "Список пуст");
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getTaskMemory().size(), "Список не пуст не удалились задачи");
    }

    @Test
    void deleteAllEpicToTwoSubTask() {
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        subTask1.setEpicId(1);
        subTask2.setEpicId(2);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertNotNull(taskManager.getEpicMemory().values(), "Список пуст");
        assertNotNull(taskManager.getSubTaskMemory().values(), "Список подзадач пуст");
        taskManager.deleteAllEpic();
        assertEquals(0, taskManager.getEpicMemory().size(), "Список не пуст не удалились задачи");
        assertEquals(0, taskManager.getSubTaskMemory().size(), "Список не пуст, не удалились подзадачи");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "Список не пуст, не удалились подзадачи");
    }

    @Test
    void deleteAllSubTask() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        subTask1.setStatus(Status.DONE);
        subTask2.setStatus(Status.DONE);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertEquals(Status.DONE, taskManager.getEpicMemory().get(1).getStatus());
        assertNotNull(taskManager.getEpicMemory().values(), "Список пуст");
        assertNotNull(taskManager.getSubTaskMemory().values(), "Список подзадач пуст");
        taskManager.deleteAllSubTask();
        assertNotNull(taskManager.getEpicMemory().values(), "Список Epic пуст");
        assertEquals(0, taskManager.getSubTaskMemory().size(), "Список не пуст, не удалились подзадачи");
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
    }

    @Test
    void deleteTaskById() {
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        assertEquals(2, taskManager.getTaskMemory().size());
        taskManager.deleteTaskById(1);
        assertEquals(1, taskManager.getTaskMemory().size(), "Задача не удалилась");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Задача не удалилась");
    }

    @Test
    void deleteTaskByNotId() {
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        assertEquals(2, taskManager.getTaskMemory().size());
        taskManager.deleteTaskById(3);
        assertEquals(2, taskManager.getTaskMemory().size(), "Изменилось количество задач");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Изменилось количество задач");
    }

    @Test
    void deleteEpicById() {
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        assertEquals(2, taskManager.getEpicMemory().size());
        taskManager.deleteEpicById(1);
        assertEquals(1, taskManager.getEpicMemory().size(), "Задача не удалилась");
    }

    @Test
    void deleteEpicByNotId() {
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        assertEquals(2, taskManager.getEpicMemory().size());
        taskManager.deleteEpicById(3);
        assertEquals(2, taskManager.getEpicMemory().size(), "Изменилось количество задач");
    }

    @Test
    void deleteEpicByOneSubTask() {
        taskManager.saveEpic(epic1);
        taskManager.saveEpic(epic2);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertEquals(2, taskManager.getEpicMemory().size());
        assertEquals(1, taskManager.getSubTaskMemory().size());
        taskManager.deleteEpicById(1);
        assertEquals(1, taskManager.getEpicMemory().size(), "Epic не удалился");
        assertEquals(0, taskManager.getSubTaskMemory().size(), "SubTask не удалилась вместе с Epic");
        assertEquals(0, taskManager.getPrioritizedTasks().size(), "SubTask не удалилась вместе с Epic");
    }

    @Test
    void deleteSubTaskById() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        subTask2.setStatus(Status.DONE);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicMemory().get(1).getStatus());
        assertNotNull(taskManager.getAllEpics(), "Список пуст");
        assertNotNull(taskManager.getAllSubTasks(), "Список подзадач пуст");
        taskManager.deleteSubTaskById(2);
        assertNotNull(taskManager.getAllEpics(), "Список Epic пуст");
        assertEquals(1, taskManager.getSubTaskMemory().size(), "Подзадача не удалилась");
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "Подзадача не удалилась");
        assertEquals(Status.DONE, taskManager.getEpicMemory().get(1).getStatus(), "Статус Эпика не изменился");
    }

    @Test
    void deleteSubTaskByNotId() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        subTask2.setStatus(Status.DONE);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicMemory().get(1).getStatus());
        assertNotNull(taskManager.getAllEpics(), "Список пуст");
        assertNotNull(taskManager.getAllSubTasks(), "Список подзадач пуст");
        taskManager.deleteSubTaskById(4);
        assertNotNull(taskManager.getAllEpics(), "Список Epic пуст");
        assertEquals(2, taskManager.getSubTaskMemory().size(), "Подзадача удалилась");
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "Подзадача удалилась");
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicMemory().get(1).getStatus(), "Статус Эпика изменился");
    }

    @Test
    void updateTask() {
        taskManager.saveTask(task1);
        Task task1Update = new Task ("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        assertEquals(task1, taskManager.getTaskMemory().get(1));
        assertEquals(Status.NEW, taskManager.getTaskMemory().get(1).getStatus());
        task1Update.setId(1);
        task1Update.setStatus(Status.DONE);
        task1Update.setStartTime(LocalDateTime.of(2024,8,23,12,00));
        task1Update.setDuration(240);
        taskManager.updateTask(task1Update);
        assertEquals(1, taskManager.getTaskMemory().size());
        assertEquals(task1Update, taskManager.getTaskMemory().get(1));
        assertEquals(Status.DONE, taskManager.getTaskMemory().get(1).getStatus());
        assertEquals(LocalDateTime.of(2024,8,23,12,00),
                taskManager.getPrioritizedTasks().get(task1Update.getStartTime()).getStartTime());
    }

    @Test
    void updateTaskNull() {
        taskManager.saveTask(task1);
        assertEquals(task1, taskManager.getTaskMemory().get(1));
        assertEquals(Status.NEW, taskManager.getTaskMemory().get(1).getStatus());
        taskManager.updateTask(taskNull);
        assertEquals(1, taskManager.getTaskMemory().size());
        assertEquals(task1, taskManager.getTaskMemory().get(1));
        assertEquals(Status.NEW, taskManager.getTaskMemory().get(1).getStatus());
    }

    @Test
    void updateTaskByNotTasks() {
        assertEquals(0, taskManager.getTaskMemory().size());
        taskManager.updateTask(task1);
        assertEquals(0, taskManager.getTaskMemory().size());
    }

    @Test
    void updateEpic() {
        taskManager.saveEpic(epic1);
        assertEquals(epic1, taskManager.getEpicMemory().get(1));
        assertEquals("Описание эпика №1", taskManager.getEpicMemory().get(1).getDescription());
        epic1.setDescription("Новое описание эпика №1");
        taskManager.updateEpic(epic1);
        assertEquals(1, taskManager.getEpicMemory().size());
        assertEquals(epic1, taskManager.getEpicMemory().get(1));
        assertEquals("Новое описание эпика №1", taskManager.getEpicMemory().get(1).getDescription());
    }

    @Test
    void updateEpicNull() {
        taskManager.saveEpic(epic1);
        assertEquals(epic1, taskManager.getEpicMemory().get(1));
        assertEquals("Описание эпика №1", taskManager.getEpicMemory().get(1).getDescription());
        taskManager.updateEpic(epicNull);
        assertEquals(1, taskManager.getEpicMemory().size());
        assertEquals(epic1, taskManager.getEpicMemory().get(1));
        assertEquals("Описание эпика №1", taskManager.getEpicMemory().get(1).getDescription());
    }

    @Test
    void updateEpicByNotEpics() {
        assertEquals(0, taskManager.getEpicMemory().size());
        taskManager.updateEpic(epic1);
        assertEquals(0, taskManager.getEpicMemory().size());
    }

    @Test
    void updateSubTask() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubTaskMemory().get(2));
        assertEquals(Status.NEW, taskManager.getSubTaskMemory().get(2).getStatus());
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus());
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        assertEquals(1, taskManager.getSubTaskMemory().size());
        assertEquals(Status.DONE, taskManager.getSubTaskMemory().get(2).getStatus());
        assertEquals(Status.DONE, taskManager.getEpicMemory().get(1).getStatus());
    }

    @Test
    void updateSubTaskByTwoSubTasks() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        subTask2.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        assertEquals(subTask1, taskManager.getSubTaskMemory().get(2));
        assertEquals(subTask1, taskManager.getSubTaskMemory().get(2));
        assertEquals(Status.NEW, taskManager.getSubTaskMemory().get(2).getStatus());
        assertEquals(Status.NEW, taskManager.getSubTaskMemory().get(3).getStatus());
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus());
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        assertEquals(2, taskManager.getSubTaskMemory().size());
        assertEquals(Status.DONE, taskManager.getSubTaskMemory().get(2).getStatus());
        assertEquals(Status.NEW, taskManager.getSubTaskMemory().get(3).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicMemory().get(1).getStatus());
    }

    @Test
    void updateSubTaskNull() {
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        assertEquals(subTask1, taskManager.getSubTaskMemory().get(2));
        assertEquals(Status.NEW, taskManager.getSubTaskMemory().get(2).getStatus());
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus());
        taskManager.updateSubTask(subTaskNull);
        assertEquals(1, taskManager.getSubTaskMemory().size());
        assertEquals(Status.NEW, taskManager.getSubTaskMemory().get(2).getStatus());
        assertEquals(Status.NEW, taskManager.getEpicMemory().get(1).getStatus());
    }

    @Test
    void updateSubTaskByNotSubTasks() {
        assertEquals(0, taskManager.getSubTaskMemory().size());
        taskManager.updateEpic(epic1);
        assertEquals(0, taskManager.getSubTaskMemory().size());
    }

    @Test
    void saveTaskToNotData() {
        assertNull(task2.getStartTime());
        assertNull(task2.getEndTime());
        assertNull(task2.getDuration());
        taskManager.saveTask(task2);
        assertNotNull(task2.getStartTime());
        assertNull(task2.getEndTime());
        assertNull(task2.getDuration());
        assertTrue(taskManager.getPrioritizedTasks().containsValue(task2));
    }

    @Test
    void saveTaskToData() {
        assertNotNull(task1.getStartTime());
        assertNotNull(task1.getEndTime());
        assertNotNull(task1.getDuration());
        taskManager.saveTask(task1);
        assertTrue(taskManager.getPrioritizedTasks().containsValue(task1));
    }

    @Test
    void saveSubTaskToNotData() {
        assertNull(subTask2.getStartTime());
        assertNull(subTask2.getEndTime());
        assertNull(subTask2.getDuration());
        taskManager.saveTask(subTask2);
        assertNotNull(subTask2.getStartTime());
        assertNull(subTask2.getEndTime());
        assertNull(subTask2.getDuration());
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask2));
    }

    @Test
    void saveSubTaskToData() {
        taskManager.saveEpic(epic1);
        subTask4.setEpicId(1);
        taskManager.saveSubTask(subTask4);
        assertTrue(taskManager.getPrioritizedTasks().containsValue(subTask4));
    }

    @Test
    void validateToError() {
        taskManager.saveTask(task1);
        task2 = task1;
        task2.setStartTime(task1.getStartTime().plusMinutes(20));
        TaskValidationException ex = assertThrows(
                TaskValidationException.class,
                () -> taskManager.validate(task2)
        );
        assertEquals("Сохранение (изменение) невозможно так как" +
                " происходит пересечение с другими задачами", ex.getMessage());
    }

    @Test
    void validateNotError() {
        taskManager.saveTask(task1);
        LocalDateTime timeStart = task1.getStartTime().plusMinutes(1580);
        Integer duration = task1.getDuration();
        task2.setStartTime(timeStart);
        task2.setDuration(duration);
        taskManager.saveTask(task2);
        taskManager.validate(task3);
    }

    @Test
    void checkWorkNumberDateTime() {
        LocalDateTime check1 = LocalDateTime.of(3000, 01, 01, 0, 1);
        LocalDateTime check2 = taskManager.numberDataByTaskNotData();
        assertEquals(check1, check2);
    }

    @Test
    void startAndEndTimeEpicNormalWork() {
        assertNull(epic1.getStartTime());
        assertNull(epic1.getEndTime());
        assertNull(epic1.getDuration());
        taskManager.saveEpic(epic1);
        subTask1.setEpicId(1);
        subTask4.setEpicId(1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask4);
        taskManager.startAndEndTimeEpic(epic1);
        assertEquals(subTask1.getStartTime(),epic1.getStartTime());
        assertEquals(subTask4.getEndTime(),epic1.getEndTime());
        assertEquals(300,epic1.getDuration());
    }

    @Test
    void startAndEndTimeEpicNull() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> taskManager.startAndEndTimeEpic(epicNull)
        );
        assertEquals("Расчитать время начала и конца невозможно, " +
                "т.к. переданный  Epic равен null", ex.getMessage());
    }

    @Test
    void startAndEndTimeEpicNotSubTasks() {
        epic1.setStartTime(LocalDateTime.of(3000, 01, 01, 0, 0));
        epic1.setEndTime(LocalDateTime.of(3000, 01, 01, 0, 0));
        taskManager.startAndEndTimeEpic(epic1);
        assertNull(epic1.getStartTime());
        assertNull(epic1.getEndTime());
    }
}