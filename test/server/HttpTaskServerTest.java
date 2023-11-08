package server;

import com.google.gson.Gson;
import entility.Epic;
import entility.Status;
import entility.SubTask;
import entility.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    Gson gson = Managers.getGson();
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void startServer() throws IOException {
        httpTaskServer = new HttpTaskServer(new InMemoryTaskManager());
        httpTaskServer.start();
    }

    @AfterEach
    void stopServer() {
        httpTaskServer.stop(0);
    }

    @Test
    void getTaskId() throws IOException, InterruptedException {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        task1.setId(1);
        String task1Json = gson.toJson(task1);
        httpTaskServer.getManager().saveTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(task1Json,response.body(), "Не правильный вывод задачи");
    }

    @Test
    void saveTask() throws IOException, InterruptedException {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        String task1Json = gson.toJson(task1);
        task1.setId(1);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1Json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(httpTaskServer.getManager().getTaskMemory().get(1),task1,"Неправильное сохранение" +
                "задачи");
    }

    @Test
    void deleteTaskId() throws IOException, InterruptedException {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        task1.setId(1);
        httpTaskServer.getManager().saveTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(0,httpTaskServer.getManager().getTaskMemory().size(),"Задача не удалилась");
    }
    @Test
    void getAllTask() throws IOException, InterruptedException {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        task2 = new Task("Задача №2", "Описание задачи №2",
                "24.08.2023 13:00", 60);
        httpTaskServer.getManager().saveTask(task1);
        httpTaskServer.getManager().saveTask(task2);
        task1.setId(1);
        task2.setId(2);
        String tasksJson = "[" + gson.toJson(task1) + "," + gson.toJson(task2) + "]";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(tasksJson,response.body(), "Не правильный вывод задач");
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        task2 = new Task("Задача №2", "Описание задачи №2",
                "24.08.2023 13:00", 60);
        httpTaskServer.getManager().saveTask(task1);
        httpTaskServer.getManager().saveTask(task2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(0,httpTaskServer.getManager().getTaskMemory().size(),"Задачи не удалились");
    }

    @Test
    void getEpicId() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        epic1.setId(1);
        String epic1Json = gson.toJson(epic1);
        httpTaskServer.getManager().saveEpic(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epic1Json,response.body(), "Не правильный вывод эпика");
    }

    @Test
    void saveEpic() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        String epic1Json = gson.toJson(epic1);
        epic1.setId(1);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epic1Json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epic1, httpTaskServer.getManager().getEpicById(1), "Эпик не сохранился");
    }

    @Test
    void deleteEpicId() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        epic1.setId(1);
        httpTaskServer.getManager().saveEpic(epic1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(0,httpTaskServer.getManager().getEpicMemory().size(),"Эпик не удалился");
    }
    @Test
    void getAllEpic() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        epic2 = new Epic("Эпика №2", "Описание эпика №2");
        httpTaskServer.getManager().saveEpic(epic1);
        httpTaskServer.getManager().saveEpic(epic2);
        epic1.setId(1);
        epic2.setId(2);
        String epicsJson = "[" + gson.toJson(epic1) + "," + gson.toJson(epic2) + "]";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epicsJson,response.body(), "Не правильный вывод эпиков");
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        epic2 = new Epic("Эпика №2", "Описание эпика №2");
        httpTaskServer.getManager().saveEpic(epic1);
        httpTaskServer.getManager().saveEpic(epic2);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(0,httpTaskServer.getManager().getEpicMemory().size(),"Эпики не удалились");
    }

    @Test
    void getSubTaskId() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        httpTaskServer.getManager().saveEpic(epic1);
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                1, "01.09.2023 13:00", 60);
        httpTaskServer.getManager().saveSubTask(subTask1);
        subTask1.setId(2);
        String subTask1Json = gson.toJson(subTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subTask1Json,response.body(), "Не правильный вывод подзадачи");
    }

    @Test
    void saveSubTask() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        httpTaskServer.getManager().saveEpic(epic1);
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                1, "01.09.2023 13:00", 60);
        String subTask1Json = gson.toJson(subTask1);
        subTask1.setId(2);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTask1Json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        subTask2 = httpTaskServer.getManager().getSubTaskMemory().get(2);
        assertEquals(httpTaskServer.getManager().getSubTaskMemory().get(2),subTask2,"Неправильное сохранение" +
                "подзадачи");
    }

    @Test
    void deleteSubTaskId() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        httpTaskServer.getManager().saveEpic(epic1);
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                1, "01.09.2023 13:00", 60);
        httpTaskServer.getManager().saveSubTask(subTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(0,httpTaskServer.getManager().getTaskMemory().size(),"Подзадача не удалилась");
    }
    @Test
    void getAllSubTask() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        httpTaskServer.getManager().saveEpic(epic1);
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                1, "01.09.2023 13:00", 60);
        subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                1, "01.09.2023 14:00", 60);
        httpTaskServer.getManager().saveSubTask(subTask1);
        httpTaskServer.getManager().saveSubTask(subTask2);
        subTask1.setId(2);
        subTask2.setId(3);
        String subTasksJson = "[" + gson.toJson(subTask1) + "," + gson.toJson(subTask2) + "]";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subTasksJson,response.body(), "Не правильный вывод подзадач");
    }

    @Test
    void deleteAllSubTasks() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        httpTaskServer.getManager().saveEpic(epic1);
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                1, "01.09.2023 13:00", 60);
        subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                1, "01.09.2023 14:00", 60);
        httpTaskServer.getManager().saveSubTask(subTask1);
        httpTaskServer.getManager().saveSubTask(subTask2);
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(0,httpTaskServer.getManager().getTaskMemory().size(),"Подзадачи не удалились");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                3, "01.09.2023 13:00", 60);
        subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                3, "01.09.2023 14:00", 60);
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        task2 = new Task("Задача №2", "Описание задачи №2",
                "24.08.2023 13:00", 60);
        httpTaskServer.getManager().saveTask(task1);
        httpTaskServer.getManager().saveTask(task2);
        httpTaskServer.getManager().saveEpic(epic1);
        httpTaskServer.getManager().saveSubTask(subTask1);
        httpTaskServer.getManager().saveSubTask(subTask2);
        httpTaskServer.getManager().getTaskById(2);
        httpTaskServer.getManager().getSubTaskById(5);
        httpTaskServer.getManager().getTaskById(1);
        httpTaskServer.getManager().getSubTaskById(4);
        subTask1.setId(4);
        subTask2.setId(5);
        task1.setId(1);
        task1.setId(2);
        String historyJson = "[" + gson.toJson(task2) + "," + gson.toJson(subTask2)
                + "," + gson.toJson(task1) + "," + gson.toJson(subTask1) + "]";
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(historyJson,response.body(), "Не правильный вывод истории");
    }

    @Test
    void getPriorityTasks() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                3, "24.08.2023 15:00", 60);
        subTask2 = new SubTask("Подзадача 2", "Описание подзадачи 2",
                3, "24.08.2023 13:00", 60);
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 14:00", 60);
        task2 = new Task("Задача №2", "Описание задачи №2",
                "24.08.2023 12:00", 60);
        httpTaskServer.getManager().saveTask(task1);
        httpTaskServer.getManager().saveTask(task2);
        httpTaskServer.getManager().saveEpic(epic1);
        httpTaskServer.getManager().saveSubTask(subTask1);
        httpTaskServer.getManager().saveSubTask(subTask2);
        subTask1.setId(4);
        subTask2.setId(5);
        task1.setId(1);
        task1.setId(2);
        String historyJson = "[" + gson.toJson(task2) + "," + gson.toJson(subTask2)
                + "," + gson.toJson(task1) + "," + gson.toJson(subTask1) + "]";
        URI url = URI.create("http://localhost:8080/tasks");
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(historyJson,response.body(), "Не правильный вывод истории");
    }

    @Test
    void taskUptade() throws IOException, InterruptedException {
        task1 = new Task("Задача №1", "Описание задачи №1",
                "24.08.2023 12:00", 60);
        httpTaskServer.getManager().saveTask(task1);
        Task task1Uptade = httpTaskServer.getManager().getTaskById(1);
        task1Uptade.setStatus(Status.DONE);
        String task1UptadeJson = gson.toJson(task1Uptade);
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task1UptadeJson);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(task1Uptade, httpTaskServer.getManager().getTaskMemory().get(1),"Неправильное " +
                "обновление задачи");
    }

    @Test
    void subTaskUptade() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        subTask1 = new SubTask("Подзадача 1", "Описание подзадачи 1",
                1, "24.08.2023 15:00", 60);
        httpTaskServer.getManager().saveEpic(epic1);
        httpTaskServer.getManager().saveSubTask(subTask1);
        SubTask subTask1Uptade = httpTaskServer.getManager().getSubTaskById(2);
        subTask1Uptade.setStatus(Status.DONE);
        String subTask1UptadeJson = gson.toJson(subTask1Uptade);
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(subTask1UptadeJson);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(subTask1Uptade, httpTaskServer.getManager().getSubTaskMemory().get(2),"Неправильное " +
                "обновление подзадачи");
    }

    @Test
    void epicUptade() throws IOException, InterruptedException {
        epic1 = new Epic("Эпика №1", "Описание эпика №1");
        httpTaskServer.getManager().saveEpic(epic1);
        Epic epic1Uptade = httpTaskServer.getManager().getEpicById(1);
        epic1Uptade.setDescription("Новое описание эпика №1");
        String epic1UptadeJson = gson.toJson(epic1Uptade);
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpClient client = HttpClient.newHttpClient();
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epic1UptadeJson);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(epic1Uptade, httpTaskServer.getManager().getEpicMemory().get(1),"Неправильное " +
                "обновление эпика");
    }
}