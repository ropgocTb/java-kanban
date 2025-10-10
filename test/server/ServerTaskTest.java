package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.NotFoundException;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTaskTest {
    static TaskManager manager;
    static HttpTaskServer taskServer;
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();


    @BeforeEach
    public void startServer() {
        manager = new InMemoryTaskManager();
        Task task = new Task("a", "b");
        manager.addTask(task);
        Task task1 = new Task("a", "b");
        manager.addTask(task1);
        taskServer = new HttpTaskServer(manager);

        try {
            taskServer.start();
        } catch (IOException e) {
            System.out.println("не удалось запустить сервер");
        }
    }

    @AfterEach
    public void stopServer() {
        taskServer.stop();
    }

    @Test
    public void getAllTasksTest() throws IOException, InterruptedException {
        List<Task> tasks = manager.getTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(tasks), str);
    }

    @Test
    public void getTaskByIdTest() throws IOException, InterruptedException {
        List<Task> tasks = manager.getTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        Task task = gson.fromJson(str, Task.class);
        System.out.println(task);

        assertEquals(200, response.statusCode());
        assertEquals(task.getId(), tasks.getFirst().getId(), "?");
    }

    @Test
    public void getTaskByWrongIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/q");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

    @Test
    public void postNewTaskTest() throws IOException, InterruptedException {
        Task task = new Task("task_test", "task_desc");
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    public void postTaskWithId() throws IOException, InterruptedException {
        Task task = new Task("task_test", "task_desc");
        task.setId(2);
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(manager.getTask(2).getTitle(), task.getTitle(), "задача не обновилась");
    }

    @Test
    public void removeTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getTask(1), "задача не удалилась");
    }
}
