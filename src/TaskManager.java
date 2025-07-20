import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private static int counter = 0;
    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();

    public static void addTask(Task task) {
        tasks.put(++counter, task);
        task.setId(counter);
        System.out.println("Задача с id: " + counter + " была добавлена.");
    }

    public static void addSubTask(SubTask subTask) {
        subTasks.put(++counter, subTask);
        subTask.setId(counter);
        System.out.println("Подзадача с id: " + counter + " была добавлена.");
    }

    public static void addEpic(Epic epic) {
        epics.put(++counter, epic);
        epic.setId(counter);
        System.out.println("Эпик с id: " + counter + " был добавлен.");
    }

    public static void removeTask(Task task) {
        if (tasks.remove(task.getId()) != null)
            System.out.println("Задача с id " + task.getId() + " удалена.");
        else
            System.out.println("Такой задачи нет.");
    }

    public static void removeSubTask(SubTask subTask) {
        subTask.getParent().removeSubTask(subTask);
        System.out.println("Задача с id " + subTask.getId() + " удалена из списка подзадач эпика.");
        subTasks.remove(subTask.getId());
        System.out.println("Задача с id " + subTask.getId() + " удалена из списка подзадач менеджера.");
    }

    public static void removeEpic(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            subTasks.remove(subTask.getId());
        }
        System.out.println("Удалены подзадачи эпика с id " + epic.getId() + ".");
        epics.remove(epic.getId());
        System.out.println("Эпик с id " + epic.getId() + " удалён.");
    }

    public static ArrayList<Task> getAllTasks() {
        if (!tasks.isEmpty())
            return new ArrayList<>(tasks.values());
        else
            return null;
    }

    public static ArrayList<SubTask> getAllSubTasks() {
        if (!subTasks.isEmpty())
            return new ArrayList<>(subTasks.values());
        else
            return null;
    }

    public static ArrayList<Epic> getAllEpics() {
        if (!epics.isEmpty())
            return new ArrayList<>(epics.values());
        else
            return null;
    }

    public static void removeAllTasks() {
        tasks.clear();
        System.out.println("Список всех задач очищен.");
    }

    public static void removeAllSubTasks() {
        for (SubTask subTask : subTasks.values()) {
            subTask.getParent().removeSubTask(subTask);
        }
        System.out.println("Все подзадачи удалены из списков подзадач соответствующих эпиков.");
        subTasks.clear();
        System.out.println("Список всех подзадач очищен.");
    }

    public static void removeAllEpics() {
        for (Epic epic : epics.values()) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getId());                  //удалить из списка менеджера
                epic.removeSubTask(subTask);                       //удалить из самого эпика
            }
        }
        System.out.println("Список всех подзадач эпиков очищен.");
        epics.clear();
        System.out.println("Список всех эпиков очищен.");
    }

    public static Task getTaskById(int id) {
        if (tasks.get(id) == null)
            System.out.println("Не найдена такая задача.");
        return tasks.get(id);
    }

    public static SubTask getSubTaskById(int id) {
        if (subTasks.get(id) == null)
            System.out.println("Не найдена такая подзадача.");
        return subTasks.get(id);
    }

    public static Epic getEpicById(int id) {
        if (epics.get(id) == null)
            System.out.println("Не найдена такой эпик.");
        return epics.get(id);
    }

    public static void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
            System.out.println("Задача с id " + task.getId() + " обновлена.");
        } else {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
        }
    }

    public static void updateSubTask(SubTask subTask) {
        if (subTasks.containsKey(subTask.getId())) {
            subTasks.put(subTask.getId(), subTask);
            System.out.println("Подзадача с id " + subTask.getId() + " обновлена.");
        } else {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
        }
    }

    public static void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            System.out.println("Эпик с id " + epic.getId() + " обновлён.");
        } else {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
        }
    }

    public static void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача с id " + id + " удалена.");
        } else {
            System.out.println("Нет задачи с id: " + id);
        }
    }

    public static void removeSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            subTask.getParent().removeSubTask(subTask);
            System.out.println("Задача с id " + id + " удалена из списка подзадач эпика.");
            subTasks.remove(id);
            System.out.println("Задача с id " + id + " удалена из списка подзадач менеджера.");
        } else {
            System.out.println("Нет подзадачи с id: " + id);
        }
    }

    public static void removeEpicById(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for(SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getId());
            }
            System.out.println("Удалены подзадачи эпика с id " + id + ".");
            epics.remove(id);
            System.out.println("Эпик с id " + id + " удалён.");
        } else {
            System.out.println("Нет эпика с id: " + id);
        }
    }

    public static ArrayList<SubTask> getSubTasksByEpic(Epic epic) {
        if (epics.containsKey(epic.getId()))
            return epics.get(epic.getId()).getSubTasks();
        return null;
    }
}
