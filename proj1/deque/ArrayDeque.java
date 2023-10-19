package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    private int rear1, rear2;
    private final int MAXSIZE = 16;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        rear1 = 4;
        rear2 = 3;
    }



    private int rearAdd(int rear) {
        rear++;
        if (rear == items.length) {
            rear = 0;
        }
        return rear;
    }

    private int rearMinus(int rear) {
        rear--;
        if (rear == -1) {
            rear = items.length - 1;
        }
        return rear;
    }

    private void resize(double k) {
        T[] newItems = (T[]) new Object[(int) (k * items.length)];
        int p = rear1;
        for (int i = 0; i < size; i++) {
            newItems[i] = items[p];
            p = rearAdd(p);
        }
        rear1 = 0;
        rear2 = size - 1;
        items = newItems;
    }
    private void resizeExpand() {
        resize(2);
    }

    private void resizeContract() {
        resize(0.5);
    }
    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resizeExpand();
        }
        size++;
        rear1 = rearMinus(rear1);
        items[rear1] = item;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resizeExpand();
        }
        size++;
        rear2 = rearAdd(rear2);
        items[rear2] = item;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        int p = rear1;
        for (int i = 0; i < size; i++) {
            System.out.print(items[p] + " ");
            p = rearAdd(p);
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        if (size - 1 < items.length / 4.0 && items.length >= MAXSIZE) {
            resizeContract();
        }
        size--;
        T returnItem = items[rear1];
        rear1 = rearAdd(rear1);
        return returnItem;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        if (size - 1 < items.length / 4.0  && items.length >= MAXSIZE) {
            resizeContract();
        }
        size--;
        T returnItem = items[rear2];
        rear2 = rearMinus(rear2);
        return returnItem;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int p = rear1 + index;
        if (p >= items.length) {
            p -= items.length;
        }
        return items[p];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator {

        int p;
        int cnt;

        public ArrayDequeIterator() {
            p = rear1 - 1;
            cnt = 0;
        }

        @Override
        public boolean hasNext() {
            return cnt <= size - 1;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                return null;
            }
            p = rearAdd(p);
            cnt++;
            return items[p];
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof Deque)) {
            return false;
        }
        if (size != ((Deque<?>) o).size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!((Deque<?>) o).get(i).equals(get(i))) {
                return false;
            }
        }
        return true;
    }
}
