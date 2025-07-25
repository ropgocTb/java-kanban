package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(Task subTask) {
        if (subTask instanceof SubTask)
            this.subTasks.add((SubTask)subTask);
        else
            System.out.println("Подзадачей может быть только подзадача");
    }

    public void removeSubTask(SubTask subTask) {
        this.subTasks.remove(subTask);
    }

    public void clearSubTasks() {
        this.subTasks.clear();
    }

    @Override
    public TaskStatus getStatus() {
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
    public void setStatus(TaskStatus status) {
        System.out.println("Статус эпика менять нельзя.");
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("tasks.Epic{" +
                "id=" + this.getId() + "; " +
                "description=" + this.getDescription() + "; " +
                "status=" + this.getStatus() + "; " +
                "subTasksIds=[");
        for (SubTask subTask : subTasks) {
            str.append(subTask.getId()).append(";");
        }
        return str + "]}";
    }
}
