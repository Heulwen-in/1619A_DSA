package ds;

import java.util.NoSuchElementException;

public class ArrayQueue<E> {
    private Object[] data;
    private int head;
    private int tail;
    private int size;
    private int capacity;

    public ArrayQueue(int cap) {
        this.capacity = Math.max(8, cap);
        this.data = new Object[this.capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public void enqueue(E item) {
        if (size == capacity) resize(capacity * 2);
        data[tail] = item;
        tail = (tail + 1) % capacity;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E dequeue() {
        if (size == 0) throw new NoSuchElementException("Queue empty");
        E item = (E) data[head];
        data[head] = null;
        head = (head + 1) % capacity;
        size--;
        if (size > 0 && size == capacity / 4) resize(capacity / 2);
        return item;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        if (size == 0) return null;
        return (E) data[head];
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    private void resize(int newCap) {
        Object[] tmp = new Object[newCap];
        for (int i = 0; i < size; i++) {
            tmp[i] = data[(head + i) % capacity];
        }
        data = tmp;
        head = 0;
        tail = size;
        capacity = newCap;
    }
}


