package tasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Epic extends Task {
    private final List<Integer> subTasks;
    private LocalDateTime endTime;

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

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public Optional<LocalDateTime> getEndTime() {
        return Optional.ofNullable(endTime);
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
