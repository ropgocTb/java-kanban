package manager;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int counter = 0;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager history = Managers.getDefaultHistory();
    protected final Set<Task> sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));


    private int initId(Task task) {
        int id = task.getId();
        if (id < 0) {
            System.out.println("Недопустимый id, id должен быть >= 1");
            return -1;
        } else if (id == 0) {
            System.out.println("id не инициализирован, будет сгенерирован новый.");
            do {
                id = ++counter;
            } while (this.tasks.containsKey(id) || this.subTasks.containsKey(id) || this.epics.containsKey(id));
            return id;
        }
        if (this.tasks.containsKey(id) || this.subTasks.containsKey(id) || this.epics.containsKey(id)) {
            System.out.println("Задача/подзадача/эпик с таким id уже есть.");
            return -1;
        }
        return id;
    }

    private boolean isOverlappingWithAny(Task task) {
        return getPrioritizedTasks().stream()
                .anyMatch(t -> t.isOverlapping(task));
    }

    @Override
    public void addTask(Task task) {
        if (task == null) {
            System.out.println("null нельзя добавлять");
            return;
        }

        if (isOverlappingWithAny(task)) {
            System.out.println("Задача пересекается по времени с другой задачей в менеджере, задача не была добавлена");
            return;
        }

        final int id = initId(task);
        if (id == -1) return;

        task.setId(id);
        tasks.put(id, new Task(task));
        System.out.println("Задача с id: " + id + " была добавлена.");

        if (task.getStartTime() == null || task.getDuration() == null) {
            System.out.println("Корректно установите дату начала, длительность и обновите задачу" +
                    " для установки приоритета.");
        } else {
            sortedTasks.add(new Task(task));
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("null нельзя добавлять");
            return;
        }

        final Epic epic = epics.get(subTask.getParent());
        if (epic == null) {
            System.out.println("У подзадачи нет эпика, требуется setParent()");
            return;
        }

        if (!epics.containsKey(epic.getId())) {
            System.out.println("Эпик подзадачи не добавлен в менеджере");
            return;
        }

        if (isOverlappingWithAny(subTask)) {
            System.out.println("Задача пересекается по времени с другой задачей в менеджере, " +
                    "задача не была добавлена");
            return;
        }

        final int id = initId(subTask);
        if (id == -1) return;

        subTask.setId(id);
        subTasks.put(id, new SubTask(subTask));
        epic.addSubTask(subTask);
        updateEpicMetrics(epic);
        System.out.println("Подзадача с id: " + id + " была добавлена.");

        if (subTask.getStartTime() == null || subTask.getDuration() == null) {
            System.out.println("Корректно установите дату начала, длительность и обновите задачу" +
                    " для установки приоритета.");
        } else {
            sortedTasks.add(new SubTask(subTask));
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

        if (tasks.remove(task.getId()) == null) {
            System.out.println("Нет задачи с id: " + task.getId());
            return;
        }

        history.removeTask(task.getId());
        sortedTasks.remove(task);
        System.out.println("Задача с id " + task.getId() + " удалена.");
    }

    @Override
    public void removeTask(int id) {
        sortedTasks.remove(tasks.get(id));

        if (tasks.remove(id) == null) {
            System.out.println("Нет задачи с id: " + id);
            return;
        }

        history.removeTask(id);
        System.out.println("Задача с id " + id + " удалена.");
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("null нельзя удалять");
            return;
        }

        if (!subTasks.containsKey(subTask.getId())) {
            System.out.println("Нет подзадачи с id: " + subTask.getId());
            return;
        }

        Epic epic = epics.get(subTask.getParent());
        epic.removeSubTask(subTask);
        updateEpicMetrics(epic);

        subTasks.remove(subTask.getId());
        history.removeTask(subTask.getId());
        sortedTasks.remove(subTask);

        System.out.println("Задача с id " + subTask.getId() + " удалена из списка подзадач эпика.");
        System.out.println("Задача с id " + subTask.getId() + " удалена из списка подзадач менеджера.");
    }

    @Override
    public void removeSubTask(int id) {
        if (!subTasks.containsKey(id)) {
            System.out.println("Нет подзадачи с id: " + id);
            return;
        }

        SubTask subTask = subTasks.get(id);

        Epic epic = epics.get(subTask.getParent());
        epic.removeSubTask(subTask);
        updateEpicMetrics(epic);

        subTasks.remove(id);
        history.removeTask(id);
        sortedTasks.remove(subTask);

        System.out.println("Задача с id " + id + " удалена из списка подзадач эпика.");
        System.out.println("Задача с id " + id + " удалена из списка подзадач менеджера.");
    }

    @Override
    public void removeEpic(Epic epic) {
        if (epic == null) {
            System.out.println("null нельзя удалять");
            return;
        }

        if (!epics.containsKey(epic.getId())) {
            System.out.println("Нет эпика с id: " + epic.getId());
            return;
        }

        for (Integer subTaskId : getEpic(epic.getId()).getSubTasks()) {
            sortedTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
            history.removeTask(subTaskId);
        }

        getEpic(epic.getId()).clearSubTasks();
        epics.remove(epic.getId());
        history.removeTask(epic.getId());

        System.out.println("Удалены подзадачи эпика с id " + epic.getId() + ".");
        System.out.println("Эпик с id " + epic.getId() + " удалён.");
    }

    @Override
    public void removeEpic(int id) {
        if (!epics.containsKey(id)) {
            System.out.println("Нет эпика с id: " + id);
            return;
        }

        for (Integer subTaskId : getEpic(id).getSubTasks()) {
            sortedTasks.remove(subTasks.get(subTaskId));
            subTasks.remove(subTaskId);
            history.removeTask(subTaskId);
        }

        getEpic(id).clearSubTasks();
        System.out.println("Удалены подзадачи эпика с id " + id + ".");
        epics.remove(id);
        System.out.println("Эпик с id " + id + " удалён.");
        history.removeTask(id);
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
        tasks.values().forEach(sortedTasks::remove);
        tasks.clear();
        System.out.println("Список всех задач очищен.");
    }

    @Override
    public void removeAllSubTasks() {
        subTasks.values().forEach(sortedTasks::remove);
        for (Epic epic : epics.values()) {
            epic.clearSubTasks();
        }
        System.out.println("Все подзадачи удалены из списков подзадач соответствующих эпиков.");
        subTasks.clear();
        System.out.println("Список всех подзадач очищен.");
    }

    @Override
    public void removeAllEpics() {
        subTasks.values().forEach(sortedTasks::remove);
        for (Epic epic : epics.values()) {
            for (Integer subTaskId : epic.getSubTasks()) {
                subTasks.remove(subTaskId);                  //удалить из списка менеджера
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
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
            return;
        }

        if (isOverlappingWithAny(task)) {
            System.out.println("Задача пересекается с другой по времени и не может быть обновлена.");
            return;
        }

        Task currentTask = getTask(task.getId());

        if (sortedTasks.contains(currentTask)) {
            sortedTasks.remove(currentTask);
            if (task.getStartTime() == null || task.getDuration() == null) {
                System.out.println("Новое время установлено некорректно, задача удалена из приоритетного списка");
            } else {
                sortedTasks.add(new Task(task));
            }
        }

        tasks.put(task.getId(), new Task(task));
        System.out.println("Задача с id " + task.getId() + " обновлена.");
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("null нельзя обновить");
            return;
        }
        if (!subTasks.containsKey(subTask.getId())) {
            System.out.println("Задачи нет в списке, поэтому она не может быть обновлена.");
            return;
        }

        if (isOverlappingWithAny(subTask)) {
            System.out.println("Задача пересекается с другой по времени и не может быть обновлена.");
            return;
        }

        SubTask currentSubTask = getSubTask(subTask.getId());

        if (sortedTasks.contains(currentSubTask)) {
            sortedTasks.remove(currentSubTask);
            if (subTask.getStartTime() == null || subTask.getDuration() == null) {
                System.out.println("обновленное время установлено некорректно, задача удалена из приоритетного списка");
            } else {
                sortedTasks.add(new SubTask(subTask));
            }
        }

        subTasks.put(subTask.getId(), new SubTask(subTask));

        Epic epic = epics.get(subTask.getParent());
        updateEpicMetrics(epic);

        System.out.println("Подзадача с id " + subTask.getId() + " обновлена.");
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null) {
            System.out.println("null нельзя обновить");
            return;
        }
        if (epics.containsKey(epic.getId())) {
            //проверить есть ли в эпике задачи, которые не добавлены в менеджер
            for (Integer subTaskId : epic.getSubTasks()) {
                if (!subTasks.containsKey(subTaskId)) {
                    System.out.println("Добавьте новые подзадачи в менеджер");
                    return;
                }
            }

            //проверить остались ли для этого эпика задачи которые должны быть удалены из менеджера
            List<SubTask> subTasksToRemove = new ArrayList<>();
            for (SubTask subTask : subTasks.values()) {
                if (subTask.getParent() == epic.getId() && !epic.getSubTasks().contains(subTask.getId()))
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
        List<SubTask> existingSubTasks = new ArrayList<>();

        if (epic == null) {
            System.out.println("передан параметр null, возвращается пустой список");
            return existingSubTasks;
        }
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Нет эпика с id: " + epic.getId() + " возвращается пустой список");
            return existingSubTasks;
        }

        existingSubTasks = epics.get(epic.getId()).getSubTasks().stream()
                .filter(subTasks::containsKey)
                .map(subTasks::get)
                .toList();

        return existingSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }

    @Override
    public TaskStatus getEpicStatus(Epic epic) {
        List<SubTask> subTasks = this.subTasks.entrySet().stream()
                .filter(st -> getEpic(epic.getId()).getSubTasks().contains(st.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        if (subTasks.isEmpty()) {
            return TaskStatus.NEW;
        }
        boolean allDone = subTasks.stream().allMatch(subTask -> subTask.getStatus() == TaskStatus.DONE);
        boolean allNew = subTasks.stream().allMatch(subTask -> subTask.getStatus() == TaskStatus.NEW);

        if (allDone) {
            return TaskStatus.DONE;
        } else if (allNew) {
            return TaskStatus.NEW;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    public Optional<LocalDateTime> getEpicStartTime(Epic epic) {
        return subTasks.values().stream()
                .filter(subTask -> subTask.getParent() == epic.getId())
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);
    }

    public Optional<LocalDateTime> getEpicEndTime(Epic epic) {
        return subTasks.values().stream()
                .filter(st -> st.getParent() == epic.getId())
                .flatMap(st -> st.getEndTime().stream())
                .max(LocalDateTime::compareTo);
    }

    public Optional<Duration> getEpicDuration(Epic epic) {
        return subTasks.values().stream()
                .filter(st -> st.getParent() == epic.getId())
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration::plus);
    }

    public void updateEpicMetrics(Epic epic) {
        epic.setStatus(getEpicStatus(epic));
        getEpicStartTime(epic).ifPresent(epic::setStartTime);
        getEpicEndTime(epic).ifPresent(epic::setEndTime);
        getEpicDuration(epic).ifPresent(epic::setDuration);
    }
}
