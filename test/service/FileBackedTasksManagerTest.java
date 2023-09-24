package service;

import entility.Epic;
import entility.SubTask;
import entility.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest <FileBackedTasksManager > {

    File fileTest = new File("test/resource/BackedInformationTest.csv");
    File fileTestLoad = new File("test/resource/BackedInformationTestLoad.csv");
    @BeforeEach
    void setUp() {
        taskManager = (FileBackedTasksManager) Managers.getDefaultBackedTask();
        init();
    }

    @Test
    void saveNormal() throws IOException {
        taskManager.setFile(fileTest);
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.saveEpic(epic2);
        taskManager.saveSubTask(subTask4);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(7);
        String contentTest = Files.readString(Path.of(String.valueOf(fileTest)));
        String[] textTestString = contentTest.split("\n");
        String content = Files.readString(Path.of(String.valueOf(new File("test/resource/BackedInformationFirst.csv"))));
        String[] textString = content.split("\n");
        assertArrayEquals(textString, textTestString, "Новый файл не совпадает с исходным, не правильная работа записи");
    }


    @Test
    void loadFromFile() {
        taskManager.setFile(fileTestLoad);
        taskManager.saveTask(task1);
        taskManager.saveTask(task2);
        taskManager.saveEpic(epic1);
        taskManager.saveSubTask(subTask1);
        taskManager.saveSubTask(subTask2);
        taskManager.saveEpic(epic2);
        taskManager.saveSubTask(subTask3);
        taskManager.getEpicById(3);
        taskManager.getSubTaskById(5);
        taskManager.getTaskById(1);
        taskManager.getEpicById(6);
        taskManager.getSubTaskById(7);
        taskManager.getSubTaskById(5);
        TaskManager manager = FileBackedTasksManager.loadFromFile(fileTestLoad);
        Task[] tasksMemory = taskManager.getAllTasks().toArray(new Task[0]);
        Task[] tasksMemoryLoad = manager.getAllTasks().toArray(new Task[0]);
        SubTask[] subTasksMemory = taskManager.getAllSubTasks().toArray(new SubTask[0]);
        SubTask[] subTasksMemoryLoad = manager.getAllSubTasks().toArray(new SubTask[0]);
        Epic[] epicsMemory = taskManager.getAllEpics().toArray(new Epic[0]);
        Epic[] epicsMemoryLoad = manager.getAllEpics().toArray(new Epic[0]);
        Task[] historyMemory = taskManager.getHistory().toArray(new Task[0]);
        Task[] historyMemoryLoad = manager.getHistory().toArray(new Task[0]);
        assertArrayEquals(tasksMemory, tasksMemoryLoad);
        assertArrayEquals(subTasksMemory,subTasksMemoryLoad);
        assertArrayEquals(epicsMemory, epicsMemoryLoad);
        assertArrayEquals(historyMemory, historyMemoryLoad);
    }
}