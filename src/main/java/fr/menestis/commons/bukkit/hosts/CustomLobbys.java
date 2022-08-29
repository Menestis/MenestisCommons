package fr.menestis.commons.bukkit.hosts;

import org.bukkit.Material;

/**
 * @author Ariloxe
 */
public enum CustomLobbys {

    CLASSIC("§7Défaut", Material.OBSIDIAN, "§7§lCOMMUN", 0, 0, 120, 0),

    FLIGHT_ISLAND("§7Ile Volante", Material.COBBLESTONE, "§e§lRARE", 100, 500, 120, 500),
    FARMER_GRASS_SPAWN("§7Ferme Hospitalière", Material.GRASS, "§e§lRARE", 100, -978, 100, -1000),
    TOPIQUEUR_SPAWN("§7Triopiqueur ", Material.BEDROCK, "§e§lRARE", 100, 750, 120, 750),
    JUJUTSU_SPAWN("§7Extension du Territoire", Material.FLOWER_POT_ITEM, "§e§lRARE", 100, -750, 120, 750),

    DEMONSLAYER_SPAWN("§7Plage Montagneuse", Material.SAND, "§8§lEPIQUE", 250, -500, 120, 500),
    FLYING_CASTE("§7Château Volant", Material.IRON_DOOR, "§8§lEPIQUE", 250, 425, 115, -531),
    JAPANESE_WORLD("§7Monde Japonais", Material.BANNER, "§8§lEPIQUE", 250, -500, 120, -500),
    LITTLE_WORLD("§7Monde Merveilleux", Material.WOOD, "§8§lEPIQUE", 250, -1000, 120, 1000),

    MOONBASE("§7Base Lunaire", Material.STONE, "§6§lLEGENDAIRE", 500, 999, 120, -1000),
    WEREWOLF_SPAWN("§7Forêt Lycanthrope", Material.BONE, "§6§lLEGENDAIRE", 500, 999, 120, -1000),
    SNK_SPAWN("§7District de Shiganshina", Material.FEATHER, "§6§lLEGENDAIRE", 500, 750, 120, -750),
    ;

    private final String name;
    private final Material icon;
    private final String rarety;
    private final int price;
    private final double locationX;
    private final double locationY;
    private final double locationZ;

    //create constructor
    CustomLobbys(String name, Material icon, String rarety, int price, double locationX, double locationY, double locationZ) {
        this.name = name;
        this.icon = icon;
        this.rarety = rarety;
        this.price = price;
        this.locationX = locationX;
        this.locationY = locationY;
        this.locationZ = locationZ;
    }

    public String getName() {
        return name;
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public double getLocationZ() {
        return locationZ;
    }

    public int getPrice() {
        return price;
    }

    public Material getIcon() {
        return icon;
    }

    public String getRarety() {
        return rarety;
    }

}
