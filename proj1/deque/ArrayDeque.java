package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>{
    T[] items;
    int size;
    int rear1, rear2;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        rear1 = size;
        rear2 = size;
    }

//    private void resize() {
//        T[] newItems =
//    }

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
    public void addFirst(T item) {
        size++;
        rear1 = rearMinus(rear1);
        items[rear1] = item;
    }

    public void addLast(T item) {
        size++;
        rear2 = rearAdd(rear2);
        items[rear2] = item;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        int p =rear1;
        for (int i = 0; i < size; i++) {
            System.out.println(items[p] + " ");
            p = rearAdd(p);
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size--;
        T returnItem = items[rear1];
        rear1 = rearAdd(rear1);
        return returnItem;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size--;
        T returnItem = items[rear2];
        rear2 = rearMinus(rear2);
        return returnItem;
    }

    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int p =rear1;
        for (int i = 0; i < index; i++) {
            p = rearAdd(p);
        }
        return items[p];
    }

    @Override
    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator{

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
        if (!(o instanceof ArrayDeque)) {
            return false;
        }
        if (size != ((ArrayDeque<?>) o).size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!((ArrayDeque<?>) o).get(i).equals(get(i))) {
                return false;
            }
        }
        return true;
    }
}
