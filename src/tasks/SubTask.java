package tasks;

public class SubTask extends Task {
    private int parent = -1;

    public SubTask(String title, String description) {
        super(title, description);
        this.type = TaskType.SUBTASK;
    }

    public SubTask(String title, String description, Epic parent) {
        super(title, description);
        this.parent = parent.getId();
        this.type = TaskType.SUBTASK;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.parent = subTask.parent;
        this.type = subTask.type;
    }

    public int getParent() {
        return parent;
    }

    //заменено на проверку по id
    public void setParent(Epic epic) {
        if (this.getId() != epic.getId()) {
            this.parent = epic.getId();
        } else {
            System.out.println("не удалось установить родителя");
        }
    }

    public void setParent(int epicId) {
        if (this.getId() != epicId) {
            this.parent = epicId;
        } else {
            System.out.println("не удалось установить родителя");
        }
    }

    @Override
    public String toString() {
        return "tasks.SubTask{" +
                "id=" + this.getId() + "; " +
                "title=" + this.getTitle() + "; " +
                "description=" + this.getDescription() + "; " +
                "status=" + this.getStatus() + "; " +
                "parentId=" + parent + ";" +
                '}';
    }
}
