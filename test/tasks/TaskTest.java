package tasks;

import org.junit.jupiter.api.Test;

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
}