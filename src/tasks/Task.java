package tasks;

import java.util.Objects;

public class Task {
    private int id;
    private String title;
    private String description;
    private TaskStatus status;

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(Task task) {
        this.id = task.id;
        this.title = task.title;
        this.description = task.description;
        this.status = task.status;
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
