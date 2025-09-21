package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {
    File file;

    @BeforeEach
    public void initFile() {
        try {
            file = File.createTempFile("testFile", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //сохранение и загрузка пустого файла
    @Test
    public void SaveAndLoadOfAnEmptyFileTest() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.getTask(0);
        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file);
        assertEquals(0, manager1.getTasks().size(), "в менеджере есть задачи");
        assertEquals(0, manager1.getEpics().size(), "в менеджере есть эпики");
        assertEquals(0, manager1.getSubTasks().size(), "в менеджере есть подзадачи");
    }

    @Test
    public void SaveMultipleTasksTest() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = new Task("task1", "task1_desc");
        manager.addTask(task1);
        Task task2 = new Task("task2", "task2_desc");
        manager.addTask(task2);

        assertTrue(file.length() > 0, "файл пустой");

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasksFromFile = manager2.getTasks();

        assertNotNull(tasksFromFile);
        assertEquals(2, tasksFromFile.size(), "количество задач не совпало");
        assertTrue(tasksFromFile.contains(task1), "задачи нет в списке");
        assertTrue(tasksFromFile.contains(task2), "задачи нет в списке");
    }

    @Test
    public void LoadMultipleTasksTest() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Epic epic = new Epic("epic1", "epic1_desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("subTask", "subTask_desc", epic);
        manager.addSubTask(subTask);

        assertTrue(file.length() > 0, "файл пустой");

        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file);
        List<Task> tasksFromFile = new ArrayList<>();
        tasksFromFile.add(manager1.getEpic(epic.getId()));
        tasksFromFile.add(manager.getSubTask(subTask.getId()));

        assertNotNull(tasksFromFile);
        assertEquals(2, tasksFromFile.size(), "количество задач не совпало");
        assertTrue(tasksFromFile.contains(epic), "задачи нет в списке");
        assertTrue(tasksFromFile.contains(subTask), "задачи нет в списке");

    }
}
