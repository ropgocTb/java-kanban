package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager history = Managers.getDefaultHistory();


    private int initId(Task task) {
        int id = task.getId();
        if (id < 0) {
            System.out.println("Недопустимый id, id должен быть >= 1");
            return -1;
        } else if (id == 0) {
            System.out.println("id не инициализирован, будет сгенерирован новый.");
            id = ++counter;
            return id;
        }
        if (tasks.containsKey(id) || subTasks.containsKey(id) || epics.containsKey(id)) {
            System.out.println("Задача/подзадача/эпик с таким id уже есть.");
            return -1;
        }
        return id;
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("null нельзя добавлять");
            return;
        }
        final int id = initId(task);
        if (id == -1) return;
        task.setId(id);
        tasks.put(id, new Task(task));
        System.out.println("Задача с id: " + id + " была добавлена.");
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("null нельзя добавлять");
            return;
        }
        final Epic epic = subTask.getParent();
        final int id = initId(subTask);
        if (id == -1) return;
        if (epic != null) {
            if (epics.containsKey(epic.getId())) {
                subTask.setId(id);
                epics.get(epic.getId()).addSubTask(subTask);
                subTasks.put(id, new SubTask(subTask));
                System.out.println("Подзадача с id: " + id + " была добавлена.");
            } else {
                System.out.println("Эпик подзадачи не добавлен в менеджере");
            }
        } else {
            System.out.println("У подзадачи нет эпика, требуется setParent()");
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic == null) {
            System.out.println("null нельзя добавлять");
            return;
        }
        final int id = initId(epic);
        if (id == -1) return;
        epic.setId(id);
        epics.put(id, new Epic(epic));
        System.out.println("Эпик с id: " + epic.getId() + " был добавлен.");
    }

    @Override
    public void removeTask(Task task) {
        if (task == null) {
            System.out.println("null нельзя удалять");
            return;
        }
        if (tasks.remove(task.getId()) != null) {
            System.out.println("Задача с id " + task.getId() + " удалена.");
            history.removeTask(task.getId());
        } else {
            System.out.println("Нет задачи с id: " + task.getId());
        }
    }

    @Override
    public void removeTask(int id) {
        if (tasks.remove(id) != null) {
            System.out.println("Задача с id " + id + " удалена.");
            history.removeTask(id);
        } else {
            System.out.println("Нет задачи с id: " + id);
        }
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("null нельзя удалять");
            return;
        }
        if (subTasks.containsKey(subTask.getId())) {
            epics.get(subTask.getParent().getId()).removeSubTask(subTask);
            System.out.println("Задача с id " + subTask.getId() + " удалена из списка подзадач эпика.");
            subTasks.remove(subTask.getId());
            System.out.println("Задача с id " + subTask.getId() + " удалена из списка подзадач менеджера.");
            history.removeTask(subTask.getId());
        } else {
            System.out.println("Нет подзадачи с id: " + subTask.getId());
        }
    }

    @Override
    public void removeSubTask(int id) {
        if (subTasks.containsKey(id)) {
            SubTask subTask = subTasks.get(id);
            epics.get(subTask.getParent().getId()).removeSubTask(subTask);
            System.out.println("Задача с id " + id + " удалена из списка подзадач эпика.");
            subTasks.remove(id);
            System.out.println("Задача с id " + id + " удалена из списка подзадач менеджера.");
            history.removeTask(id);
        } else {
            System.out.println("Нет подзадачи с id: " + id);
        }
    }

    @Override
    public void removeEpic(Epic epic) {
        if (epic == null) {
            System.out.println("null нельзя удалять");
            return;
        }

        if (epics.containsKey(epic.getId())) {
            for (SubTask subTask : getEpic(epic.getId()).getSubTasks()) {
                subTasks.remove(subTask.getId());
                history.removeTask(subTask.getId());
            }
            getEpic(epic.getId()).clearSubTasks();
            System.out.println("Удалены подзадачи эпика с id " + epic.getId() + ".");
            epics.remove(epic.getId());
            System.out.println("Эпик с id " + epic.getId() + " удалён.");
            history.removeTask(epic.getId());
        } else {
            System.out.println("Нет эпика с id: " + epic.getId());
        }
    }

    @Override
    public void removeEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);
            for (SubTask subTask : getEpic(epic.getId()).getSubTasks()) {
                subTasks.remove(subTask.getId());
                history.removeTask(subTask.getId());
            }
            getEpic(epic.getId()).clearSubTasks();
            System.out.println("Удалены подзадачи эпика с id " + id + ".");
            epics.remove(id);
            System.out.println("Эпик с id " + id + " удалён.");
            history.removeTask(id);
        } else {
            System.out.println("Нет эпика с id: " + id);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Список всех задач очищен.");
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
        }
        System.out.println("Все подзадачи удалены из списков подзадач соответствующих эпиков.");
        subTasks.clear();
        System.out.println("Список всех подзадач очищен.");
    }

    @Override
    public void removeAllEpics() {
        for (Epic epic : epics.values()) {
            for (SubTask subTask : epic.getSubTasks()) {
                subTasks.remove(subTask.getId());                  //удалить из списка менеджера
            }
            epic.clearSubTasks();                                  //удалить из эпика
        }
        System.out.println("Подзадачи удаленных эпиков удалены из менеджера.");
        epics.clear();
        System.out.println("Список всех эпиков очищен.");
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null) {
            System.out.println("Не найдена такая задача.");
        } else {
            history.addTask(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTask(int id) {
        if (subTasks.get(id) == null) {
            System.out.println("Не найдена такая подзадача.");
        } else {
            history.addTask(subTasks.get(id));
        }
        return subTasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) == null) {
            System.out.println("Не найден такой эпик.");
        } else {
            history.addTask(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null) {
            System.out.println("null нельзя обновить");
            return;
        }
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), new Task(task));
            System.out.println("Задача с id " + task.getId() + " обновлена.");
        } else {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("null нельзя обновить");
            return;
        }
        if (subTasks.containsKey(subTask.getId())) {
            Epic epic = epics.get(subTask.getParent().getId());
            epic.updateSubTask(subTask);
            subTasks.put(subTask.getId(), new SubTask(subTask));
            System.out.println("Подзадача с id " + subTask.getId() + " обновлена.");
        } else {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("null нельзя обновить");
            return;
        }
        if (epics.containsKey(epic.getId())) {
            //проверить есть ли в эпике задачи, которые не добавлены в менеджер
            for (SubTask subTask : epic.getSubTasks()) {
                if (!subTasks.containsKey(subTask.getId()))
                    addSubTask(subTask);
            }

            //проверить остались ли для этого эпика задачи которые должны быть удалены из менеджера
            List<SubTask> subTasksToRemove = new ArrayList<>();
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getParent().getId() == epic.getId() && !epic.getSubTasks().contains(subTask))
                    subTasksToRemove.add(subTask);
            }

            for (SubTask subTask : subTasksToRemove) {
                removeSubTask(subTask);
            }
            epics.put(epic.getId(), new Epic(epic));
            System.out.println("Эпик с id " + epic.getId() + " обновлён.");
        } else {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
        }
    }

    //теперь менеджер возаращает только те подзадачи эпика, которые явно были добавлены
    @Override
    public List<SubTask> getSubTasksByEpic(Epic epic) {
        if (epic == null) {
            System.out.println("передан параметр null, возвращается null");
            return null;
        }
        List<SubTask> existingSubTasks = new ArrayList<>();
        if (epics.containsKey(epic.getId())) {
            Epic epic1 = epics.get(epic.getId());
            for (SubTask subTask : epic1.getSubTasks()) {
                if (subTasks.containsKey(subTask.getId()))
                    existingSubTasks.add(subTask);
            }
        } else {
            System.out.println("Нет эпика с id: " + epic.getId());
        }
        return existingSubTasks;
    }

    public List<Task> getHistory() {
        return history.getHistory();
    }
}
