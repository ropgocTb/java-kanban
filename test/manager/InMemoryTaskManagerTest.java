package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    TaskManager manager;

    @BeforeEach
    public void initTaskManager() { manager = Managers.getDefault(); }

    //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
    @Test
    void addNewTaskTest() {
        Task task = new Task("task1test", "task1desctest");
        manager.addTask(task);

        final int taskId = task.getId();
        final Task savedTask = manager.getTask(taskId);

        //создайте тест, в котором проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task.getId(), savedTask.getId(), "id не совпадают");
        assertEquals(task.getTitle(), savedTask.getTitle(), "названия не совпадают");
        assertEquals(task.getDescription(), savedTask.getDescription(), "описания не совпадают");
        assertEquals(task.getStatus(), savedTask.getStatus(), "статусы не совпадают");

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "задачи не возвращаются");
        assertEquals(1, tasks.size(), "неверное количество задач");
        assertEquals(task, tasks.getFirst(), "задачи не совпадают");
    }

    @Test
    void addNewEpicTest() {
        Epic epic = new Epic("epic_add_test", "epic_add_test_desc");
        manager.addEpic(epic);

        final int epicId = epic.getId();
        final Epic savedEpic = manager.getEpic(epicId);

        assertNotNull(savedEpic, "эпик не найден");
        assertEquals(epic.getId(), savedEpic.getId(), "Не совпадают id");
        assertEquals(epic.getTitle(), savedEpic.getTitle(), "Не совпадают названия");
        assertEquals(epic.getDescription(), savedEpic.getDescription(), "Не совпадают описания");
        assertEquals(epic.getStatus(), savedEpic.getStatus(), "Не совпадают статусы");
        assertEquals(epic.getSubTasks(), savedEpic.getSubTasks(), "Не совпадают подзадачи");

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "эпики не возвращаются");
        assertEquals(1, epics.size(), "неверное количество эпиков");
        assertEquals(epic, epics.getFirst(), "Не совпадают эпики");
    }

    @Test
    void addNewSubTaskTest() {
        //проверка на добавление без родителя
        SubTask subTask = new SubTask("sub1_add_test", "sub1_add_test_desc");
        manager.addSubTask(subTask);

        final int noSubTaskId = subTask.getId();
        final SubTask notSavedSubTask = manager.getSubTask(noSubTaskId);

        assertNull(notSavedSubTask, "подзадача не должна сохраняться без родителя");

        final List<SubTask> emptyList = manager.getSubTasks();

        assertEquals(0, emptyList.size(), "в списке что то есть");

        Epic epic = new Epic("epic_sub_test", "epic_sub_test_desc");
        manager.addEpic(epic);
        subTask.setParent(epic);
        manager.addSubTask(subTask);

        //проверить сохранилось ли в списке менеджера
        final int subtaskId = subTask.getId();
        final SubTask savedSubTask = manager.getSubTask(subtaskId);

        assertNotNull(savedSubTask, "не сохраняется задача");
        assertEquals(subTask.getId(), savedSubTask.getId(), "id подзадачи не совпадают");
        assertEquals(subTask.getTitle(), savedSubTask.getTitle(), "названия подзадачи не совпадают");
        assertEquals(subTask.getDescription(), savedSubTask.getDescription(), "описания" +
                " подзадачи не совпадают");
        assertEquals(subTask.getStatus(), savedSubTask.getStatus(), "статусы подзадачи не совпадают");
        assertEquals(subTask.getParent(), savedSubTask.getParent(), "эпики подзадачи не совпадают");

        //проверить целостность списка менеджера
        final List<SubTask> subtasks = manager.getSubTasks();

        assertNotNull(subtasks, "подзадача не сохранилась");
        assertEquals(1, subtasks.size(), "неверное количество подзадач в списке");
        assertEquals(subTask, subtasks.getFirst(), "подзадачи не совпадают");

        //проверить сохранилось ли в списке родителя
        final List<SubTask> subTasks = epic.getSubTasks();

        assertNotNull(subTasks, "нет подзадачи в списке родителя");
        assertEquals(1, subTasks.size(), "неверное количество подзадач в списке родителя");
        assertEquals(subTask, subtasks.getFirst(), "подзадачи не совпадают");
    }

    @Test
    void addSameTasksTest() {
        Task task = new Task("task1test", "task1test_desc");
        manager.addTask(task);
        manager.addTask(task);

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "Задача не найдена");
        assertEquals(1, tasks.size(), "дубликат добавился");
        assertEquals(task, tasks.getFirst(), "задачи не совпадают");
    }

    @Test
    void addSameSubTasksTest() {
        Epic epic = new Epic("epic1test", "epic1desc");
        manager.addEpic(epic);

        SubTask subTask = new SubTask("sub1test", "sub1test_desc", epic);

        manager.addSubTask(subTask);
        manager.addSubTask(subTask);

        final List<SubTask> subTasks = manager.getSubTasks();

        assertNotNull(subTasks, "подзадача не добавлена");
        assertEquals(1, subTasks.size(), "добавился дубликат");
        assertEquals(subTask, subTasks.getFirst(), "подзадачи не совпадают");
    }

    @Test
    void addSameEpicTest() {
        Epic epic = new Epic("epic1test", "epic1desc");
        manager.addEpic(epic);
        manager.addEpic(epic);

        final List<Epic> epics = manager.getEpics();

        assertNotNull(epics, "подзадача не добавлена");
        assertEquals(1, epics.size(), "добавился дубликат");
        assertEquals(epic, epics.getFirst(), "подзадачи не совпадают");
    }

    @Test
    void removeSingleTaskTest() {
        Task task = new Task("task", "task_desc");
        manager.addTask(task);

        assertEquals(1, manager.getTasks().size(), "не добавилось");

        manager.removeTask(task);

        assertEquals(0, manager.getTasks().size(), "не удалилось");
    }

    @Test
    void removeMultipleTasksIncludedIdRemovalTest() {
        Task task = new Task("task", "task_desc");
        Task task1 = new Task("task1", "task1_desc");
        manager.addTask(task);
        manager.addTask(task1);
        manager.removeTask(task1.getId());

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "удалило больше нужного");
        assertEquals(1, tasks.size(), "удалило больше нужного");
        assertEquals(task, tasks.getFirst(), "удалило не тот элемент");
    }

    @Test
    void removeAllTasksTest() {
        Task task = new Task("task", "task_desc");
        Task task1 = new Task("task1", "task1_desc");
        manager.addTask(task);
        manager.addTask(task1);

        assertNotNull(manager.getTasks());
        assertEquals(2, manager.getTasks().size(), "Задачи не добавлены");

        manager.removeAllTasks();

        assertEquals(0, manager.getTasks().size(), "удалило не всё");
    }

    @Test
    void removeSingleSubTaskTest() {
        Epic epic = new Epic("epic", "epic_desc");
        SubTask subTask = new SubTask("a", "b", epic);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        assertNotNull(manager.getSubTasks(), "подзадача не добавлена в менеджер");
        assertNotNull(epic.getSubTasks(), "подзадача не добавлена в эпик");
        assertEquals(1, manager.getSubTasks().size(), "подзадача не добавлена в менеджер");
        assertEquals(1, epic.getSubTasks().size(), "подзадача не добавлена в эпик");

        manager.removeSubTask(subTask);

        assertEquals(0, manager.getSubTasks().size(), "подзадача не удалена из менеджера");
        assertEquals(0, epic.getSubTasks().size(), "подзадача не удалена из эпика");

    }

    @Test
    void removeMultipleSubTasksIncludedIdRemovalTest() {
        Epic epic = new Epic("epic", "epic_desc");
        SubTask subTask = new SubTask("a", "b", epic);
        SubTask subTask1 = new SubTask("c", "d", epic);
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask1);

        assertNotNull(manager.getSubTasks(), "подзадачи не добавлена в менеджер");
        assertNotNull(epic.getSubTasks(), "подзадачи не добавлена в эпик");
        assertEquals(2, manager.getSubTasks().size(), "подзадачи не добавлена в менеджер");
        assertEquals(2, epic.getSubTasks().size(), "подзадачи не добавлена в эпик");

        manager.removeSubTask(subTask.getId());

        assertEquals(1, manager.getSubTasks().size(), "подзадача не удалена из менеджера");
        assertEquals(1, epic.getSubTasks().size(), "подзадача не удалена из эпика");
        assertEquals(subTask1, manager.getSubTasks().getFirst(), "из менеджера удалилась не та задача");
        assertEquals(subTask1, epic.getSubTasks().getFirst(), "из эпика " +
                "удалилась не та задача");
    }

    @Test
    void removeAllSubTaskTest() {
        Epic epic = new Epic("epic", "epic_desc");
        SubTask subTask = new SubTask("a", "b", epic);
        SubTask subTask1 = new SubTask("c", "d", epic);
        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addSubTask(subTask1);

        assertNotNull(manager.getSubTasks(), "подзадачи не добавлена в менеджер");
        assertNotNull(epic.getSubTasks(), "подзадачи не добавлена в эпик");
        assertEquals(2, manager.getSubTasks().size(), "подзадачи не добавлена в менеджер");
        assertEquals(2, epic.getSubTasks().size(), "подзадачи не добавлена в эпик");

        manager.removeAllSubTasks();

        assertEquals(0, manager.getSubTasks().size(), "подзадачи не удалены из менеджера");
        assertEquals(0, epic.getSubTasks().size(), "подзадачи не удалены из эпика");
    }

    @Test
    void removeSingleEpicTest() {
        Epic epic = new Epic("a", "b");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("c", "d", epic);
        manager.addSubTask(subTask);

        assertNotNull(manager.getEpics(), "эпик не добавлен");
        assertEquals(1, manager.getEpics().size(), "неверное количество эпиков");
        assertEquals(epic, manager.getEpic(epic.getId()), "эпик не добавлен");
        assertEquals(subTask, manager.getSubTasksByEpic(epic).getFirst(), "подзадача не добавлена в эпик");

        manager.removeEpic(epic.getId());

        assertEquals(0, manager.getSubTasks().size(), "подзадача не удалилась из менеджера");
        assertEquals(0, epic.getSubTasks().size(), "подзадача не удалилась из эпика");
        assertEquals(0, manager.getEpics().size(), "эпик не удалился из менеджера");
    }

    @Test
    void removeMultipleEpicsTest() {
        Epic epic1 = new Epic("a", "b");
        Epic epic2 = new Epic("c", "d");

        SubTask subTask1 = new SubTask("e", "f", epic1);
        SubTask subTask2 = new SubTask("g", "h", epic2);
        SubTask subTask3 = new SubTask("i", "j", epic2);

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);
        manager.addSubTask(subTask3);

        assertEquals(2, manager.getEpics().size(), "неверное количество эпиков");
        assertEquals(3, manager.getSubTasks().size(), "неверное количество подзадач");

        manager.removeEpic(epic2);

        assertEquals(1, manager.getSubTasks().size(), "удалились не все подзадачи");
        assertEquals(1, manager.getEpics().size(), "удалились не все эпики");
    }

    @Test
    void removeAllEpicsTest() {
        Epic epic1 = new Epic("a", "b");
        Epic epic2 = new Epic("c", "d");

        SubTask subTask1 = new SubTask("e", "f", epic1);
        SubTask subTask2 = new SubTask("g", "h", epic2);

        manager.addEpic(epic1);
        manager.addEpic(epic2);
        manager.addSubTask(subTask1);
        manager.addSubTask(subTask2);

        assertEquals(2, manager.getEpics().size(), "неверное количество эпиков");
        assertEquals(2, manager.getSubTasks().size(), "неверное количество подзадач");

        manager.removeAllEpics();

        assertEquals(0, manager.getSubTasks().size(), "удалились не все подзадачи");
        assertEquals(0, manager.getEpics().size(), "удалились не все эпики");
    }

    //проверьте, что InMemoryTaskManager действительно добавляет задачи разного типа и может найти их по id;
    @Test
    void addAndRetrieveDifferentTasksFromInMemoryTaskManager() {
        Epic epic = new Epic("a", "b");
        epic.setId(1);
        SubTask subTask = new SubTask("c", "d", epic);
        subTask.setId(2);
        Task task = new Task("e", "f");
        task.setId(3);

        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addTask(task);

        //убедиться что добавлены
        assertNotNull(manager.getEpics(), "эпик не добавлен");
        assertEquals(1, manager.getEpics().size(), "эпик не добавлен");

        assertNotNull(manager.getTasks(), "задача не добавлена");
        assertEquals(1, manager.getTasks().size(), "задача не добавлена");

        assertNotNull(manager.getEpics().getFirst().getSubTasks(), "подзадача не добавлена в эпик");
        assertEquals(1, manager.getEpics().getFirst().getSubTasks().size(), "подзадача не добавлена в эпик");
        assertNotNull(manager.getSubTasks(), "подзадача не добавлена в менеджер");
        assertEquals(1, manager.getSubTasks().size(), "подзадача не добавлена в менеджер");

        //убедиться что возвращаются по id
        final Task savedTask = manager.getTask(3);
        assertNotNull(savedTask, "не удалось верунть задачу по id");
        assertEquals(task.getTitle(), savedTask.getTitle(), "задача не совпала");

        final Epic savedEpic = manager.getEpic(1);
        assertNotNull(savedEpic, "не удалось вернуть эпик по id");
        assertEquals(epic.getTitle(), savedEpic.getTitle(), "эпик не совпал");

        final SubTask savedSubTask = manager.getSubTask(2);
        assertNotNull(savedSubTask, "не удалось вернуть подзадачу по id");
        assertEquals(subTask.getTitle(), savedSubTask.getTitle(), "подзадача не совпала в менеджере");
        assertEquals(subTask.getTitle(), savedEpic.getSubTasks().getFirst().getTitle(), "подзадача не " +
                "совпала в эпике");
    }

    //проверьте, что задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
    @Test
    void generatedIdsDoNotConflictWithSettledIdsForTasks() {
        Task task = new Task("a", "b");
        manager.addTask(task);

        Task task1 = new Task("c", "d");
        task1.setId(task.getId());
        manager.addTask(task1);

        final List<Task> tasks = manager.getTasks();

        assertNotNull(tasks, "задача не добавлена");
        assertEquals(1, tasks.size(), "добавилось несколько задач");
        assertEquals(task.getTitle(), tasks.getFirst().getTitle(), "задачи не совпали");
    }

    @Test
    void generatedIdsDoNotConflictWithSettledIdsForSubTasks() {
        Epic epic = new Epic("a", "b");
        SubTask subTask = new SubTask("c", "d", epic);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        SubTask subTask1 = new SubTask("c1", "d1", epic);
        subTask1.setId(subTask.getId());
        manager.addSubTask(subTask1);

        assertNotNull(manager.getSubTasks(), "подзадачи не добавлены");
        assertEquals(1, manager.getSubTasks().size(), "добавились лишние подзадачи");
        assertEquals(subTask.getTitle(), manager.getSubTasks().getFirst().getTitle(), "задачи не совпали.");
    }

    @Test
    void generatedIdsDoNotConflictWithSettledIdsForEpics() {
        Epic epic = new Epic("a", "b");
        manager.addEpic(epic);

        Epic epic1 = new Epic("c", "d");
        epic1.setId(epic.getId());
        manager.addEpic(epic1);

        assertNotNull(manager.getEpics(), "эпики не добавлены");
        assertEquals(1, manager.getEpics().size(), "неверное количество эпиков в менеджере");
        assertEquals(epic.getTitle(), manager.getEpics().getFirst().getTitle(), "добавился неверный эпик");
    }

    @Test
    void shouldNotAddAnythingIfSettledIdIsNegative() {
        Task task = new Task("a", "b");
        Epic epic = new Epic("c", "d");
        SubTask subTask = new SubTask("e", "f", epic);

        task.setId(-1);
        epic.setId(-2);
        subTask.setId(-3);

        manager.addTask(task);
        manager.addEpic(epic);
        manager.addSubTask(subTask);

        assertEquals(0, manager.getTasks().size(), "задача была добавлена");
        assertEquals(0, manager.getSubTasks().size(), "задача была добавлена");
        assertEquals(0, manager.getEpics().size(), "задача была добавлена");
    }

    @Test
    void shouldReturnHistory() {
        Task task = new Task("a", "b");
        Epic epic = new Epic("c", "d");

        manager.addTask(task);
        manager.addEpic(epic);

        manager.getEpic(epic.getId());
        manager.getTask(task.getId());

        assertNotNull(manager.getHistory(), "история не записана");
        assertEquals(2, manager.getHistory().size(), "неверное количество");
    }

    @Test
    void shouldReturnSubTasksOfAnEpic() {
        Epic epic = new Epic("a", "b");
        SubTask subTask1 = new SubTask("s1", "s1d", epic);
        SubTask subTask2 = new SubTask("s2", "s2d", epic);

        manager.addEpic(epic);
        manager.addSubTask(subTask1);

        assertEquals(2, epic.getSubTasks().size(), "неверное количество подзадач у эпика");
        assertEquals(1, manager.getSubTasksByEpic(epic).size(), "неверное количество подзадач" +
                "по методу менеджера");

        manager.addSubTask(subTask2);

        assertEquals(2, manager.getSubTasksByEpic(epic).size(), "неверное количество подзадач" +
                "по методу менеджера");
    }
}