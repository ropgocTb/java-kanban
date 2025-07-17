public class SubTask extends Task {
    private Epic parent;

    public SubTask(String title, String description, Epic parent) {
        super(title, description);
        this.parent = parent;
        parent.addSubTask(this);
    }

    protected Epic getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + this.getId() + "; " +
                "title=" + this.getTitle() + "; " +
                "description=" + this.getDescription() + "; " +
                "status=" + this.getStatus() + "; " +
                "parentId=" + parent.getId() + ";" +
                '}';
    }
}
