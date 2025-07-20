public class Main {
    

    public static void main(String[] args) {
        //проверка добавления
        Task task1 = new Task("task1", "task1_desc");
        TaskManager.addTask(task1);
        Task task2 = new Task("task2", "task2_desc");
        TaskManager.addTask(task2);

        Epic epic1 = new Epic("epic1", "epic1_desc");
        TaskManager.addEpic(epic1);

        SubTask subTask1 = new SubTask("sub1", "sub1_desc", epic1);
        TaskManager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("sub2", "sub2_desc", epic1);
        TaskManager.addSubTask(subTask2);

        Epic epic2 = new Epic("epic2", "epic2_desc");
        TaskManager.addEpic(epic2);

        SubTask subTask3 = new SubTask("sub3", "sub3_desc", epic2);
        TaskManager.addSubTask(subTask3);
        SubTask subTask4 = new SubTask("sub4", "sub4_desc", epic2);
        TaskManager.addSubTask(subTask4);
        SubTask subTask5 = new SubTask("sub5", "sub5_desc", epic2);
        TaskManager.addSubTask(subTask5);

        System.out.println(TaskManager.getTaskById(task1.getId()));
        System.out.println(TaskManager.getSubTaskById(subTask1.getId()));
        System.out.println(TaskManager.getEpicById(epic2.getId()));
        System.out.println(TaskManager.getAllTasks());
        System.out.println(TaskManager.getAllSubTasks());
        System.out.println(TaskManager.getAllEpics());

        //проверка изменения статуса
        task1.setStatus(TaskStatus.DONE);
        TaskManager.updateTask(task1);
        subTask1.setStatus(TaskStatus.IN_PROGRESS);
        TaskManager.updateSubTask(subTask1);
        subTask2.setStatus(TaskStatus.DONE);
        TaskManager.updateSubTask(subTask2);
        subTask3.setStatus(TaskStatus.DONE);
        TaskManager.updateSubTask(subTask3);
        epic2.setDescription("Changed_description");
        TaskManager.updateEpic(epic2);

        System.out.println(TaskManager.getAllTasks());
        System.out.println(TaskManager.getAllSubTasks());
        System.out.println(TaskManager.getAllEpics());

        //проверка удаления
        TaskManager.removeTaskById(task1.getId());
        TaskManager.removeSubTask(subTask4);
        TaskManager.removeSubTaskById(subTask5.getId());
        TaskManager.removeEpic(epic2);

        System.out.println(TaskManager.getAllTasks());
        System.out.println(TaskManager.getAllSubTasks());
        System.out.println(TaskManager.getAllEpics());

        TaskManager.removeAllTasks();
        TaskManager.removeAllSubTasks();

        System.out.println(TaskManager.getAllTasks());
        System.out.println(TaskManager.getAllSubTasks());
        System.out.println(TaskManager.getAllEpics());


        System.out.println(TaskManager.getTaskById(1));
        TaskManager.removeTask(task1);

        System.out.println(TaskManager.getSubTasksByEpic(epic1));

        TaskManager.removeEpicById(epic2.getId());
        TaskManager.removeAllEpics();
        System.out.println(TaskManager.getAllEpics());

    }
}
