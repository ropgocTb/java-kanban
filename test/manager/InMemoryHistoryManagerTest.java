package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
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

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void differentVersionTasksTest() {
        Task task = new Task("task1", "task1_desc");
        historyManager.addTask(task);
        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.addTask(task);

        assertEquals(2, historyManager.getHistory().size(), "неверное количество задач в истории");
        assertEquals(TaskStatus.NEW, historyManager.getHistory().get(0).getStatus(), "История " +
                "не сохранилась");
        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().get(1).getStatus(), "История " +
                "не сохранилась");
        assertEquals(historyManager.getHistory().get(0), historyManager.getHistory().get(1), "объекты " +
                "разные");
    }

}