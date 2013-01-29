package me.chaseoes.firstjoinplus.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashSet;
import java.util.logging.Level;

import me.chaseoes.firstjoinplus.FirstJoinPlus;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

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
        return new File(plugin.getServer().getWorlds().get(0).getName() + "/players/").list().length;
    }
    
    public void teleportToFirstSpawn(Player player) {
        int x = plugin.getConfig().getInt("spawn.x");
        int y = plugin.getConfig().getInt("spawn.y");
        int z = plugin.getConfig().getInt("spawn.z");
        float pitch = plugin.getConfig().getInt("spawn.pitch");
        float yaw = plugin.getConfig().getInt("spawn.yaw");
        player.teleport(new Location(plugin.getServer().getWorld(plugin.getConfig().getString("spawn.world")), x + 0.5, y, z + 0.5, yaw, pitch));
    }

    public void giveFirstJoinItems(Player player) {
        for (String itemStr : plugin.getConfig().getStringList("on-first-join.give-items.items")) {
            ItemStack i;
            String[] itemValues = itemStr.split("\\:");
            
            if (isNumber(itemValues[0])) {
                i = new ItemStack(Material.getMaterial(Integer.parseInt(itemValues[0])));
            } else {
                i = new ItemStack(Material.getMaterial(itemValues[0].toUpperCase()));
            }
            
            if (itemValues.length > 1) {
                i.setAmount(Integer.parseInt(itemValues[1]));
            }
            
            if (itemValues.length > 2) {
                i = new ItemStack(i.getType(), i.getAmount(), (short) Integer.parseInt(itemValues[2]));
            }
            
            player.getInventory().addItem(i);
        }
    }
    
    public void giveWrittenBooks(Player player) {
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
    
    public String formatVariables(String string, Player player) {
        return colorize(string.replace("%player-name", player.getName()).replace("%player-display-name", player.getDisplayName()).replace("%unique-players", getUniquePlayerCount() + ""));
    }
    
    public boolean isNumber(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
