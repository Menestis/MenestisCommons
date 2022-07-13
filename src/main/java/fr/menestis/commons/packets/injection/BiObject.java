package fr.menestis.commons.packets.injection;

/**
 * Useful object
 *
 * @param <K> The type of the first object
 * @param <V> The type of the second object
 */
public class BiObject<K, V> {

    private final K first;
    private final V second;

    /**
     * @param first  The first object
     * @param second The second object
     */
    public BiObject(final K first, final V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first object
     */
    public K getFirst() {
        return first;
    }

    /**
     * @return the second object
     */
    public V getSecond() {
        return second;
    }
}
