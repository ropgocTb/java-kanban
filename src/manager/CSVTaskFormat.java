package manager;

import tasks.*;

public class CSVTaskFormat {
    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task instanceof SubTask ? ((SubTask) task).getParent() : "");
    }

    public static String getHeader() {
        return "id,type,name,status,description,epic";
    }

    public static Task fromString(String str) {
        String[] fields = str.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int epicId = fields.length > 5 ? Integer.parseInt(fields[5]) : -1;

        switch (type) {
            case TaskType.TASK: {
                Task task = new Task(title, description);
                task.setId(id);
                task.setType(type);
                task.setStatus(status);
                return task;
            }
            case TaskType.EPIC: {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setType(type);
                epic.setStatus(status);
                return epic;
            }
            case TaskType.SUBTASK: {
                SubTask subTask = new SubTask(title, description);
                subTask.setId(id);
                subTask.setType(type);
                subTask.setStatus(status);
                subTask.setParent(epicId);
                return subTask;
            }
            default:
                System.out.println("Нет такого типа задачи");
        }

        return null;
    }
}
