package fr.menestis.commons.bukkit.moderation.uis;

import fr.blendman974.kinventory.inventories.KInventory;
import fr.blendman974.kinventory.inventories.KItem;
import fr.menestis.commons.bukkit.ItemCreator;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * @author Ariloxe
 */
public class CasierViewInventoryGui {

    public CasierViewInventoryGui(Player player, Player target, CasierMainGui casierMainGui) {
        KInventory kInventory = new KInventory(54, "§8┃ §cModération §f(§cView§f)");

        KItem armor = new KItem(new ItemCreator(Material.ARMOR_STAND).name("§8» §eSlot d'Armure §8«").get());

        kInventory.setElement(49, armor);
        kInventory.setElement(48, armor);
        kInventory.setElement(47, armor);
        kInventory.setElement(46, armor);
        kInventory.setElement(45, new KItem(new ItemCreator(Material.BARRIER).name("§8» §eSlot de Main §8«").get()));

        if (target.getInventory().getHelmet() != null)
            kInventory.setElement(49, new KItem(target.getInventory().getHelmet()));
        if (target.getInventory().getChestplate() != null)
            kInventory.setElement(48, new KItem(target.getInventory().getChestplate()));
        if (target.getInventory().getLeggings() != null)
            kInventory.setElement(47, new KItem(target.getInventory().getLeggings()));
        if (target.getInventory().getBoots() != null)
            kInventory.setElement(46, new KItem(target.getInventory().getBoots()));


        if (target.getItemInHand() != null && target.getItemInHand().getType() != Material.AIR)
            kInventory.setElement(45, new KItem(target.getItemInHand()));

        KItem info = new KItem(new ItemCreator(Material.ARROW).name("§8» §cRetour §8«").lore("", "§7Permet de retourner au menu précédent", "").get());
        info.addCallback((kInventoryRepresentation, itemStack, player1, kInventoryClickContext) -> {
            casierMainGui.open(player1);
        });

        KItem infoExp = new KItem(new ItemCreator(Material.EXP_BOTTLE).name("§8» §eExpérience §8«").lore("", "§8» §7Points d'Expérience: §a" + (int) target.getExp(), "").get());
        KItem infoArmor = new KItem(new ItemCreator(Material.IRON_CHESTPLATE).name("§8» §eVie §8«").amount(((int) target.getHealth())).lore("", "§8» §7Points de PV: §c" + (int) target.getHealth(), "").get());
        KItem infoBouffe = new KItem(new ItemCreator(Material.COOKED_BEEF).name("§8» §eFaim §8«").amount(((int) target.getSaturation())).lore("", "§8» §7Points de faim: §f" + (int) target.getSaturation(), "").get());

        kInventory.setElement(50, infoArmor);
        kInventory.setElement(51, infoBouffe);
        kInventory.setElement(52, infoExp);
        kInventory.setElement(53, info);

        KItem glass = new KItem(new ItemCreator(Material.STAINED_GLASS_PANE).name("§a").get());

        for (int i = 9; i < 18; i++)
            kInventory.setElement(i, glass);

        int i = 0;
        for (ItemStack item : target.getInventory().getContents()) {
            if (i < 8) {
                if (item != null && item.getType() != Material.AIR)
                    kInventory.setElement(i, new KItem(item));

                i++;
            } else if (i > 17) {
                if (item != null && item.getType() != Material.AIR)
                    kInventory.setElement(i, new KItem(item));

                i++;
            } else
                i = 18;

        }

        kInventory.open(player);
    }

}
