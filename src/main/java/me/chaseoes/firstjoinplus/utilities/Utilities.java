package me.chaseoes.firstjoinplus.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

import me.chaseoes.firstjoinplus.FirstJoinPlus;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;

public class Utilities {

    private FirstJoinPlus plugin;
    static Utilities instance = new Utilities();
    public HashSet<String> invincible = new HashSet<String>();

    private Utilities() {

    }

    public static Utilities getUtilities() {
        return instance;
    }

    public void setup(FirstJoinPlus p) {
        plugin = p;
    }

    public String colorize(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public int getUniquePlayerCount() {
        return new File(plugin.getServer().getWorldContainer(),plugin.getServer().getWorlds().get(0).getName() + "/players/").list().length;
    }

    public Location getFirstJoinLocation() {
        int x = plugin.getConfig().getInt("spawn.x");
        int y = plugin.getConfig().getInt("spawn.y");
        int z = plugin.getConfig().getInt("spawn.z");
        float pitch = plugin.getConfig().getInt("spawn.pitch");
        float yaw = plugin.getConfig().getInt("spawn.yaw");
        return new Location(plugin.getServer().getWorld(plugin.getConfig().getString("spawn.world")), x + 0.5, y, z + 0.5, yaw, pitch);
    }
    
    public List<ItemStack> getFirstJoinKit() {
        List<ItemStack> kit = new ArrayList<ItemStack>();
        for (String s : FirstJoinPlus.getInstance().getConfig().getStringList("first-join-kit")) {
            String[] item = s.split("\\:");
            
            Material mat = Material.AIR;
            try {
                mat = Material.getMaterial(item[0]);
            } catch (Exception e) {
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "Error encountered while attempting to give a new player the first join kit. Unknown item name: " + item[0]);
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "Find and double check item names using this page:");
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "http://jd.bukkit.org/rb/apidocs/org/bukkit/Material.html");
            }
            
            int amount = 1;
            
            if (item[1] != null && isNumber(item[1])) {
                amount = Integer.parseInt(item[1]);
            } else {
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "Error encountered while attempting to give a new player the first join kit.");
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "The item amount must be a number: " + item[1]);
            }
            
            kit.add(new ItemStack(mat, amount));
        }
        return kit;
    }

    public void giveWrittenBooks(final Player player) {
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (String file : plugin.getConfig().getStringList("on-first-join.give-written-books.book-files")) {
                    ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                    BookMeta bm = (BookMeta) book.getItemMeta();
                    File f = new File(plugin.getDataFolder() + "/" + file);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        StringBuilder sb = new StringBuilder();
                        String line = br.readLine();
                        int i = 0;

                        while (line != null) {
                            i++;

                            if (i != 1 && i != 2) {
                                if (line.equalsIgnoreCase("/newpage") || line.equalsIgnoreCase("/np")) {
                                    bm.addPage(sb.toString());
                                    sb = new StringBuilder();
                                } else {
                                    sb.append(Utilities.getUtilities().colorize(line));
                                    sb.append("\n");
                                }
                            } else {
                                if (i == 1) {
                                    bm.setTitle(Utilities.getUtilities().colorize(line));
                                }
                                if (i == 2) {
                                    bm.setAuthor(Utilities.getUtilities().colorize(line));
                                }
                            }
                            line = br.readLine();
                        }

                        br.close();
                        bm.addPage(sb.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                        plugin.getLogger().log(Level.WARNING, "Error encountered while trying to give a new player a written book! (File " + file + ")");
                        plugin.getLogger().log(Level.WARNING, "Please check that the file exists and is readable.");
                    }

                    book.setItemMeta(bm);
                    player.getInventory().addItem(book);
                }
            }
        }, plugin.getConfig().getLong("on-first-join.give-written-books.delay"));
    }

    public void playSmoke(Location loc) {
        for (int i = 0; i <= 25; i++) {
            loc.getWorld().playEffect(loc, Effect.SMOKE, i);
        }
    }

    public void playFirework(Location loc) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fm = fw.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder().trail(true).flicker(false).withColor(Color.PURPLE).withFade(Color.PURPLE).with(Type.BALL_LARGE).build();
        fm.addEffect(effect);
        fm.setPower(2);
        fw.setFireworkMeta(fm);
    }

    public String formatVariables(String string, Player player) {
        return colorize(string.replace("%player-name", player.getName()).replace("%player-display-name", player.getDisplayName()).replace("%unique-players", getUniquePlayerCount() + "").replace("%country", getCountry(player)).replace("%city", getCity(player)).replace("%new-line", "\n"));
    }

    public String formatVariables(String string, Player player, String reason) {
        return colorize(string.replace("%player-name", player.getName()).replace("%player-display-name", player.getDisplayName()).replace("%unique-players", getUniquePlayerCount() + "").replace("%reason", reason).replace("%country", getCountry(player)).replace("%city", getCity(player)).replace("%new-line", "\n"));
    }

    public boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public String getCountry(Player player) {
        if (plugin.getGeoIPLookup() != null) {
            if (plugin.getGeoIPLookup().getLocation(player.getAddress().getAddress()) != null) {
                return plugin.getGeoIPLookup().getLocation(player.getAddress().getAddress()).countryName;
            }
            return "unknown";
        }
        return "N/A";
    }

    public String getCity(Player player) {
        if (plugin.getGeoIPLookup() != null) {
            if (plugin.getGeoIPLookup().getLocation(player.getAddress().getAddress()) != null) {
                return plugin.getGeoIPLookup().getLocation(player.getAddress().getAddress()).city;
            }
            return "unknown";
        }
        return "N/A";
    }

}
