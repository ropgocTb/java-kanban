import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<SubTask> subTasks;

    public Epic(String title, String description) {
        super(title, description);
        this.subTasks = new ArrayList<>();
    }

    protected ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    protected void addSubTask(SubTask subTask) {
        this.subTasks.add(subTask);
    }

    protected void removeSubTask(SubTask subTask) {
        this.subTasks.remove(subTask);
    }

    protected TaskStatus getStatus() {
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
        String str = "Epic{" +
                "id=" + this.getId() + "; " +
                "status=" + this.getStatus() + "; " +
                "subTasksIds=[";
        for (SubTask subTask : subTasks) {
            str += subTask.getId() + ";";
        }
        return str.substring(0, str.length() - 1) + "]}";
    }
}
