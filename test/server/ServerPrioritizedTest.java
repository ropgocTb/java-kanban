package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServerPrioritizedTest {
    static TaskManager manager = new InMemoryTaskManager();
    static HttpTaskServer taskServer;
    Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalTimeTypeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .serializeNulls()
            .create();

    @BeforeEach
    public void startServer() {
        Task task = new Task("a", "b", LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);
        Task task1 = new Task("a", "b", task.getStartTime().minusMinutes(55), Duration.ofMinutes(45));
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
    public void getPrioritizedTasksTest() throws IOException, InterruptedException {
        List<Task> prioritizedTasks = manager.getPrioritizedTasks();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String str = response.body();
        List<Task> prioritizedTasksFromServer = gson.fromJson(str, new TaskListTypeToken());

        assertEquals(200, response.statusCode());
        assertEquals(prioritizedTasks.size(), prioritizedTasksFromServer.size(), "очередь не совпала " +
                "по размерам");
    }
}
