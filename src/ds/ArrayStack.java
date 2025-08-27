package ds;

import java.util.NoSuchElementException;

public class ArrayStack<E> {
    private Object[] data;
    private int top;
    private int capacity;

    public ArrayStack(int cap) {
        this.capacity = Math.max(8, cap);
        this.data = new Object[this.capacity];
        this.top = 0;
    }

    public void push(E item) {
        if (top == capacity) resize(capacity * 2);
        data[top++] = item;
    }

    @SuppressWarnings("unchecked")
    public E pop() {
        if (top == 0) throw new NoSuchElementException("Stack empty");
        E item = (E) data[--top];
        data[top] = null;
        if (top > 0 && top == capacity / 4) resize(capacity / 2);
        return item;
    }

    @SuppressWarnings("unchecked")
    public E peek() {
        if (top == 0) return null;
        return (E) data[top - 1];
    }

    public boolean isEmpty() { return top == 0; }
    public int size() { return top; }

    private void resize(int newCap) {
        Object[] tmp = new Object[newCap];
        System.arraycopy(data, 0, tmp, 0, top);
        data = tmp;
        capacity = newCap;
    }
}


