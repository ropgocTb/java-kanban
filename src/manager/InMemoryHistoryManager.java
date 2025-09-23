package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    public static class Node<T> {
        public T value;
        public Node<T> next;
        public Node<T> prev;

        public Node(T value, Node<T> next, Node<T> prev) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newTail = new Node<>(task, null, oldTail);
        tail = newTail;
        if (oldTail == null) {
            head = newTail;
        } else {
            oldTail.next = newTail;
        }
        size++;
    }

    private List<Task> getTasks() {
        List<Task> list = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            list.add(node.value);
            node = node.next;
        }
        return list;
    }

    private void removeNode(Node<Task> node) {
        size--;
        if (size == 0) {
            head = tail = null;
        } else {
            if (node == head) {
                head = node.next;
                node.next.prev = null;
            } else if (node == tail) {
                tail = node.prev;
                node.prev.next = null;
            } else {
                node.next.prev = node.prev;
                node.prev.next = node.next;
            }
        }
    }

    @Override
    public void addTask(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node<Task> node = historyMap.get(task.getId());
            removeNode(node);
        }
        linkLast(new Task(task));
        historyMap.put(task.getId(), tail);
    }

    @Override
    public void removeTask(int id) {
        if (historyMap.get(id) == null) {
            System.out.println("В истории нет задачи с id: " + id);
            return;
        }
        removeNode(historyMap.get(id));
        historyMap.remove(id);
        System.out.println("Задача с id: " + id + " удалена из истории.");
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
