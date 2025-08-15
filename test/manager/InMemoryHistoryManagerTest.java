package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;

    @BeforeEach
    void initHistoryManager() {
        historyManager = Managers.getDefaultHistory();
    }


    @Test
    void addTaskTest() {
        Task task = new Task("task1", "task1_desc");
        historyManager.addTask(task);

        final List<Task> tasks = historyManager.getHistory();

        assertNotNull(tasks, "задача не добавилась");
        assertEquals(1, tasks.size(), "задача добавлена некорректно");
        assertEquals(task, tasks.getFirst(), "задачи не совпадают");
    }

    //теперь этот тест сохраняет состояние задачи в истории независимо от изменения объекта задачи, но копии не хранятся
    @Test
    void differentVersionTasksTest() {
        Task task = new Task("task1", "task1_desc");
        historyManager.addTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(1, historyManager.getHistory().size(), "неверное количество задач в истории");
        assertNotEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().getFirst().getStatus(), "История " +
                "обновилась вместе с объектом");
    }

    @Test
    void removeTaskTest() {
        Task task = new Task("task1", "task1_desc");
        historyManager.addTask(task);

        assertNotNull(historyManager.getHistory(), "задача не добавлена");

        historyManager.removeTask(task.getId());

        assertEquals(0, historyManager.getHistory().size(), "задача не удалена");
    }

    @Test
    void historyIndexOrderTest() {
        Task task1 = new Task("task1", "task1_desc");
        task1.setId(1);
        Task task2 = new Task("task2", "task2_desc");
        task2.setId(2);

        historyManager.addTask(task1);
        historyManager.addTask(task2);

        assertNotNull(historyManager.getHistory(), "задачи не добавлены");
        assertEquals(2, historyManager.getHistory().size(), "неверное количество задач в истории");
        assertEquals(task1, historyManager.getHistory().getFirst(), "порядок не совпал");
        assertEquals(task2, historyManager.getHistory().get(1), "порядок не совпал");

        historyManager.removeTask(task1.getId());

        assertEquals(task2, historyManager.getHistory().getFirst(), "индекс после удаления не совпал");
    }

    @Test
    void historyLinkedListOrderTest() {
        Task task1 = new Task("task1", "task1_desc");
        task1.setId(1);
        Task task2 = new Task("task2", "task2_desc");
        task2.setId(2);
        Task task3 = new Task("task3", "task3_desc");
        task3.setId(3);

        historyManager.addTask(task1);
        historyManager.addTask(task2);
        historyManager.addTask(task3);

        assertNotNull(historyManager.getHistory(), "задачи не добавлены");
        assertEquals(3, historyManager.getHistory().size(), "неверное количество задач в истории");
        assertEquals(task1, historyManager.getHistory().getFirst(), "порядок не совпал");
        assertEquals(task2, historyManager.getHistory().get(1), "порядок не совпал");
        assertEquals(task3, historyManager.getHistory().get(2), "порядок не совпал");

        historyManager.removeTask(task2.getId());

        assertEquals(task1, historyManager.getHistory().getFirst(), "индекс после удаления не совпал");
        assertEquals(task3, historyManager.getHistory().get(1), "индекс после удаления не совпал");
    }
}