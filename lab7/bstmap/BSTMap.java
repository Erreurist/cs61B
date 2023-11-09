package bstmap;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node root;

    private class Node {
        private K key;
        private V val;
        private Node left;
        private Node right;
        private int size;

        public Node(K key, V val) {
            this.key = key;
            this.val = val;
            this.size = 1;
        }
    }

    public BSTMap() {
        this.root = null;
    }
    @Override
    public void clear() {
        clear(root);
    }

    private void clear(Node node) {
        if (node == null) {
            return;
        }
        clear(node.left);
        clear(node.right);
        node.left = null;
        node.right = null;
    }

    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }


    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(Node node, K key) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (node == null) {
            return null;
        }
        int cmp = node.key.compareTo(key);
        if (cmp < 0) {
            return get(node.left, key);
        } else if (cmp > 0) {
            return get(node.right, key);
        }
        return node.val;
    }

    @Override
    public int size() {
        return size(root);
    }

    private int size(Node node) {
        if (node != null) {
            return node.size;
        }
        return 0;
    }

    @Override
    public void put(K key, V val) {
        root = put(root, key, val);
    }

    private Node put(Node node, K key, V val) {
        if (key == null) {
            throw new NullPointerException();
        }
        if (node == null) {
            return new Node(key, val);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = put(node.left, key, val);
        } else if (cmp > 0) {
            node.right = put(node.right, key, val);
        } else {
            node.val = val;
        }
        node.size = 1 + size(node.left) + size(node.right);
        return node;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V val) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrder(root);
    }

    private void printInOrder(Node node) {
        if (node == null) {
            return;
        }
        printInOrder(node.left);
        System.out.println(node.key);
        printInOrder(node.right);
    }

}
