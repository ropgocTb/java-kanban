package tasks;

import manager.Managers;
import manager.TaskManager;
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
        TaskManager manager = Managers.getDefault();
        Epic epic = new Epic("epic1", "epic1_desc");
        manager.addEpic(epic);

        assertEquals(TaskStatus.NEW, epic.getStatus(), "статус после создания должен быть NEW");

        SubTask subTask = new SubTask("sub1", "sub1_desc", epic);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubTask(subTask);

        assertEquals(TaskStatus.IN_PROGRESS, manager.getEpicStatus(epic), "статус не изменился");

        SubTask subTask1 = new SubTask("sub2", "sub2_desc", epic);
        subTask1.setStatus(TaskStatus.DONE);
        subTask.setStatus(TaskStatus.DONE);
        manager.addSubTask(subTask1);
        manager.updateSubTask(subTask);

        assertEquals(TaskStatus.DONE, manager.getEpicStatus(epic), "статус не изменился");

        manager.getEpic(epic.getId()).setStatus(TaskStatus.NEW);

        assertNotEquals(TaskStatus.NEW, manager.getEpicStatus(epic), "статус изменился после setStatus()");
    }
}