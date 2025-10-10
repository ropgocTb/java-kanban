package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerEpicTest {
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

        Epic epic = new Epic("epic", "epic_desc");
        manager.addEpic(epic);

        SubTask subTask1 = new SubTask("sub1", "sub1_desc", epic);
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("sub2", "sub2_desc", epic);
        manager.addSubTask(subTask2);

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
    public void getEpicsTest() throws IOException, InterruptedException {
        List<Epic> epics = manager.getEpics();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(epics), str, "эпики не совпали");
    }

    @Test
    public void getSubTasksOfEpicTest() throws IOException, InterruptedException {
        Epic epic = manager.getEpic(1);
        List<SubTask> subTasks = manager.getSubTasksByEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(subTasks), str, "список подзадач не совпал");
    }

    @Test
    public void getEpicByIdTest() throws IOException, InterruptedException {
        Epic epic = manager.getEpic(1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(epic), str, "вернувшийся эпик не совпал");
    }

    @Test
    public void postEpicTest() throws IOException, InterruptedException {
        Epic epic = new Epic("new_epic", "new_epic_desc");
        String jsonEpic = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(2, manager.getEpics().size(), "эпик не добавился");
    }

    @Test
    public void deleteEpicTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getEpics().size(), "эпик не удалился");
    }
}
