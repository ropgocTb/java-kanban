public class Main {

    static TaskManager manager = new TaskManager();

    public static void main(String[] args) {
        Task task1 = new Task("task1", "task1_desc");
        manager.addTask(task1);
        Task task2 = new Task("task2", "task2_desc");
        manager.addTask(task2);

        Epic epic1 = new Epic("epic1", "epic1_desc");
        manager.addTask(epic1);

        SubTask subTask1 = new SubTask("sub1", "sub1_desc", epic1);
        manager.addTask(subTask1);
        SubTask subTask2 = new SubTask("sub2", "sub2_desc", epic1);
        manager.addTask(subTask2);

        Epic epic2 = new Epic("epic2", "epic2_desc");
        manager.addTask(epic2);

        SubTask subTask3 = new SubTask("sub3", "sub3_desc", epic2);
        manager.addTask(subTask3);

        System.out.println(manager.getAllTasks());

        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(subTask1);
        subTask2.setStatus(TaskStatus.DONE);
        manager.updateTask(subTask2);
        subTask3.setStatus(TaskStatus.DONE);
        manager.updateTask(subTask3);

        System.out.println(manager.getAllTasks());

        manager.removeTaskById(task1.getId());
        manager.removeTask(epic2);

        System.out.println(manager.getAllTasks());

        manager.removeAllTasks();

        System.out.println(manager.getAllTasks());
    }
}
