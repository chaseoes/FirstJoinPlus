package me.chaseoes.firstjoinplus;

import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        if (plugin.getConfig().getBoolean("settings.showfirstjoinsmoke")) {
            for (int i = 0; i <= 25; i++) {
                player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
                player.getWorld().playEffect(player.getLocation(), Effect.BOW_FIRE, i);
            }
        }

        // Give some XP!
        if (plugin.getConfig().getInt("settings.xponfirstjoin") != 0) {
            player.setLevel(plugin.getConfig().getInt("settings.xponfirstjoin"));
        }

        // Run some commands!
        if (plugin.getConfig().getBoolean("settings.commandsonfirstjoin")) {
            for (String command : plugin.getConfig().getStringList("commands")) {
                player.performCommand(command);
            }
        }

        // Show the number of players who have joined in total.
        if (plugin.getConfig().getBoolean("settings.numberonfirstjoin")) {
            plugin.getServer().broadcastMessage(Utilities.getUtilities().format(plugin.getConfig().getString("messages.numbermessage"), player));
        }

    }

}
