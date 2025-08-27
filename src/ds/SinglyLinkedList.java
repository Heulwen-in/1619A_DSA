package ds;

import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class SinglyLinkedList<E> {
    private static class Node<E> {
        E value;
        Node<E> next;
        Node(E v) { this.value = v; }
    }

    private Node<E> head;
    private Node<E> tail;
    private int size;

    public void addFirst(E value) {
        Node<E> n = new Node<>(value);
        n.next = head;
        head = n;
        if (tail == null) tail = n;
        size++;
    }

    public void addLast(E value) {
        Node<E> n = new Node<>(value);
        if (tail == null) {
            head = tail = n;
        } else {
            tail.next = n;
            tail = n;
        }
        size++;
    }

    public E removeFirst() {
        if (head == null) throw new NoSuchElementException("List empty");
        E v = head.value;
        head = head.next;
        if (head == null) tail = null;
        size--;
        return v;
    }

    public E peekFirst() {
        return head == null ? null : head.value;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public void forEach(Consumer<E> action) {
        Node<E> cur = head;
        while (cur != null) { action.accept(cur.value); cur = cur.next; }
    }
}


