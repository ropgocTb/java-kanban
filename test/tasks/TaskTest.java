package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    //проверьте, что экземпляры класса Task равны друг другу, если равен их id;
    @Test
    void taskEqualTest() {
        Task task1 = new Task("task1", "task1_desc");
        Task task2 = new Task("task2", "task2_desc");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи должны быть равны если равны их id");
    }

    //валидные данные
    @Test
    public void isOverlappingTestOnEnd() {
        TaskManager manager = Managers.getInMemory();

        Task task1 = new Task("task1", "task1_desc",
                LocalDateTime.of(2025, 9, 30, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);
        Task task2 = new Task("task2", "task2_desc",
                LocalDateTime.of(2025, 9, 30, 10, 30), Duration.ofMinutes(30));
        task2.setId(2);

        assertFalse(task1.isOverlapping(task2), "Задачи не должны пересекаться на границах");
        assertFalse(task2.isOverlapping(task1), "Задачи не должны пересекаться на границах");
    }

    @Test
    public void isOverlappingTestOnStart() {
        Task task1 = new Task("task1", "task1_desc",
                LocalDateTime.of(2025, 9, 30, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);
        Task task2 = new Task("task2", "task2_desc",
                LocalDateTime.of(2025, 9, 30, 9, 30), Duration.ofMinutes(30));
        task2.setId(2);

        assertFalse(task1.isOverlapping(task2), "Задачи не должны пересекаться на границах");
        assertFalse(task2.isOverlapping(task1), "Задачи не должны пересекаться на границах");
    }

    @Test
    public void isOverlappingTest() {
        Task task1 = new Task("task1", "task1_desc",
                LocalDateTime.of(2025, 9, 30, 10, 0), Duration.ofMinutes(30));
        task1.setId(1);
        Task task2 = new Task("task2", "task2_desc",
                LocalDateTime.of(2025, 9, 30, 10, 20), Duration.ofMinutes(30));
        task2.setId(2);

        assertTrue(task1.isOverlapping(task2), "Задачи не должны пересекаться на границах");
        assertTrue(task2.isOverlapping(task1), "Задачи не должны пересекаться на границах");
    }
}