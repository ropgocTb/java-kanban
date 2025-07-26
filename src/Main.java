import manager.InMemoryTaskManager;
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

    public static void main(String[] args) {
        //Базовая структура
        TaskManager manager = new InMemoryTaskManager();
        Epic epic = new Epic("Эпик", "описание эпика");
        SubTask subTask = new SubTask("Подзадача эпика", "описание подзадачи", epic);
        Task task = new Task("Задача", "описание задачи");

        manager.addEpic(epic);
        manager.addSubTask(subTask);
        manager.addTask(task);

        //заполнение истории
        manager.getEpic(epic.getId());
        manager.getSubTask(subTask.getId());
        manager.getTask(task.getId());

        printAllTasks(manager);
    }
}
