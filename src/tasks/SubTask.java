package tasks;

public class SubTask extends Task {
    private Epic parent;

    public SubTask(String title, String description) {
        super(title, description);
    }

    public SubTask(String title, String description, Epic parent) {
        super(title, description);
        this.parent = parent;
    }

    public SubTask(SubTask subTask) {
        super(subTask);
        this.parent = subTask.parent;
    }

    public Epic getParent() {
        return parent;
    }

    //заменено на проверку по id
    public void setParent(Epic epic) {
        if (this.getId() != epic.getId()) {
            this.parent = epic;
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
                "parentId=" + parent.getId() + ";" +
                '}';
    }
}
