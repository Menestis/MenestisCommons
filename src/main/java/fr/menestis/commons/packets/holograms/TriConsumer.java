package fr.menestis.commons.packets.holograms;

/**
 * @author Blendman974
 */
public interface TriConsumer<U, V, W> {
    void accept(U u, V v, W w);
}
