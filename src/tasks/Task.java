package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;
    protected TaskType type;
    private Duration duration;
    private LocalDateTime startTime;

    public Task (String title, String description, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.type = TaskType.TASK;
        this.status = TaskStatus.NEW;
    }

    public Task(String title, String description, TaskType type) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = type;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.type = TaskType.TASK;
    }

    public Task(Task task) {
        this.id = task.id;
        this.title = task.title;
        this.description = task.description;
        this.status = task.status;
        this.type = task.type;
        this.startTime = task.startTime;
        this.duration = task.duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskType getType() {
        return this.type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public Optional<LocalDateTime> getEndTime() {
        if (startTime != null && duration != null)
            return Optional.of(startTime.plusMinutes(duration.toMinutes()));
        return Optional.empty();
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public boolean isOverlapping(Task task) {
        if (this.getEndTime().isEmpty() || task.getEndTime().isEmpty())
            return false;

        if (this.getId() == task.getId())
            return false;

        return this.startTime.isBefore(task.getEndTime().get()) && this.getEndTime().get().isAfter(task.getStartTime());
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        Task task = (Task) o;
        return id == task.id; //в условии написано что одинаковые id = одинаковые задачи
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status);
    }

    @Override
    public String toString() {
        return "tasks.Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
