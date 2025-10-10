package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    public InMemoryTaskManager initTaskManager() {
        return Managers.getInMemory();
    }

    // тест на проверку пересечения интервалов времени
    //Корректный расчет пересечения временных интервалов
    @Test
    public void timeIntervalsIntersectTest() {
        Task task1 = new Task("task1", "task1_desc",
                LocalDateTime.of(2025, 9, 30, 10, 0), Duration.ofMinutes(30));
        manager.addTask(task1);
        Task task2 = new Task("task1", "task1_desc",
                LocalDateTime.of(2025, 9, 30, 10, 0), Duration.ofMinutes(30));
        assertThrows(OverlapException.class, () -> manager.addTask(task2));

        assertEquals(1, manager.getTasks().size(), "Задача пересекающаяся по времени добавилась");

        Epic epic = new Epic("epic", "epic_sub");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("sub", "sub_desc",
                LocalDateTime.of(2025, 9, 30, 10, 0), Duration.ofMinutes(30), epic);
        assertThrows(OverlapException.class, () -> manager.addSubTask(subTask));

        assertEquals(0, manager.getSubTasks().size(), "подзадача которая пересекается по времени с" +
                " другой задачей добавилась в менеджер");
    }

    @Test
    public void epicSetStartTimeAndDurationTest() {
        Epic epic = new Epic("epic", "epic_desc");
        manager.addEpic(epic);
        SubTask subTask1 = new SubTask("sub1", "sub1_desc",
                LocalDateTime.of(2025, 10, 1, 0, 0,0),
                Duration.ofMinutes(45), epic);
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("sub2", "sub2_desc",
                subTask1.getStartTime().plusMinutes(45), Duration.ofMinutes(45), epic);
        manager.addSubTask(subTask2);

        assertEquals(manager.getEpic(epic.getId()).getStartTime(), subTask1.getStartTime(),
                "неверное время начала эпика");
        assertEquals(manager.getEpic(epic.getId()).getDuration(), subTask1.getDuration().plus(subTask2.getDuration()),
                "длительность эпика неверная");
        assertEquals(manager.getEpic(epic.getId()).getEndTime(), subTask2.getEndTime(),
                "конец времени выполнения у эпика неверный");
    }

    @Test
    public void updateStartTimeTest() {
        Task task = new Task("task", "task_desc", LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);

        task.setStartTime(LocalDateTime.now().plusMinutes(15));
        manager.updateTask(task);

        assertEquals(task.getStartTime(), manager.getPrioritizedTasks().getFirst().getStartTime(), "задача " +
                "не обновилась в sortedTasks");
    }

    //при обновлении времени с корректного на null задача должна удаляться из sortedTasks
    @Test
    public void updateStartTimeNullTest() {
        Task task = new Task("task", "task_desc", LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);

        task.setStartTime(null);
        manager.updateTask(task);

        assertEquals(0, manager.getPrioritizedTasks().size(), "задача " +
                "не обновилась в sortedTasks");
    }

    @Test
    public void updateWithOrderTest() {
        Task task = new Task("a", "b", LocalDateTime.now(), Duration.ofMinutes(15));
        manager.addTask(task);
        Epic epic = new Epic("a", "b");
        manager.addEpic(epic);
        SubTask subTask = new SubTask("c", "d", LocalDateTime.now().plusMinutes(15),
                Duration.ofMinutes(15), epic);
        manager.addSubTask(subTask);

        System.out.println(manager.getPrioritizedTasks());
        assertEquals(task, manager.getPrioritizedTasks().getFirst(), "порядок приоритета неверный");
        assertEquals(subTask, manager.getPrioritizedTasks().getLast(), "порядок приоритета неверный");

        subTask.setStartTime(subTask.getStartTime().minusMinutes(31));
        manager.updateSubTask(subTask);

        assertEquals(2, manager.getPrioritizedTasks().size(), "элементов не 2");
        System.out.println(manager.getPrioritizedTasks());
        assertEquals(subTask.getStartTime(), manager.getPrioritizedTasks().getFirst().getStartTime(), "время" +
                " не обновилось в sortedTasks");
    }
}