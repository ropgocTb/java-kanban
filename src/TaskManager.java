import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int counter;
    private static HashMap<Integer, Task> tasks;

    public TaskManager() {
        counter = 0;
        tasks = new HashMap<>();
    }

    public static void addTask(Task task) {
        tasks.put(++counter, task);
        task.setId(counter);
        System.out.println("Задача с id: " + counter + " была добавлена.");
    }

    public static void removeTask(Task task) {
        if (task instanceof SubTask) {
            SubTask subTask = (SubTask) task;
            subTask.getParent().removeSubTask(subTask);
            System.out.println("Задача с id " + task.getId() + " удалена из списка подзадач эпика.");
            tasks.remove(task.getId());
            System.out.println("Задача с id " + task.getId() + " удалена из списка задач менеджера.");
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            for (SubTask subTask : epic.getSubTasks()) {
                tasks.remove(subTask.getId());
            }
            System.out.println("Удалены подзадачи эпика с id " + task.getId() + ".");
            tasks.remove(task.getId());
            System.out.println("Эпик с id " + task.getId() + " удалён.");
        } else {
            tasks.remove(task.getId());
            System.out.println("Задача с id " + task.getId() + " удалена.");
        }
    }

    public static ArrayList<Task> getAllTasks() {
        if (!tasks.isEmpty())
            return new ArrayList<>(tasks.values());
        else
            return null;
    }

    public static void removeAllTasks() {
        tasks.clear();
        System.out.println("Список всех задач очищен.");
    }

    public static Task getTaskById(int id) {
        return tasks.get(id);
    }

    public static void updateTask(Task task) {// в локальном случае нет от этого никакого смысла ссылки то одинаковые
        tasks.put(task.getId(), task);
        System.out.println("Задача с id " + task.getId() + " обновлена.");
    }

    public static void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            if (tasks.get(id) instanceof SubTask) {
                SubTask subTask = (SubTask) tasks.get(id);
                subTask.getParent().removeSubTask(subTask);
                System.out.println("Задача с id " + id + " удалена из списка подзадач эпика.");
                tasks.remove(id);
                System.out.println("Задача с id " + id + " удалена из списка задач менеджера.");
                return;
            } else if (tasks.get(id) instanceof Epic) {
                Epic epic = (Epic) tasks.get(id);
                for(SubTask subTask : epic.getSubTasks()) {
                    tasks.remove(subTask.getId());
                }
                System.out.println("Удалены подзадачи эпика с id " + id + ".");
                tasks.remove(id);
                System.out.println("Эпик с id " + id + " удалён.");
                return;
            }
            tasks.remove(id);
            System.out.println("Задача с id " + id + " удалена.");
        } else {
            System.out.println("Нет задачи с id: " + id);
        }
    }

    public static ArrayList<SubTask> getSubTasksByEpic(Epic epic) {
        return epic.getSubTasks();
    }
}
