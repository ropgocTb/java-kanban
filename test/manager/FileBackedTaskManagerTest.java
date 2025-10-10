package manager;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    File file;
    File fileToSave;

    @Override
    public FileBackedTaskManager initTaskManager() {
        return Managers.getFileBacked();
    }

    @BeforeEach
    public void initFile() {
        try {
            file = File.createTempFile("testFile", null);
            fileToSave = File.createTempFile("test_File1", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    public void deleteFile() {
        file.delete();
        fileToSave.delete();
    }

    //сохранение и загрузка пустого файла assertDoesNotThrow()
    @Test
    public void SaveAndLoadOfAnEmptyFileTest() {
        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager = new FileBackedTaskManager(file);
        });
        assertThrows(NotFoundException.class, () -> manager.getTask(0));
        assertDoesNotThrow(() -> {
            FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file, fileToSave);
            assertEquals(0, manager1.getTasks().size(), "в менеджере есть задачи");
            assertEquals(0, manager1.getEpics().size(), "в менеджере есть эпики");
            assertEquals(0, manager1.getSubTasks().size(), "в менеджере есть подзадачи");
        });
    }

    @Test
    public void SaveMultipleTasksTest() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task1 = new Task("task1", "task1_desc");
        manager.addTask(task1);
        Task task2 = new Task("task2", "task2_desc");
        manager.addTask(task2);

        assertTrue(file.length() > 0, "файл пустой");

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file, fileToSave);
        List<Task> tasksFromFile = manager2.getTasks();

        assertNotNull(tasksFromFile);
        assertEquals(2, tasksFromFile.size(), "количество задач не совпало");
        assertTrue(tasksFromFile.contains(task1), "задачи нет в списке");
        assertTrue(tasksFromFile.contains(task2), "задачи нет в списке");
    }

    @Test
    public void startTimeDurationEndTimeSaveLoadTest() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Task task = new Task("task", "task_desc",
                LocalDateTime.of(2025, 10, 1, 0, 0,0),
                Duration.ofMinutes(15));
        manager.addTask(task);
        Epic epic = new Epic("epic", "epic_desc");
        manager.addEpic(epic);
        SubTask subTask1 = new SubTask("sub1", "sub1_desc",
                task.getStartTime().plusMinutes(15), Duration.ofMinutes(45), epic);
        manager.addSubTask(subTask1);

        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file, fileToSave);

        assertEquals(manager.getEpic(epic.getId()).getEndTime(), manager1.getEpic(epic.getId()).getEndTime());
        assertEquals(manager.getEpic(epic.getId()).getStartTime(), manager1.getEpic(epic.getId()).getStartTime());
        assertEquals(manager.getEpic(epic.getId()).getDuration(), manager1.getEpic(epic.getId()).getDuration());

        assertEquals(manager.getTask(task.getId()).getEndTime(), manager1.getTask(task.getId()).getEndTime());
        assertEquals(manager.getTask(task.getId()).getStartTime(), manager1.getTask(task.getId()).getStartTime());
        assertEquals(manager.getTask(task.getId()).getDuration(), manager1.getTask(task.getId()).getDuration());

        assertEquals(manager.getSubTask(subTask1.getId()).getEndTime(),
                manager1.getSubTask(subTask1.getId()).getEndTime());
        assertEquals(manager.getSubTask(subTask1.getId()).getStartTime(),
                manager1.getSubTask(subTask1.getId()).getStartTime());
        assertEquals(manager.getSubTask(subTask1.getId()).getDuration(),
                manager1.getSubTask(subTask1.getId()).getDuration());
    }

    //тесты для ошибок при работе с файлом
    @Test
    public void LoadMultipleTasksTest() {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Epic epic = new Epic("epic1", "epic1_desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("subTask", "subTask_desc", epic);
        manager.addSubTask(subTask);

        assertTrue(file.length() > 0, "файл пустой");

        FileBackedTaskManager manager1 = FileBackedTaskManager.loadFromFile(file, fileToSave);
        List<Task> tasksFromFile = new ArrayList<>();
        tasksFromFile.add(manager1.getEpic(epic.getId()));
        tasksFromFile.add(manager.getSubTask(subTask.getId()));

        assertNotNull(tasksFromFile);
        assertEquals(2, tasksFromFile.size(), "количество задач не совпало");
        assertTrue(tasksFromFile.contains(epic), "задачи нет в списке");
        assertTrue(tasksFromFile.contains(subTask), "задачи нет в списке");
    }

    @Test
    public void testLoadFromNonExistentFile() {
        File nonExistentFile = new File("non_existent_file.txt");
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(nonExistentFile,
                new File("tasks1.txt")));
    }


    @Test
    public void testSaveToReadOnlyFile() {
        File readOnlyFile = new File("read_only_file.txt");
        try {
            readOnlyFile.createNewFile();
            readOnlyFile.setWritable(false);

            FileBackedTaskManager manager = new FileBackedTaskManager(readOnlyFile);
            Task task = new Task("task1", "task1_desc");
            assertThrows(ManagerSaveException.class, () -> manager.addTask(task));
        } catch (IOException e) {
            System.out.println("Не удалось создать файл для теста: " + e.getMessage());
        } finally {
            readOnlyFile.delete();
        }
    }
}
