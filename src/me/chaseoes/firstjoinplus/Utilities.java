package me.chaseoes.firstjoinplus;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import uk.org.whoami.geoip.GeoIPLookup;
import uk.org.whoami.geoip.GeoIPTools;

public class Utilities {

    private FirstJoinPlus plugin;
    static Utilities instance = new Utilities();
    public GeoIPLookup geoiplookup;

    private Utilities() {

    }

    public static Utilities getUtilities() {
        return instance;
    }

    public void setup(FirstJoinPlus p) {
        plugin = p;
        Plugin geoiptools = plugin.getServer().getPluginManager().getPlugin("GeoIPTools");
        if (geoiptools != null) {
            geoiplookup = ((GeoIPTools) geoiptools).getGeoIPLookup();
        }
    }

    public void log(String l) {
        plugin.log.info("[FirstJoinPlus] " + l);
    }

    public boolean onlyfirstjoin() {
        if (plugin.getConfig().getBoolean("settings.onlyfirstjoin")) {
            return true;
        }
        return false;
    }

    public void teleportToFirstSpawn(Player player) {
        int x = plugin.getConfig().getInt("spawn.x");
        int y = plugin.getConfig().getInt("spawn.y");
        int z = plugin.getConfig().getInt("spawn.z");
        float pitch = plugin.getConfig().getInt("spawn.pitch");
        float yaw = plugin.getConfig().getInt("spawn.yaw");
        Location tp = new Location(plugin.getServer().getWorld(plugin.getConfig().getString("spawn.world")), x, y, z, yaw, pitch);
        tp.add(.5, 0, .5);
        player.teleport(tp);
    }

    public boolean needsUpdate() {
        String pageurl = UpdateChecker.fetch("http://emeraldsmc.com/fjp/");
        if (!pageurl.equalsIgnoreCase(plugin.getDescription().getVersion())) {
            return true;
        }
        return false;
    }

    public String colorize(String s) {
        if (s == null) {
            return null;
        }
        return s.replaceAll("&([l-o0-9a-f])", "\u00A7$1");
    }

    public String format(String string, Player player) {
        String format = string.replace("%name%", player.getName()).replace("%displayname%", player.getDisplayName());
        if (format.contains("%city%") || format.contains("%country%") && geoIPInstalled()) {
            format = format.replace("%city%", getCity(player)).replace("%country%", getCountry(player));
        }
        if (format.contains("%number")) {
            format = format.replace("%number%", getUniquePlayerCount());
        }
        return colorize(format);
    }

    public String getUniquePlayerCount() {
        return new File(plugin.getConfig().getString("settings.worldname") + "/players/").list().length + "";
    }

    public void giveFirstJoinItems(Player player) {
        for (String itemStr : plugin.getConfig().getStringList("items")) {
            ItemStack istack = new ItemStack(Integer.parseInt(itemStr.split("\\.")[0]), Integer.parseInt(itemStr.split("\\.")[1]), (short) 0, Byte.parseByte(itemStr.split("\\.")[2]));
            player.getInventory().addItem(istack);
        }
    }

    public boolean geoIPInstalled() {
        return geoiplookup != null;
    }

    public String getCity(Player player) {
        if (geoIPInstalled()) {
            try {
                return geoiplookup.getLocation(player.getAddress().getAddress()).city;
            } catch (NullPointerException ex) {
                return "unknown";
            }
        }
        return "unknown";
    }

    public String getCountry(Player player) {
        if (geoIPInstalled()) {
            try {
                return geoiplookup.getCountry(player.getAddress().getAddress()).getName();
            } catch (NullPointerException ex) {
                return "unknown";
            }
        }
        return "unknown";
    }

}
