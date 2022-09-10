package fr.menestis.commons.bukkit.moderation.uis;

import fr.blendman.magnet.api.MagnetApi;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import fr.menestis.commons.bukkit.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Ariloxe
 */
public class CasierMainGui {

    private final KInventory kInventory;
    private final String pseudo;

    private CasierMessageGui casierMessageSanctionGui;
    private CasierCombatSanctionGui casierCombatSanctionGui;
    private CasierAutreGui casierAutreGui;


    public CasierMainGui(String pseudo) {

        this.pseudo = pseudo;

        this.kInventory = new KInventory(54, "§8┃ §cModération §f(§c" + pseudo + "§f)");

        List<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 9, 7, 8, 17, 43, 44, 36, 35, 36, 37, 27));

        for (int vitre : list)
            kInventory.setElement(vitre, new KItem(new ItemCreator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 14)).name("§c").get()));

        MagnetApi.MagnetStore.getApi().getPlayerHandle().getPlayerInfo(pseudo).thenAccept(playerInfo -> {
            KItem profileItem = new KItem(new ItemCreator(new ItemStack(Material.SKULL_ITEM, 1, (byte) SkullType.PLAYER.ordinal())).owner(pseudo).name("§f(§c!§f) §c" + pseudo).lore("", "  §8• §fParamètres de Modération", "", "  §8• §" + (playerInfo.getBan() == null ? "cNon-banni(e)" : "aBanni(e)" + (playerInfo.getBan().getReason() == null ? "" : " §7(" + playerInfo.getBan().getReason() + ")"))).get());
            this.kInventory.setElement(5, profileItem);
        }).exceptionally(throwable -> {
            throwable.printStackTrace();
            return null;
        });

        KItem viewItem = new KItem(new ItemCreator(Material.CHEST).name("§f» §cInventaire §f«").lore("", "§fVous permet d'accéder à l'§cinventaire", "§factuel du joueur.", "", Bukkit.getPlayer(pseudo) == null ? "§cImpossible: joueur déconnecté." : "§8➠ §7Cliquez pour voir son inventaire.").get());
        viewItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            Player target = Bukkit.getPlayer(pseudo);
            if (target == null) {
                player.sendMessage("§c§lMod §f§l» §cAction impossible: ce joueur n'est pas en ligne.");
                return;
            }

            new CasierViewInventoryGui(player, target, this);
        });

        KItem kickItem = new KItem(new ItemCreator(Material.SIGN).name("§f» §cAutres §f«").lore("", "§fVous permet de gérer les autres", "§fcatégories du joueur.", "", "§8➠ §7Cliquez pour entamer la procédure.").get());
        kickItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (casierAutreGui == null)
                this.casierAutreGui = new CasierAutreGui(this.pseudo);
            this.casierAutreGui.open(player);
        });

        KItem banItem = new KItem(new ItemCreator(Material.ANVIL).name("§f» §cCombat §f«").lore("", "§fVous permet de gérer les combats", "§fdu joueur.", "", "§8➠ §7Cliquez pour entamer la procédure.").get());
        banItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (casierCombatSanctionGui == null)
                this.casierCombatSanctionGui = new CasierCombatSanctionGui(this.pseudo);
            this.casierCombatSanctionGui.open(player);
        });


        KItem muteItem = new KItem(new ItemCreator(Material.NAME_TAG).name("§f» §cMessages §f«").lore("", "§fVous permet de gérer les messages", "§fdu joueur.", "", "§8➠ §7Cliquez pour entamer la procédure.").get());
        muteItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            if (casierMessageSanctionGui == null)
                this.casierMessageSanctionGui = new CasierMessageGui(this.pseudo);
            this.casierMessageSanctionGui.open(player);
        });


        this.kInventory.setElement(3, viewItem);

        this.kInventory.setElement(11, kickItem);
        this.kInventory.setElement(13, banItem);
        this.kInventory.setElement(15, muteItem);

    }

    public String getPseudo() {
        return pseudo;
    }

    public void open(Player player) {
        this.kInventory.open(player);
    }
}
