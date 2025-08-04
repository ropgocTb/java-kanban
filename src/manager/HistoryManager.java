package manager;

import tasks.Task;

import java.util.List;

public interface HistoryManager {
    void addTask(Task task);

    void removeTask(int id);

    List<Task> getHistory();
}