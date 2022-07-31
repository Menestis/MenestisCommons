package fr.menestis.commons.bukkit.moderation.uis;

import fr.blendman.magnet.api.MagnetApi;
import fr.blendman.magnet.api.server.ServerCacheHandler;
import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import fr.menestis.commons.bukkit.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CasierAutreGui {


    private final String pseudo;
    private final KInventory kInventory = new KInventory(54, "§8┃ §cModération §f(§cAutre§f)");

    public CasierAutreGui(String pseudo) {
        this.pseudo = pseudo;

        List<Integer> list = new ArrayList<>(Arrays.asList(0, 1, 9, 7, 8, 17, 43, 44, 36, 35, 36, 37, 27));

        for (int vitre : list)
            kInventory.setElement(vitre, new KItem(new ItemCreator(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 8)).name("§c").get()));

        getItem("Usebug", "usebug", Material.STONE_AXE, 10);
        getItem("Tower/Camp", "tower_camping", Material.COBBLESTONE, 11);
        getItem("iPvP/Stalk", "ipvp_stalking", Material.LAVA_BUCKET, 12);
        getItem("SpecHelp", "spec_helping", Material.FEATHER, 13);
        getItem("Hors-Règles", "out_of_rules", Material.EYE_OF_ENDER, 14);
    }

    private void getItem(String displayName, String sanction_type, Material material, int slot) {
        KItem kItem = new KItem(new ItemCreator(material).name("§8» §c" + displayName).lore("§7Vous permet de gérer §e" + this.pseudo, "", "§8» §bClique-Gauche §7pour appliquer cette sanction.", "§8» §bClique-Droit §7pour retirer cette sanction.").get());
        kItem.addCallback((kInventoryRepresentation, itemStack, player, kInventoryClickContext) -> {
            MagnetApi.MagnetStore.getApi().getPlayerHandle().getPlayerInfo(pseudo).thenAccept(playerInfo -> {
                if(playerInfo.getPower() >= ServerCacheHandler.ServerCacheHandlerStore.getServerCacheHandler().getInfo(player.getUniqueId()).getPower()){
                    player.sendMessage("§3§lMenestis §f» §cErreur : Vous ne pouvez pas sanctionner un joueur ayant des permissions similaires ou supérieures à vous.");
                    return;
                }

                if(kInventoryClickContext.getClickType().isLeftClick()){
                    MagnetApi.MagnetStore.getApi().getPlayerHandle().getPlayerUUID(pseudo)
                            .thenCompose(uuid -> MagnetApi.MagnetStore.getApi().getPlayerHandle().sanctionPlayer(uuid, sanction_type, player.getUniqueId(), false))
                            .thenAccept(playerSanctionResult -> {
                                if (playerSanctionResult.getSanction() == null) {
                                    player.sendMessage("§3§lMenestis §f» §cErreur : Vous ne pouvez pas sanctionner ce joueur car il subit déjà une sanction du même type.");
                                    return;
                                }

                                String value = playerSanctionResult.getSanction().getValue();
                                player.sendMessage("§3§lMenestis §f» §7Vous avez sanctionné §e" + pseudo + " §8(§e" + value + "§8)");
                                if (playerSanctionResult.getId() != null)
                                    player.sendMessage("§7ID de la sanction : " + playerSanctionResult.getId());
                            })
                            .exceptionally(throwable -> {
                                throwable.printStackTrace();
                                return null;
                            });
                } else if(kInventoryClickContext.getClickType().isRightClick()){
                    MagnetApi.MagnetStore.getApi().getPlayerHandle().getPlayerUUID(pseudo)
                            .thenCompose(uuid -> MagnetApi.MagnetStore.getApi().getPlayerHandle().sanctionPlayer(uuid, sanction_type, player.getUniqueId(), true))
                            .thenAccept(playerSanctionResult -> {
                                player.sendMessage("§3§lMenestis §f» §7Vous avez retiré la sanction de §e" + pseudo);
                            }).exceptionally(throwable -> {
                                throwable.printStackTrace();
                                return null;
                            });
                }
            });

        });

        this.kInventory.setElement(slot, kItem);
    }

    public void open(Player player) {
        this.kInventory.open(player);
    }


}
