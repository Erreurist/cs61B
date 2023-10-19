package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> c;
    public MaxArrayDeque(Comparator<T> c) {
        this.c = c;
    }

    public T max() {
        if (isEmpty()) {
            return null;
        }
        T res = get(0);
        for (int i = 0; i < size(); i++) {
            if (c.compare(get(i), res) > 0) {
                res = get(i);
            }
        }
        return res;
    }


    public T max(Comparator<T> comparator) {
        if (isEmpty()) {
            return null;
        }
        T res = get(0);
        for (int i = 0; i < size(); i++) {
            if (comparator.compare(get(i), res) > 0) {
                res = get(i);
            }
        }
        return res;
    }

}
