package me.chaseoes.firstjoinplus;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class FirstJoinPlus extends JavaPlugin {

    public final Logger log = Logger.getLogger("Minecraft");
    public String latestVersion = null;

    @Override
    public void onEnable() {
        // Listener Registration
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListeners(this), this);
        pm.registerEvents(new FirstJoinListener(this), this);
        Utilities.getUtilities().setup(this);

        // Configuration
        getConfig().options().header("FirstJoinPlus " + getDescription().getVersion() + " Configuration -- Please see: https://github.com/chaseoes/FirstJoinPlus/wiki/Configuration #");
        getConfig().options().copyDefaults(true);
        getConfig().options().copyHeader(true);
        saveConfig();

        // Metrics
        if (getConfig().getBoolean("settings.metrics")) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();
            } catch (IOException e) {
                // Failed to submit!
            }
        }

        // Check for updates every 30 minutes.
        getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
            @Override 
            public void run() {
                UpdateChecker update = new UpdateChecker();
                latestVersion = update.getLatestVersion();
                if (Utilities.getUtilities().needsUpdate()) {
                    for (Player player : getServer().getOnlinePlayers()) {
                        player.sendMessage("§e[§lFirstJoinPlus§r§e] §aA new version is available!");
                        player.sendMessage("§e[§lFirstJoinPlus§r§e] §aDownload it at: §ohttp://dev.bukkit.org/server-mods/firstjoinplus/");
                    }
                }
            }
        }, 3660L);

    }

    @Override
    public void onDisable() {
        reloadConfig();
        saveConfig();
        getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {

        if (strings.length < 1 || strings.length > 1) {
            cs.sendMessage("§e[§lFirstJoinPlus§r§e] §4Usage: /firstjoinplus <reload|setspawn|spawn|items|motd>");
            return true;
        }

        if (strings[0].equalsIgnoreCase("reload")) {
            if (cs.hasPermission("firstjoinplus.reload")) {
                reloadConfig();
                saveConfig();
                cs.sendMessage("§e[§lFirstJoinPlus§r§e] §aSuccessfully reloaded the configuration!");
            } else {
                noPermission(cs);
            }
            return true;
        }

        if (!(cs instanceof Player)) {
            cs.sendMessage("§e[§lFirstJoinPlus§r§e] §cYou must be a player to do that.");
            return true;
        }

        Player player = (Player) cs;
        if (strings[0].equalsIgnoreCase("motd")) {
            if (cs.hasPermission("firstjoinplus.motd")) {
                List<String> motd = getConfig().getStringList("motd");
                for (String motdStr : motd) {
                    cs.sendMessage(Utilities.getUtilities().format(motdStr, player));
                }
            } else {
                noPermission(cs);
            }
        }

        if (strings[0].equalsIgnoreCase("setspawn")) {
            if (cs.hasPermission("firstjoinplus.setspawn")) {
                getConfig().set("settings.firstjoinspawning", true);
                getConfig().set("spawn.x", player.getLocation().getBlockX());
                getConfig().set("spawn.y", player.getLocation().getBlockY());
                getConfig().set("spawn.z", player.getLocation().getBlockZ());
                getConfig().set("spawn.pitch", player.getLocation().getPitch());
                getConfig().set("spawn.yaw", player.getLocation().getYaw());
                getConfig().set("spawn.world", player.getLocation().getWorld().getName());
                saveConfig();
                reloadConfig();
                cs.sendMessage("§e[§lFirstJoinPlus§r§e] §aSuccessfully set the first join spawnpoint!");
                cs.sendMessage("§e[§lFirstJoinPlus§r§e] §aTest it by typing: §o/firstjoinplus spawn");
            } else {
                noPermission(cs);
            }
        }

        if (strings[0].equalsIgnoreCase("items")) {
            if (cs.hasPermission("firstjoinplus.items")) {
                getConfig().set("settings.itemonfirstjoin", true);
                Utilities.getUtilities().giveFirstJoinItems(player);
                cs.sendMessage("§e[§lFirstJoinPlus§r§e] §aSuccessfully gave all items defined in the configuration.");
            } else {
                noPermission(cs);
            }
        }

        if (strings[0].equalsIgnoreCase("spawn")) {
            if (cs.hasPermission("firstjoinplus.spawn")) {
                if (!getConfig().getBoolean("settings.firstjoinspawning")) {
                    cs.sendMessage("§e[§lFirstJoinPlus§r§e] §cYou haven't set a first join spawnpoint yet.");
                    return true;
                }

                Utilities.getUtilities().teleportToFirstSpawn(player);
                cs.sendMessage("§e[§lFirstJoinPlus§r§e] §aSuccessfully teleported to the first join spawnpoint.");
            } else {
                noPermission(cs);
            }
        }
        return true;
    }

    public void noPermission(CommandSender cs) {
        cs.sendMessage("§e[§lFirstJoinPlus§r§e] §cYou don't have permission for that.");
    }

}