package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    void addNewTaskManagerTest() {
        assertNotNull(Managers.getDefault(), "TaskManager не инициализирован.");
    }

    @Test
    void addNewHistoryManagerTest() {
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager не инициализирован.");
    }
}