package manager;

import service.Node;
import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    class historyLinkedList<T> {
        public Node<T> head;
        public Node<T> tail;
        private int size = 0;

        public historyLinkedList() {
            this.head = null;
            this.tail = null;
        }

        public void linkLast(T task) {
            final Node<T> oldTail = tail;
            final Node<T> newTail = new Node<>(task, null, oldTail);
            tail = newTail;
            if (oldTail == null) {
                head = newTail;
            } else {
                oldTail.next = newTail;
            }
            size++;
        }

        public List<T> getTasks() {
            if (size == 0)
                throw new NoSuchElementException();
            List<T> list = new ArrayList<>();
            Node<T> node = head;
            while(node != null) {
                list.add(node.value);
                node = node.next;
            }
            return list;
        }

        public void removeNode(Node<T> node) {
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
    }

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private final historyLinkedList<Task> historyList = new historyLinkedList<>();

    @Override
    public void addTask(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node<Task> node = historyMap.get(task.getId());
            historyList.removeNode(node);
        }
        historyList.linkLast(new Task(task));
        historyMap.put(task.getId(), historyList.tail);
    }

    @Override
    public void removeTask(int id) {
        if (historyMap.get(id) == null) {
            System.out.println("В истории нет задачи с id: " + id);
            return;
        }
        Node<Task> node = historyMap.get(id);
        historyList.removeNode(node);
        historyMap.remove(id);
        System.out.println("Задача с id: " + id + " удалена из истории.");
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }
}
