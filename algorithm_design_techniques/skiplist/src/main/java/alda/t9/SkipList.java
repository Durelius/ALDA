package alda.t9;

import java.util.Arrays;
import java.util.Random;

public class SkipList<T extends Comparable<T>> {

    SkipList() {
        first = new Node<T>(null, null, null);
        last = new Node<T>(null, null, null);
        first.next = (Node<T>[]) new Node[MAX_LEVEL];
        Arrays.fill(first.next, last); // every level starts by pointing to last
    }

    private Node<T> first;
    private Node<T> last;
    private int currentHighestLevel = 0;
    private final Random random = new Random();
    private static final int MAX_LEVEL = 10;

    private static class Node<T> {
        private T data;
        private Node<T>[] next;
        private Node<T> prev;

        Node(T item, Node<T> prev, Node<T>[] next) {
            this.data = item;
            this.prev = prev;
            this.next = next;
        }

        private int level() {
            return next.length;
        }
    }

    // -----------------------------------------------------------------------
    // add
    // -----------------------------------------------------------------------
    public void add(T item) {
        // Determine the new node's height first so currentHighestLevel is up-to-date
        int newLevel = randomLevel();

        // update[i] = rightmost node at level i that is strictly less than item
        Node<T>[] update = (Node<T>[]) new Node[MAX_LEVEL];
        Arrays.fill(update, first); // default: first is predecessor at every level

        var current = first;
        for (int i = currentHighestLevel - 1; i >= 0; i--) {
            while (current.next[i] != last &&
                    current.next[i].data.compareTo(item) < 0) {
                current = current.next[i];
            }
            update[i] = current;
        }

        // Build and wire in the new node
        Node<T>[] next = (Node<T>[]) new Node[newLevel];
        Node<T> newNode = new Node<>(item, update[0], next);

        for (int i = 0; i < newLevel; i++) {
            newNode.next[i] = update[i].next[i];
            update[i].next[i] = newNode;
        }

        // Maintain the level-0 prev pointer of the successor
        newNode.next[0].prev = newNode;
    }

    // -----------------------------------------------------------------------
    // remove
    // -----------------------------------------------------------------------
    public boolean remove(T item) {
        Node<T>[] update = (Node<T>[]) new Node[MAX_LEVEL];
        Arrays.fill(update, first);

        var current = first;
        for (int i = currentHighestLevel - 1; i >= 0; i--) {
            while (current.next[i] != last &&
                    current.next[i].data.compareTo(item) < 0) {
                current = current.next[i];
            }
            update[i] = current;
        }

        current = current.next[0];
        if (current == last || !current.data.equals(item))
            return false;

        // Unlink from every level this node participates in
        for (int i = 0; i < current.level(); i++) {
            update[i].next[i] = current.next[i];
        }
        current.next[0].prev = current.prev; // repair level-0 prev
        return true;
    }

    // -----------------------------------------------------------------------
    // contains – uses the skip-list fast path
    // -----------------------------------------------------------------------
    public boolean contains(T item) {
        var current = first;
        for (int i = currentHighestLevel - 1; i >= 0; i--) {
            while (current.next[i] != last &&
                    current.next[i].data.compareTo(item) < 0) {
                current = current.next[i];
            }
        }
        current = current.next[0];
        return current != last && current.data.equals(item);
    }

    // -----------------------------------------------------------------------
    // size – walk level 0
    // -----------------------------------------------------------------------
    public int size() {
        int size = 0;
        var current = first.next[0];
        while (current != last) {
            current = current.next[0];
            size++;
        }
        return size;
    }

    // -----------------------------------------------------------------------
    // levelOfNode – 1-based index along level 0
    // -----------------------------------------------------------------------
    int levelOfNode(int i) {
        var current = first.next[0];
        int count = 1;
        while (current != last && count < i) {
            current = current.next[0];
            count++;
        }
        if (current == last)
            return -1; // index out of range
        return current.level();
    }

    // -----------------------------------------------------------------------
    // randomLevel – returns a value in [1 .. MAX_LEVEL]
    // -----------------------------------------------------------------------
    private int randomLevel() {
        int level = 1;
        while (level < MAX_LEVEL && random.nextBoolean()) {
            level++;
        }
        if (level > currentHighestLevel)
            currentHighestLevel = level;
        return level;
    }
}
