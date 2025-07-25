package tasks;

public class SubTask extends Task {
    private Epic parent;

    public SubTask(String title, String description) {
        super(title, description);
    }

    public SubTask(String title, String description, Epic parent) {
        super(title, description);
        this.parent = parent;
        parent.addSubTask(this);
    }

    public Epic getParent() {
        return parent;
    }

    public void setParent(Task epic) {
        if (epic instanceof Epic) {
            Epic epic1 = (Epic) epic;
            epic1.addSubTask(this);
            this.parent = epic1;
        } else {
            System.out.println("Родителем может быть только эпик");
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
