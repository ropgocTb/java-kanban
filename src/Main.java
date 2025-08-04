import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

public class Main {
    private static void printAllTasks(TaskManager manager) {
        System.out.println("---------Печать содержимого менеджера----------");
        System.out.println("Задачи: ");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики: ");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            for (SubTask subTask : epic.getSubTasks()) {
                System.out.println("--->" + subTask);
            }
        }
        System.out.println("Подзадачи: ");
        for (SubTask subTask : manager.getSubTasks()) {
            System.out.println(subTask);
        }
        System.out.println("История: ");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    private static void printHistory(TaskManager manager) {
        System.out.println("История: ");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {
        //Пользовательский сценарий
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("task1", "task1_desc");
        manager.addTask(task1);
        Task task2 = new Task("task2", "task2_desc");
        manager.addTask(task2);

        Epic epic1 = new Epic("epic1", "epic1_desc");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("subTask1ForEpic1", "subTask1ForEpic1_desc", epic1);
        manager.addSubTask(subTask1);
        SubTask subTask2 = new SubTask("subTask2ForEpic1", "subTask2ForEpic1_desc", epic1);
        manager.addSubTask(subTask2);
        SubTask subTask3 = new SubTask("subTask3ForEpic1", "subTask3ForEpic1_desc", epic1);
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic("epic2", "epic2_desc");
        manager.addEpic(epic2);

        //Как должна изменяться история просмотра (по id задач)
        //null
        manager.getTask(task1.getId()); // 1 -> null
        printHistory(manager);
        manager.getTask(task2.getId()); // 1 -> 2 -> null
        printHistory(manager);
        manager.getTask(task1.getId()); // 2 -> 1 -> null
        printHistory(manager);

        manager.getEpic(epic1.getId()); // 2 -> 1 -> 3 -> null
        printHistory(manager);
        manager.getTask(task2.getId()); // 1 -> 3 -> 2 -> null
        printHistory(manager);
        manager.getEpic(epic2.getId()); // 1 -> 3 -> 2 -> 7 -> null
        printHistory(manager);

        manager.removeEpic(epic2.getId()); // 1 -> 3 -> 2 -> null
        printHistory(manager);

        manager.getSubTask(subTask2.getId()); // 1 -> 3 -> 2 -> 5 -> null
        printHistory(manager);
        manager.getSubTask(subTask1.getId()); // 1 -> 3 -> 2 -> 5 -> 4 -> null
        printHistory(manager);

        manager.removeEpic(epic1); // 1 -> 2 -> null (удалился эпик и все его подзадачи)
        printAllTasks(manager);
    }
}
