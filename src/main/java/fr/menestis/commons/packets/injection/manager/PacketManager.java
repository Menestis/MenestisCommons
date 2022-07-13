package fr.menestis.commons.packets.injection.manager;

import fr.menestis.commons.packets.injection.BiObject;
import fr.menestis.commons.packets.injection.PacketModel;
import fr.menestis.commons.packets.injection.listener.Listen;
import fr.menestis.commons.packets.injection.listener.PacketListener;
import net.minecraft.server.v1_8_R3.Packet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class PacketManager {

    private static final PacketManager INSTANCE = new PacketManager();
    private final Map<Class<? extends Packet>, List<BiObject<PacketListener, Method>>> packetListeners = new HashMap<>();

    private PacketManager() {
    }

    public static PacketManager getInstance() {
        return INSTANCE;
    }

    public boolean call(final PacketModel packetModel) throws InvocationTargetException, IllegalAccessException {
        final List<BiObject<PacketListener, Method>> biObjects = this.packetListeners.get(packetModel.getPacket().getClass());

        if (biObjects == null) {
            // Even if this packet is not Listen, he should be handle by Bukkit
            return true;
        }

        for (BiObject<PacketListener, Method> listenerMethod : biObjects) {
            final PacketListener packetListener = listenerMethod.getFirst();
            final Method method = listenerMethod.getSecond();

            method.setAccessible(true); // Allow methods to be private !
            method.invoke(packetListener, packetModel);

            // Return to know if the packet should be handle by the server or not
            return packetModel.isHandle();
        }

        // By default, we handle all packets (default server comportment)
        return true;
    }

    public void register(PacketListener packetListener) {
        final List<Method> listenerMethods = Arrays.stream(packetListener.getClass().getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(Listen.class))
                .collect(Collectors.toList());

        // Remove those lines to hide Warning when registering parent class and children does not have any Packet Listen
        // if (listenerMethods.isEmpty())
        // LogUtils.warning("Registering a class as PacketListener with zero @Listen method !");

        for (Method listenerMethod : listenerMethods) {
            final Listen annotation = listenerMethod.getAnnotation(Listen.class);
            final List<BiObject<PacketListener, Method>> entries = this.packetListeners.getOrDefault(annotation.packet(), new ArrayList<>());
            entries.add(new BiObject<>(packetListener, listenerMethod));

            this.packetListeners.put(annotation.packet(), entries);
        }
    }

    public void clear() {
        packetListeners.clear();
    }
}
