package manager;

import tasks.Task;
import tasks.Epic;
import tasks.SubTask;
import tasks.TaskType;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CSVTaskFormat {
    final static DateTimeFormatter DEFAULT_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                task instanceof SubTask ? ((SubTask) task).getParent() : "",
                task.getStartTime() == null ? "" : task.getStartTime().format(DEFAULT_FORMAT),
                task.getDuration() == null || task.getDuration() == Duration.ZERO ? ""
                        : task.getDuration().toMinutes(),
                task.getEndTime().isEmpty() ? "" : task.getEndTime().get().format(DEFAULT_FORMAT));
    }

    public static String getHeader() {
        return "id,type,name,status,description,epic,startTime,duration,endTime";
    }

    public static Optional<Task> fromString(String str) {
        if (str.isBlank() || str.isEmpty())
            return Optional.empty();

        String[] fields = str.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int epicId = -1;
        LocalDateTime startTime = null;
        Duration duration = Duration.ZERO;

        if (fields.length > 5 && !fields[5].isEmpty()) {
            try {
                epicId = Integer.parseInt(fields[5]);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка парсинга эпика строки: " + e.getMessage());
            }
        }

        if (fields.length > 6 && !fields[6].isEmpty()) {
            try {
                startTime = LocalDateTime.parse(fields[6], DEFAULT_FORMAT);
            } catch (RuntimeException e) {
                System.out.println("Ошибка парсинга даты начала строки: " + e.getMessage());
            }
        }

        if (fields.length > 7 && !fields[7].isEmpty()) {
            try {
                duration = Duration.ofMinutes(Long.parseLong(fields[7]));
            } catch (NumberFormatException e) {
                System.out.println("Ошибка при парсинге длительности: " + e.getMessage());
                return Optional.empty();
            }
        }

        switch (type) {
            case TASK: {
                Task task = new Task(title, description);
                task.setId(id);
                task.setType(type);
                task.setStatus(status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return Optional.of(task);
            }
            case EPIC: {
                Epic epic = new Epic(title, description);
                epic.setId(id);
                epic.setType(type);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return Optional.of(epic);
            }
            case SUBTASK: {
                SubTask subTask = new SubTask(title, description);
                subTask.setId(id);
                subTask.setType(type);
                subTask.setStatus(status);
                subTask.setParent(epicId);
                subTask.setStartTime(startTime);
                subTask.setDuration(duration);
                return Optional.of(subTask);
            }
            default:
                System.out.println("Нет такого типа задачи");
        }

        return Optional.empty();
    }
}
