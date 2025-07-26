package tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    //проверьте, что наследники класса Task равны друг другу, если равен их id;
    @Test
    void subTaskEqualTest() {
        SubTask subTask1 = new SubTask("subtask1", "subtask1_desc");
        SubTask subTask2 = new SubTask("subtask2", "subtask2_desc");

        subTask1.setId(1);
        subTask2.setId(1);

        assertEquals(subTask1, subTask2, "Эпики должны быть равны если равны их id");
    }

    //проверьте, что объект Subtask нельзя сделать своим же эпиком;
    @Test
    void subTaskAsItsOwnParentTest() {
        SubTask subTask1 = new SubTask("subTask1", "subTask1_desc");
        Epic subTask1epic = new Epic("123", "123");
        subTask1epic.setId(subTask1.getId());
        subTask1.setParent(subTask1epic);
        assertNull(subTask1.getParent());
    }
}