package me.chaseoes.firstjoinplus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class FirstJoinListener implements Listener {

    private final JavaPlugin plugin;

    public FirstJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFirstJoin(FirstJoinEvent event) {

        // Variables!
        Player player = event.getPlayer();

        // Show the first join message.
        if (plugin.getConfig().getBoolean("settings.showfirstjoinmessage")) {
            event.setFirstJoinMessage(Utilities.getUtilities().format(plugin.getConfig().getString("messages.firstjoinmessage"), player));
        }

        // Give a player an item on their first join.
        if (plugin.getConfig().getBoolean("settings.itemonfirstjoin")) {
            Utilities.getUtilities().giveFirstJoinItems(player);
        }

        // Show the first join MOTD.
        if (plugin.getConfig().getBoolean("settings.showfirstjoinmotd")) {
            for (String motdStr : plugin.getConfig().getStringList("motd")) {
                player.sendMessage(Utilities.getUtilities().format(motdStr, player));
            }
        }

        // Teleport the player to the first join spawnpoint.
        if (plugin.getConfig().getBoolean("settings.firstjoinspawning")) {
            final Player fp = event.getPlayer();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Utilities.getUtilities().teleportToFirstSpawn(fp);
                }
            }, 3L);
        }

        // Show some fancy smoke!
        // Can't wait for a Firework API to implement the same idea!
        if (plugin.getConfig().getBoolean("settings.showfirstjoinsmoke")) {
            for (int i = 0; i <= 25; i++) {
                player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
            }
        }

        // Play a sound!
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            if (p.hasPermission("firstjoinplus.notify")) {
                Sound s = Sound.valueOf(plugin.getConfig().getString("settings.notify-sound"));
                p.playSound(p.getLocation(), s, 1, 1);
            }
        }

        // Give some XP!
        if (plugin.getConfig().getInt("settings.xponfirstjoin") != 0) {
            player.setLevel(plugin.getConfig().getInt("settings.xponfirstjoin"));
        }

        // Run some commands!
        if (plugin.getConfig().getBoolean("settings.commandsonfirstjoin")) {
            for (String command : plugin.getConfig().getStringList("commands")) {
                String cmnd = Utilities.getUtilities().format(command, player);
                player.performCommand(cmnd);
            }
        }

        // Run some more commands!
        if (plugin.getConfig().getBoolean("settings.console-commandsonfirstjoin")) {
            for (String command : plugin.getConfig().getStringList("console-commands")) {
                String cmnd = Utilities.getUtilities().format(command, player);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmnd);
            }
        }

        // Show the number of players who have joined in total.
        if (plugin.getConfig().getBoolean("settings.numberonfirstjoin")) {
            plugin.getServer().broadcastMessage(Utilities.getUtilities().format(plugin.getConfig().getString("messages.numbermessage"), player));
        }

        // Make the player invincible for x seconds.
        Integer invincible = plugin.getConfig().getInt("settings.invincibleonfirstjoin");
        if (invincible != 0) {
            Utilities.getUtilities().invincible.add(player.getName());
            final Player p = player;
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    Utilities.getUtilities().invincible.remove(p.getName());
                }
            }, invincible * 20L);
        }

        // Written books!
        if (plugin.getConfig().getBoolean("settings.writtenbooksonfirstjoin")) {
            for (String file : plugin.getConfig().getStringList("written-books")) {
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

    }

}
