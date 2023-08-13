package service;

import entility.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    private Node last;
    private Node first;
    public static class Node {
        Task value;
        Node next;
        Node prev;

        public Node(Task value) {
            this.value = value;
        }

        public Task getValue() {
            return value;
        }
    }

    private Map<Integer, Node> map = new HashMap<>();

    public Map<Integer, Node> getMap() {
        return map;
    }

    public void linkLast(Task task) {
        Node node = new Node(task);
        if (first == null) {
            first = node;
        } else {
            last.next = node;
            node.prev = last;
        }
        last = node;
        map.put(task.getId(),node);
    }
     public List<Task> getTasks() {
        Node current = first;
        List <Task> result = new ArrayList<>();
        while (current != null) {
            result.add(current.getValue());
            current = current.next;
        }
        return result;
    }

    public void removeNode(Node node) {
        if (node == last && node == first) {
            first = null;
            last = null;
        } else if (node == first) {
            first = node.next;
            first.prev = null;
        } else if (node == last) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        map.remove(node.getValue().getId());
    }

}
