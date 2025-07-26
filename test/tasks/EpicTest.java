package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    //проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    void epicEqualTest() {
        Epic epic1 = new Epic("epic1", "epic1_desc");
        Epic epic2 = new Epic("epic2", "epic2_desc");

        epic1.setId(1);
        epic2.setId(1);

        assertEquals(epic1, epic2, "Эпики должны быть равны если равны их id");
    }

    //проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
    @Test
    void epicAsSubTaskOfItselfTest() {
        Epic epic1 = new Epic("epic1", "epic1_desc");
        SubTask subTask = new SubTask("123", "123");
        subTask.setId(epic1.getId());
        epic1.addSubTask(subTask);
        assertEquals(0, epic1.getSubTasks().size(), "эпик добавился в качестве подзадачи");
    }

    @Test
    void epicStatusTest() {
        Epic epic = new Epic("epic1", "epic1_desc");
        epic.setId(1);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "статус после создания должен быть NEW");

        SubTask subTask = new SubTask("sub1", "sub1_desc", epic);
        subTask.setId(2);
        subTask.setStatus(TaskStatus.IN_PROGRESS);

        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "статус не изменился");

        SubTask subTask1 = new SubTask("sub2", "sub2_desc", epic);
        subTask.setId(3);
        subTask1.setStatus(TaskStatus.DONE);
        subTask.setStatus(TaskStatus.DONE);

        assertEquals(TaskStatus.DONE, epic.getStatus(), "статус не изменился");

        epic.setStatus(TaskStatus.NEW);

        assertNotEquals(TaskStatus.NEW, epic.getStatus(), "что то очень не так");
    }
}