package manager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getFileBacked() {
        return new FileBackedTaskManager();
    }

    public static InMemoryTaskManager getInMemory() {
        return new InMemoryTaskManager();
    }
}
