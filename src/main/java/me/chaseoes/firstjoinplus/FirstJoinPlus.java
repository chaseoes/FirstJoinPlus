package me.chaseoes.firstjoinplus;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import me.chaseoes.firstjoinplus.metrics.MetricsLite;
import me.chaseoes.firstjoinplus.utilities.UpdateChecker;
import me.chaseoes.firstjoinplus.utilities.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import uk.org.whoami.geoip.GeoIPLookup;
import uk.org.whoami.geoip.GeoIPTools;

public class FirstJoinPlus extends JavaPlugin {

    private static FirstJoinPlus instance;
    public UpdateChecker update;
    String smile = "Girls with the prettiest smiles, have the saddest stories.";

    public static FirstJoinPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListeners(), this);
        pm.registerEvents(new FirstJoinListener(this), this);
        Utilities.getUtilities().setup(this);
        instance = this;
        update = new UpdateChecker();
        update.startTask();
        
        if (getConfig().getBoolean("on-first-join.give-written-books.enabled")) {
            saveResource("rules.txt", false);
        }

        // Compatibility
        if (getConfig().getString("settings.worldname") != null) {
            File configuration = new File(getDataFolder() + "/config.yml");
            configuration.setWritable(true);
            configuration.renameTo(new File(getDataFolder() + "/old-config.yml"));
            String[] sections = getConfig().getConfigurationSection("").getKeys(false).toArray(new String[0]);
            for (String s : sections) {
                getConfig().set(s, null);
            }
            saveConfig();
            getLogger().log(Level.SEVERE, "Your configuration was found to be outdated, so we generated a new one for you.");
        }

        // Configuration
        getConfig().options().header("FirstJoinPlus " + getDescription().getVersion() + " Configuration -- Please see: https://github.com/chaseoes/FirstJoinPlus/wiki/Configuration #");
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();

        // Metrics
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit! :(
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        reloadConfig();
        saveConfig();
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        String prefix = ChatColor.DARK_GREEN + "[FirstJoinPlus] ";
        String noPermission = prefix + ChatColor.DARK_RED + "You don't have permission.";

        if (strings.length == 0) {
            cs.sendMessage(ChatColor.GOLD + "FirstJoinPlus " + ChatColor.GRAY + "version " + ChatColor.GOLD + getDescription().getVersion() + ChatColor.GRAY + " by chaseoes.");
            return true;
        }

        if (strings.length != 1) {
            cs.sendMessage(prefix + ChatColor.DARK_RED + "Usage: /firstjoinplus <reload|setspawn|spawn|items|motd>");
            return true;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            if (cs.hasPermission("firstjoinplus.reload")) {
                reloadConfig();
                saveConfig();
                if (getConfig().getBoolean("on-first-join.give-written-books.enabled")) {
                    saveResource("rules.txt", false);
                }
                cs.sendMessage(prefix + ChatColor.GREEN + "Successfully reloaded the configuration.");
            } else {
                cs.sendMessage(noPermission);
            }
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage("You must be a player to do that.");
            return true;
        }
        
        Player player = (Player) cs;
        
        if (strings[0].equalsIgnoreCase("motd")) {
            if (cs.hasPermission("firstjoinplus.motd")) {
                for (String motdStr : getConfig().getStringList("on-first-join.send-motd.messages")) {
                    cs.sendMessage(Utilities.getUtilities().formatVariables(motdStr, player));
                }
            } else {
                cs.sendMessage(noPermission);
            }
        }

        if (strings[0].equalsIgnoreCase("setspawn")) {
            if (cs.hasPermission("firstjoinplus.setspawn")) {
                getConfig().set("on-first-join.teleport", true);
                getConfig().set("spawn.x", player.getLocation().getBlockX());
                getConfig().set("spawn.y", player.getLocation().getBlockY());
                getConfig().set("spawn.z", player.getLocation().getBlockZ());
                getConfig().set("spawn.pitch", player.getLocation().getPitch());
                getConfig().set("spawn.yaw", player.getLocation().getYaw());
                getConfig().set("spawn.world", player.getLocation().getWorld().getName());
                saveConfig();
                reloadConfig();
                cs.sendMessage(prefix + ChatColor.GREEN + "Successfully set the first join spawn location.");
            } else {
                cs.sendMessage(noPermission);
            }
        }

        if (strings[0].equalsIgnoreCase("items")) {
            if (cs.hasPermission("firstjoinplus.items")) {
                Utilities.getUtilities().giveFirstJoinItems(player);
                if (getConfig().getBoolean("on-first-join.give-written-books.enabled")) {
                    Utilities.getUtilities().giveWrittenBooks(player);
                }
                cs.sendMessage(prefix + ChatColor.GREEN + "Successfully gave all defined items.");
            } else {
                cs.sendMessage(noPermission);
            }
        }

        if (strings[0].equalsIgnoreCase("spawn")) {
            if (cs.hasPermission("firstjoinplus.spawn")) {
                if (!getConfig().getBoolean("on-first-join.teleport")) {
                    cs.sendMessage(prefix + ChatColor.DARK_RED + "A first join spawn location hasn't been set.");
                    return true;
                }

                player.teleport(Utilities.getUtilities().getFirstJoinLocation());
                cs.sendMessage(prefix + ChatColor.GREEN + "Successfully teleported to the first join spawn location.");
            } else {
                cs.sendMessage(noPermission);
            }
        }
        return true;
    }
    
    public GeoIPLookup getGeoIPLookup() {
        Plugin pl = getServer().getPluginManager().getPlugin("GeoIPTools");
        if(pl != null) {
            return ((GeoIPTools) pl).getGeoIPLookup();
        }
        return null;
    }

}