package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import entility.*;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static java.lang.Integer.parseInt;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private HttpServer server;
    private TaskManager manager;
    private Gson gson;

    public HttpTaskServer() {
        manager = Managers.getDefault();
    }

    public HttpTaskServer(TaskManager manager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", new TaskHandler());
        this.manager = manager;
    }

    public class TaskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            System.out.println("Началась обработка end-point " + httpExchange.getRequestURI());
            String method = httpExchange.getRequestMethod();
            gson = Managers.getGson();
            String query = httpExchange.getRequestURI().getQuery();
            String paths = httpExchange.getRequestURI().getPath().replaceFirst("/tasks/", "");
            String[] path = paths.split("/");
            switch (path[0]) {
                case "": {
                    if (method.equals("GET")) {
                        String response = gson.toJson(manager.getPrioritizedTask());
                        writeResponse(httpExchange, response, 200);
                    } else {
                        writeResponse(httpExchange, "Ожидается GET запрос а получен "
                                + method, 405);
                    }
                    break;
                }
                case "task": {
                    switch (method) {
                        case "GET": {
                            String response;
                            int responseCode = 200;
                            if (query == null) {
                                response = gson.toJson(manager.getAllTasks());
                            } else if (query.contains("id=")) {
                                StringBuilder idString = new StringBuilder(query);
                                idString.delete(0, 3);
                                int id = parseInt(idString.toString());
                                response = gson.toJson(manager.getTaskById(id));
                            } else {
                                response = "Такого эндпоинта не существует";
                                responseCode = 400;
                            }
                            writeResponse(httpExchange, response, responseCode);
                            break;
                        }
                        case "POST": {
                            InputStream inputStream = httpExchange.getRequestBody();
                            if (inputStream != null) {
                                String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                                if (requestBody.isEmpty()) {
                                    writeResponse(httpExchange, "Тело запроса пустое", 400);
                                    return;
                                }
                                if (query == null) {
                                    System.out.println("Началась обработка запроса по созданию задачи");
                                    Task task = gson.fromJson(requestBody, Task.class);
                                    if (task.getId() == 0) {
                                        if (task.getStatus() == null) {
                                            task.setStatus(Status.NEW);
                                        }
                                        task.setType(Type.TASK);
                                        manager.saveTask(task);
                                        writeResponse(httpExchange, "Задача успешно создана",
                                                200);
                                    } else {
                                        manager.updateTask(task);
                                        writeResponse(httpExchange, "Задача успешно обновлена",
                                                200);
                                    }
                                } else {
                                    writeResponse(httpExchange, "Ожидается tasks/task/ а получено "
                                            + httpExchange.getRequestURI(), 405);
                                }
                            }
                            break;
                        }
                        case "DELETE": {
                            if (query == null) {
                                manager.deleteAllTask();
                                writeResponse(httpExchange, "Все задачи удалены", 200);
                            } else if (query.contains("id=")) {
                                StringBuilder idString = new StringBuilder(query);
                                idString.delete(0, 3);
                                int id = parseInt(idString.toString());
                                manager.deleteTaskById(id);
                                writeResponse(httpExchange, "Задача с id = " + id + " удалена",
                                        200);
                            } else {
                                writeResponse(httpExchange, "Такого эндпоинта не существует",
                                        400);
                            }
                            break;
                        }
                        default: {
                            writeResponse(httpExchange, "Ожидаем GET,POST,DELETE запрос а получен "
                                    + method, 405);
                        }
                    }
                    break;
                }
                case "epic": {
                    switch (method) {
                        case "GET": {
                            String response;
                            int responseCode = 200;
                            if (query == null) {
                                response = gson.toJson(manager.getAllEpics());
                            } else if (query.contains("id=")) {
                                StringBuilder idString = new StringBuilder(query);
                                idString.delete(0, 3);
                                int id = parseInt(idString.toString());
                                response = gson.toJson(manager.getEpicById(id));
                            } else {
                                response = "Такого эндпоинта не существует";
                                responseCode = 400;
                            }
                            writeResponse(httpExchange, response, responseCode);
                            break;
                        }
                        case "POST": {
                            InputStream inputStream = httpExchange.getRequestBody();
                            if (inputStream != null) {
                                String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                                if (requestBody.isEmpty()) {
                                    writeResponse(httpExchange, "Тело запроса пустое", 400);
                                    return;
                                }
                                if (query == null) {
                                    Epic epic = gson.fromJson(requestBody, Epic.class);
                                    if (epic.getId() == 0) {
                                        System.out.println("Началась обработка запроса по созданию эпика");
                                        epic.setType(Type.EPIC);
                                        manager.saveEpic(epic);
                                        writeResponse(httpExchange, "Эпик успешно создан",
                                                200);
                                    } else {
                                        System.out.println("Началась обработка запроса по обновлению эпика");
                                        manager.updateEpic(epic);
                                        writeResponse(httpExchange, "Эпик успешно обновлен",
                                                200);
                                    }
                                } else {
                                    writeResponse(httpExchange, "Ожидается tasks/epic/ а получено "
                                            + httpExchange.getRequestURI(), 400);
                                }
                            }
                            break;
                        }
                        case "DELETE": {
                            if (query == null) {
                                manager.deleteAllEpic();
                                writeResponse(httpExchange, "Все эпики удалены", 200);
                            } else if (query.contains("id=")) {
                                StringBuilder idString = new StringBuilder(query);
                                idString.delete(0, 3);
                                int id = parseInt(idString.toString());
                                manager.deleteEpicById(id);
                                writeResponse(httpExchange, "Эпик с id = " + id + " удалена",
                                        200);
                            } else {
                                writeResponse(httpExchange, "Такого эндпоинта не существует",
                                        400);
                            }
                            break;
                        }
                        default: {
                            writeResponse(httpExchange, "Ожидаем GET,POST,DELETE запрос а получен "
                                    + method, 405);
                        }
                    }
                    break;
                }
                case "subtask": {
                    switch (method) {
                        case "GET": {
                            String response;
                            int responseCode = 200;
                            if (path.length < 2) {
                                if (query == null) {
                                    response = gson.toJson(manager.getAllSubTasks());
                                } else if (query.contains("id=")) {
                                    StringBuilder idString = new StringBuilder(query);
                                    idString.delete(0, 3);
                                    int id = parseInt(idString.toString());
                                    response = gson.toJson(manager.getSubTaskById(id));
                                } else {
                                    response = "Такого эндпоинта не существует";
                                    responseCode = 400;
                                }
                            } else if (path[1].equals("epic")&&query.contains("id=")) {
                                StringBuilder idString = new StringBuilder(query);
                                idString.delete(0, 3);
                                Epic epic = manager.getEpicById(parseInt(idString.toString()));
                                response = gson.toJson(manager.subTasksByEpic(epic));
                            } else {
                                response = "Такого эндпоинта не существует";
                                responseCode = 400;
                            }
                            writeResponse(httpExchange, response, responseCode);
                            break;
                        }
                        case "POST": {
                            InputStream inputStream = httpExchange.getRequestBody();
                            if (inputStream != null) {
                                String requestBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                                if (requestBody.isEmpty()) {
                                    writeResponse(httpExchange, "Тело запроса пустое", 400);
                                    return;
                                }
                                if (query == null) {
                                    SubTask subTask = gson.fromJson(requestBody, SubTask.class);
                                    if (subTask.getId() == 0) {
                                        System.out.println("Началась обработка запроса по созданию подзадачи");
                                        if (subTask.getStatus() == null) {
                                            subTask.setStatus(Status.NEW);
                                        }
                                        subTask.setType(Type.SUBTASK);
                                        manager.saveSubTask(subTask);
                                        writeResponse(httpExchange, "Подзадача успешно создана",
                                                200);
                                    } else {
                                        System.out.println("Началась обработка запроса по обновлению подзадачи");
                                        manager.updateSubTask(subTask);
                                        writeResponse(httpExchange, "Подзадача успешно обновлена",
                                                200);
                                    }
                                } else {
                                    writeResponse(httpExchange, "Ожидается tasks/subtask/ а получено "
                                            + httpExchange.getRequestURI(), 400);
                                }
                            }
                            break;
                        }
                        case "DELETE":{
                            if (query == null) {
                                manager.deleteAllSubTask();
                                writeResponse(httpExchange, "Все подзадачи удалены", 200);
                            } else if (query.contains("id=")) {
                                StringBuilder idString = new StringBuilder(query);
                                idString.delete(0,3);
                                int id = parseInt(idString.toString());
                                manager.deleteSubTaskById(id);
                                writeResponse(httpExchange, "Подадача с id = " + id + " удалена",
                                        200);
                            } else {
                                writeResponse(httpExchange, "Такого эндпоинта не существует",
                                        400);
                            }
                            break;
                        }
                        default: {
                            writeResponse(httpExchange, "Ожидаем GET,POST,DELETE запрос а получен "
                                    + method, 405);
                        }
                    }
                    break;
                }
                case "history": {
                    if (method.equals("GET")) {
                        String response = gson.toJson(manager.getHistory());
                        writeResponse(httpExchange, response, 200);
                    } else {
                        writeResponse(httpExchange, "Ожидаем GET запрос а получен " + method,
                                405);
                    }
                    break;
                }
                default: {
                    writeResponse(httpExchange, "Такого эндпоинта не существует", 404);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        final HttpTaskServer server = new HttpTaskServer();
        server.start();


    }

    public void start() {
        System.out.println("Сервер запущен с портом " + PORT);
        server.start();
    }

    public void stop(int delay) {
        server.stop(delay);
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    public TaskManager getManager() {
        return manager;
    }
}