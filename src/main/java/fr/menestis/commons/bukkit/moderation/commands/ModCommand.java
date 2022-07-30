package fr.menestis.commons.bukkit.moderation.commands;

import fr.menestis.commons.bukkit.moderation.ModerationManager;
import fr.menestis.commons.bukkit.moderation.uis.CasierMainGui;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ModCommand extends Command {
    public ModCommand() {
        super("mod");
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!commandSender.hasPermission("menestis.moderate")) {
            commandSender.sendMessage("§cVous n’avez pas la permission.");
            return true;
        }

        if (strings.length == 0) {
            Player player = ((Player) commandSender);

            if (ModerationManager.getInstance().getVanishPlayers().contains(player.getUniqueId())) {
                ModerationManager.getInstance().getVanishPlayers().remove(player.getUniqueId());
                Bukkit.getOnlinePlayers().forEach(player1 -> player.showPlayer(player));
                player.setGameMode(GameMode.SURVIVAL);

                player.teleport(ModerationManager.getInstance().getUuidLocationMap().get(player.getUniqueId()));
                player.getInventory().setContents(ModerationManager.getInstance().getUuidInventoryMap().get(player.getUniqueId()).getContents());

                ModerationManager.getInstance().getUuidInventoryMap().remove(player.getUniqueId());
                ModerationManager.getInstance().getUuidLocationMap().remove(player.getUniqueId());
            } else {
                ModerationManager.getInstance().getVanishPlayers().add(player.getUniqueId());
                Bukkit.getOnlinePlayers().forEach(player1 -> player.hidePlayer(player));
                player.setGameMode(GameMode.SPECTATOR);

                ModerationManager.getInstance().getUuidLocationMap().put(player.getUniqueId(), player.getLocation());
                ModerationManager.getInstance().getUuidInventoryMap().put(player.getUniqueId(), player.getInventory());
            }

            return false;
        } else if (strings.length == 1) {
            new CasierMainGui(strings[0]).open(((Player) commandSender));
            return false;
        }

        return false;
    }
}
