package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subTasks = new ArrayList<>();
        this.type = TaskType.EPIC;
    }

    public Epic(Epic epic) {
        super(epic);
        this.subTasks = new ArrayList<>(epic.getSubTasks());
        this.type = epic.getType();
    }

    public List<Integer> getSubTasks() {
        return subTasks;
    }

    //заменено на проверку по id,
    public void addSubTask(SubTask subTask) {
        if (!this.subTasks.contains(subTask.getId()) && this.getId() != subTask.getId()) {
            SubTask subTaskCopy = new SubTask(subTask);
            subTaskCopy.setParent(this);
            this.subTasks.add(subTaskCopy.getId());
        } else {
            System.out.println("не удалось добавить подзадачу");
        }
    }

    public void removeSubTask(SubTask subTask) {
        if (subTask != null)
            this.subTasks.remove((Integer) subTask.getId());
    }

    public void clearSubTasks() {
        this.subTasks.clear();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("tasks.Epic{" +
                "id=" + this.getId() + "; " +
                "description=" + this.getDescription() + "; " +
                "status=" + this.getStatus() + "; " +
                "subTasksIds=[");
        for (Integer subTaskId : subTasks) {
            str.append(subTaskId).append(";");
        }
        return str + "]}";
    }
}
