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
}