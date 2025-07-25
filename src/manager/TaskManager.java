package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addTask(Task task);

    void addSubTask(SubTask subTask);

    void addEpic(Epic epic);

    void removeTask(Task task);

    void removeSubTask(SubTask subTask);

    void removeEpic(Epic epic);

    ArrayList<Task> getTasks();

    ArrayList<SubTask> getSubTasks();

    ArrayList<Epic> getEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subTask);

    void updateEpic(Epic epic);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    ArrayList<SubTask> getSubTasksByEpic(Epic epic);

    List<Task> getHistory();
}
