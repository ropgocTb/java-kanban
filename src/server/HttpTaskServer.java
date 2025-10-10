package server;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.NotFoundException;
import manager.OverlapException;
import manager.TaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager taskManager) {
        this.manager = taskManager;
        this.gson = new GsonBuilder().setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .serializeNulls()
                .create();
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TaskHandler());
        server.createContext("/subtasks", new SubTaskHandler());
        server.createContext("/epics", new EpicHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/prioritized", new PrioritizedHandler());
        server.start();
    }

    public void stop() {
        server.stop(0);
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] elements = exchange.getRequestURI().getPath().split("/");

        try {
            return Optional.of(Integer.parseInt(elements[2]));
        } catch (NumberFormatException ex) {
            return Optional.empty();
        }
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
        System.out.println("Server started on port " + PORT);
    }

    public class TaskHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            try {
                switch (method) {
                    case "GET":
                        handleGetTask(exchange);
                        break;
                    case "POST":
                        handlePostTask(exchange);
                        break;
                    case "DELETE":
                        handleDeleteTask(exchange);
                        break;
                    default:
                        exchange.sendResponseHeaders(500, -1);
                        exchange.close();
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (OverlapException e) {
                sendHasOverlaps(exchange);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        }

        private void handleGetTask(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String jsonString;
            if (path.equals("/tasks")) {
                jsonString = gson.toJson(manager.getTasks());
            } else {
                Optional<Integer> idOpt = getTaskId(exchange);
                if (idOpt.isPresent()) {
                    int id = idOpt.get();
                    jsonString = gson.toJson(manager.getTask(id));
                } else {
                    throw new NotFoundException("Не найдена такая задача.");
                }
            }
            sendText(exchange, jsonString);
        }

        private void handlePostTask(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
                int id = jsonObject.get("id").getAsInt();
                String type = jsonObject.get("type").getAsString();

                if (!type.equals("TASK")) {
                    sendInvalidInput(exchange);
                    return;
                }

                if (id == 0) {
                    manager.addTask(gson.fromJson(body, Task.class));
                } else {
                    manager.updateTask(gson.fromJson(body, Task.class));
                }

                sendCreated(exchange);
            }
        }

        private void handleDeleteTask(HttpExchange exchange) throws IOException {
            Optional<Integer> idOpt = getTaskId(exchange);
            if (idOpt.isEmpty())
                throw new NotFoundException("не найдена такая задача");
            manager.removeTask(idOpt.get());
            sendText(exchange, "Задача удалена");
        }
    }


    public class SubTaskHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            try {
                switch (method) {
                    case "GET":
                        handleGetSubTask(exchange);
                        break;
                    case "POST":
                        handlePostSubTask(exchange);
                        break;
                    case "DELETE":
                        handleDeleteSubTask(exchange);
                        break;
                    default:
                        exchange.sendResponseHeaders(500, -1);
                        exchange.close();
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (OverlapException e) {
                sendHasOverlaps(exchange);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        }

        public void handleGetSubTask(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String jsonString;
            if (path.equals("/subtasks")) {
                jsonString = gson.toJson(manager.getSubTasks());
            } else {
                Optional<Integer> idOpt = getTaskId(exchange);
                if (idOpt.isPresent()) {
                    jsonString = gson.toJson(manager.getSubTask(idOpt.get()));
                } else {
                    throw new NotFoundException("не найдена такая подзадача");
                }
            }
            sendText(exchange, jsonString);
        }

        public void handlePostSubTask(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject jsonObject = gson.fromJson(body, JsonObject.class);
                int id = jsonObject.get("id").getAsInt();
                String type = jsonObject.get("type").getAsString();

                if (!type.equals("SUBTASK")) {
                    sendInvalidInput(exchange);
                    return;
                }

                if (id == 0) {
                    manager.addSubTask(gson.fromJson(body, SubTask.class));
                } else {
                    manager.updateSubTask(gson.fromJson(body, SubTask.class));
                }
                sendCreated(exchange);
            }
        }

        public void handleDeleteSubTask(HttpExchange exchange) throws  IOException {
            Optional<Integer> idOpt = getTaskId(exchange);
            if (idOpt.isEmpty())
                throw new NotFoundException("не найдена такая задача");
            manager.removeSubTask(idOpt.get());
            sendText(exchange, "Подзадача удалена");
        }
    }

    public class EpicHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();

            try {
                switch (method) {
                    case "GET":
                        handleGetEpic(exchange);
                        break;
                    case "POST":
                        handlePostEpic(exchange);
                        break;
                    case "DELETE":
                        handleDeleteEpic(exchange);
                        break;
                    default:
                        exchange.sendResponseHeaders(500, -1);
                        exchange.close();
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange);
            } catch (OverlapException e) {
                sendHasOverlaps(exchange);
            } catch (Exception e) {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
            }
        }

        public void handleGetEpic(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            String jsonString;

            if (path.equals("/epics")) {
                jsonString = gson.toJson(manager.getEpics());
            } else {
                Optional<Integer> idOpt = getTaskId(exchange);
                if (idOpt.isPresent() && path.endsWith("subtasks")) {
                    int id = idOpt.get();
                    Epic epic = manager.getEpic(id);
                    jsonString = gson.toJson(manager.getSubTasksByEpic(epic));
                } else if (idOpt.isPresent()) {
                    int id = idOpt.get();
                    jsonString = gson.toJson(manager.getEpic(id));
                } else {
                    throw new NotFoundException("не найден такой эпик");
                }
            }

            sendText(exchange, jsonString);
        }

        public void handlePostEpic(HttpExchange exchange) throws IOException {
            try (InputStream inputStream = exchange.getRequestBody()) {
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

                if (!jsonObject.get("type").getAsString().equals("EPIC")) {
                    sendInvalidInput(exchange);
                    return;
                }

                Epic epic = gson.fromJson(body, Epic.class);
                manager.addEpic(epic);

                sendCreated(exchange);
            }
        }

        public void handleDeleteEpic(HttpExchange exchange) throws IOException {
            Optional<Integer> idOpt = getTaskId(exchange);

            if (idOpt.isEmpty())
                throw new NotFoundException("Не найден такой эпик");

            manager.removeEpic(idOpt.get());
            sendText(exchange, "Эпик удалён");
        }
    }

    public class HistoryHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            if (!exchange.getRequestMethod().equals("GET") || !path.equals("/history"))
                sendInvalidInput(exchange);

            String jsonHistory = gson.toJson(manager.getHistory());

            sendText(exchange, jsonHistory);
        }
    }

    public class PrioritizedHandler extends BaseHttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            if (!exchange.getRequestMethod().equals("GET") || !path.equals("/prioritized"))
                sendInvalidInput(exchange);

            String jsonPrioritized = gson.toJson(manager.getPrioritizedTasks());

            sendText(exchange, jsonPrioritized);
        }
    }
}
