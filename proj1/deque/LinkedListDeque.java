package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {

    /* Define the Node class */
    private class Node {
        private T item;
        private Node prev;
        private Node next;

        private Node(T item, Node prev, Node next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }



    private Node sentinal;
    private int size;


    /* Creates an empty linked list deque. */
    public LinkedListDeque() {
        sentinal = new Node(null, null, null);
        sentinal.next = sentinal;
        sentinal.prev = sentinal;
        size = 0;
    }



    @Override
    public void addFirst(T item) {
        Node first = new Node(item, sentinal, sentinal.next);
        sentinal.next.prev = first;
        sentinal.next = first;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node last = new Node(item, sentinal.prev, sentinal);
        sentinal.prev.next = last;
        sentinal.prev = last;
        size++;
    }


    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (Node p = sentinal.next; p != sentinal; p = p.next) {
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        size--;
        T returnItem = sentinal.next.item;
        sentinal.next.next.prev = sentinal;
        sentinal.next = sentinal.next.next;
        return returnItem;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        size--;
        T returnItem = sentinal.prev.item;
        sentinal.prev.prev.next = sentinal;
        sentinal.prev = sentinal.prev.prev;
        return returnItem;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node p = sentinal;
        for (int i = 0; i <= index; i++) {
            p = p.next;
        }
        return p.item;
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return getRecursiveNode(index).item;
    }

    private Node getRecursiveNode(int idx) {
        if (idx == 0) {
            return sentinal.next;
        }
        return getRecursiveNode(idx - 1).next;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {

        private Node wizPos;

        public LinkedListDequeIterator() {
            wizPos = sentinal;
        }

        @Override
        public boolean hasNext() {
            return wizPos.next != sentinal;
        }

        @Override
        public T next() {
            wizPos = wizPos.next;
            return wizPos.item;
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque)) {
            return false;
        }
        if (size != ((LinkedListDeque<?>) o).size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!((LinkedListDeque<?>) o).get(i).equals(get(i))) {
                return false;
            }
        }
        return true;
    }
}
