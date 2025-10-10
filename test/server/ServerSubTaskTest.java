package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manager.InMemoryTaskManager;
import manager.NotFoundException;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServerSubTaskTest {
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
    public void getSubTasksTest() throws IOException, InterruptedException {
        List<SubTask> subTasks = manager.getSubTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(subTasks), str);
    }

    @Test
    public void getSubTaskByIdTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(200, response.statusCode());
        assertEquals(gson.fromJson(str, SubTask.class).getTitle(), manager.getSubTask(2).getTitle(),
                "задачи не совпали");
    }

    @Test
    public void postNewSubTaskTest() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("new_sub", "new_sub_desc", manager.getEpics().getFirst());
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(201, response.statusCode());
        assertEquals(3, manager.getSubTasks().size(), "подзадача не добавилась");
    }

    @Test
    public void postNewSubTaskWithIdTest() throws IOException, InterruptedException {
        SubTask subTask = new SubTask("new_sub", "new_sub_desc", manager.getEpics().getFirst());
        subTask.setId(3);
        String subTaskJson = gson.toJson(subTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        System.out.println(str);

        assertEquals(201, response.statusCode());
        assertEquals(manager.getSubTask(3).getTitle(), subTask.getTitle(), "подзадача не обновилась");
    }

    @Test
    public void deleteSubTaskTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertThrows(NotFoundException.class, () -> manager.getSubTask(3), "задача не удалилась");
    }
}
