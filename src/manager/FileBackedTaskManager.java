package manager;

import tasks.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public static void main(String[] args) {
        FileBackedTaskManager manager = new FileBackedTaskManager(new File("tasks.txt"));
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
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubTask(subTask2);
        SubTask subTask3 = new SubTask("subTask3ForEpic1", "subTask3ForEpic1_desc", epic1);
        manager.addSubTask(subTask3);

        Epic epic2 = new Epic("epic2", "epic2_desc");
        manager.addEpic(epic2);

        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(new File("tasks.txt"));

        System.out.println(manager.getTasks());
        System.out.println(manager.getEpics());
        System.out.println(manager.getSubTasks());

        System.out.println(manager2.getTasks());
        System.out.println(manager2.getEpics());
        System.out.println(manager2.getSubTasks());

    }

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager() {
        try {
            this.file = File.createTempFile("tempFile", null);
            if (this.file.exists()) {
                System.out.println("temp File created: " + this.file.getName());
            } else {
                System.out.println("temp File can't be created: " + this.file.getName());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader br = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
            br.readLine();
            while(br.ready()) {
                String line = br.readLine();
                Task task = CSVTaskFormat.fromString(line);
                if (task instanceof SubTask) {
                    manager.addSubTask((SubTask) task);
                } else if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException ex) {
            System.out.println("исключение");
        }

        return manager;
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write(CSVTaskFormat.getHeader());
            bw.newLine();
            for (var set : tasks.entrySet()) {
                bw.write(CSVTaskFormat.toString(set.getValue()));
                bw.newLine();
            }

            for (var set : epics.entrySet()) {
                bw.write(CSVTaskFormat.toString(set.getValue()));
                bw.newLine();
            }

            for (var set : subTasks.entrySet()) {
                bw.write(CSVTaskFormat.toString(set.getValue()));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удается сохранить файл: " + file.getName());
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void removeTask(Task task) {
        super.removeTask(task);
        save();
    }

    @Override
    public void removeSubTask(SubTask subTask) {
        super.removeSubTask(subTask);
        save();
    }

    @Override
    public void removeEpic(Epic epic) {
        super.removeEpic(epic);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public Task getTask(int id) {
        final Task task = super.getTask(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        final SubTask subTask = super.getSubTask(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpic(int id) {
        final Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    @Override
    public List<Task> getTasks() {
        final List<Task> tasks = super.getTasks();
        save();
        return tasks;
    }

    @Override
    public List<SubTask> getSubTasks() {
        final List<SubTask> subTasks = super.getSubTasks();
        save();
        return subTasks;
    }

    @Override
    public List<Epic> getEpics() {
        final List<Epic> epics = super.getEpics();
        save();
        return epics;
    }
}
