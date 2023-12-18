package core.basesyntax;

import static java.util.Objects.hash;

import java.util.Map;
import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    public static final int DEFAULT_INITIAL_CAPACITY = 16;
    public static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private int size;
    private int threshold;
    private Node<K, V>[] table = new Node[DEFAULT_INITIAL_CAPACITY];

    static class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            return o instanceof Map.Entry<?, ?> e
                    && Objects.equals(key, e.getKey())
                    && Objects.equals(value, e.getValue());
        }
    }

    @Override
    public void put(K key, V value) {
        if (size > threshold) {
            resize();
        }
        Node<K, V>[] tab = table;
        Node<K, V> first;
        Node<K, V> next;
        int n = tab.length;
        int i;
        int hash = (key == null) ? 0 : hash(key);
        if (tab[i = ((n - 1) & hash)] == null) {
            first = new Node<>(hash, key, value, null);
            tab[i] = first;
            size++;
        }
        if ((first = tab[i = ((n - 1) & hash)]) != null
                && (key != null && (!first.key.equals(key)))) {
            if ((next = first.next) != null) {
                if (next.next != null) {
                    while (next.next != null) {
                        next = next.next;
                    }
                }
                next.next = new Node<>(hash, key, value, null);
                size++;
            } else {
                first.next = new Node<>(hash, key, value, null);
                tab[i].next = first.next;
                size++;
            }
        }
        if ((first = tab[((n - 1) & hash)]) != null && (first.key == key
                || (key != null && key.equals(first.key)))) {
            first.value = value;
        }
    }

    @Override
    public V getValue(K key) {
        Node<K, V> e;
        return (e = getNode(key)) == null ? null : e.value;
    }

    @Override
    public int getSize() {
        return size;
    }

    final Node<K, V> getNode(Object key) {
        Node<K, V>[] tab;
        Node<K, V> first;
        Node<K, V> next;
        int n;
        int hash = (key == null) ? 0 : hash(key);
        K k;
        if ((tab = table) != null && (n = tab.length) > 0
                && (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && ((k = first.key) == key || key != null && key.equals(k))) {
                return first;
            }
            if ((next = first.next) != null) {
                do {
                    if (next.hash == hash
                            && ((k = next.key) == key || (key != null && key.equals(k)))) {
                        return next;
                    }
                } while ((next = next.next) != null);
            }
        }
        return null;
    }

    final Node<K, V>[] resize() {
        Node<K, V>[] oldTab = table;
        int oldCapacity = (oldTab == null) ? 0 : oldTab.length;
        int oldThreshold = threshold;
        int newCapacity;
        int newThreshold;
        if (oldCapacity > 0) {
            if ((getSize() > oldCapacity * DEFAULT_LOAD_FACTOR)) {
                newCapacity = oldCapacity << 1;
                newThreshold = oldThreshold << 1;
            } else {
                newCapacity = DEFAULT_INITIAL_CAPACITY;
                newThreshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
            }
            threshold = newThreshold;
            Node<K, V>[] newTab = (Node<K, V>[]) new Node[newCapacity];
            for (int i = 0; i < oldTab.length; i++) {
                Node<K, V> e;
                if ((e = oldTab[i]) != null) {
                    if (e.next != null) {
                        Node<K,V> loHead = null;
                        Node<K,V> loTail = null;
                        Node<K,V> hiHead = null;
                        Node<K,V> hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCapacity) == 0) {
                                if (loTail == null) {
                                    loHead = e;
                                } else {
                                    loTail.next = e;
                                }
                                loTail = e;
                            } else {
                                if (hiTail == null) {
                                    hiHead = e;
                                } else {
                                    hiTail.next = e;
                                }
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[i] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[i + oldCapacity] = hiHead;
                        }
                    } else {
                        newTab[e.hash & (newCapacity - 1)] = e;
                    }
                }
            }
            table = newTab;
        }
        return table;
    }
}
