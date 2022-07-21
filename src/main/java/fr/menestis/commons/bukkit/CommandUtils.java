package fr.menestis.commons.bukkit;

import fr.menestis.commons.packets.injection.SimpleReflection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.util.Map;

/**
 * @author Ariloxe
 */
public class CommandUtils {

    public static void registerCommand(String fallback, Command command) {
        ((CraftServer) Bukkit.getServer()).getCommandMap().register(fallback, command);
    }

    public static void unregisterCommand(String fallback, Command command) {
        try {
            SimpleCommandMap commandMap = ((CraftServer) Bukkit.getServer()).getCommandMap();
            Map<String, Command> commands = (Map<String, Command>) SimpleReflection.getFieldValue(commandMap, "knownCommands");
            Command removed = commands.remove(fallback + ":" + command.getName());
            if (removed != null && removed == command)
                removed.unregister(commandMap);
            removed = commands.remove(command.getName());
            if (removed != null && removed == command)
                removed.unregister(commandMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}