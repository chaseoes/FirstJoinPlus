package me.chaseoes.firstjoinplus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerListeners implements Listener {

    private final JavaPlugin plugin;

    public PlayerListeners(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // First Join Detection
    @EventHandler(priority = EventPriority.LOWEST)
    public void firstJoinDetection(PlayerJoinEvent event) {
        // Define our variables.
        Player player = event.getPlayer();

        // Call the first join event.
        if (!player.hasPlayedBefore()) {
            plugin.getServer().getPluginManager().callEvent(new FirstJoinEvent(event));
            return;
        }
    }

    // Join Handling
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!Utilities.getUtilities().onlyfirstjoin()) {
            // Variables
            StringBuilder joinmsg = new StringBuilder();

            // Show Join Message and Number on Join
            if (player.hasPlayedBefore()) {
                if (plugin.getConfig().getBoolean("settings.showjoinmessage")) {
                    joinmsg.append(Utilities.getUtilities().format(plugin.getConfig().getString("messages.joinmessage"), player));
                    if (plugin.getConfig().getBoolean("settings.numberonjoin")) {
                        joinmsg.append("\n" + Utilities.getUtilities().format(plugin.getConfig().getString("messages.numbermessage"), player));
                    }
                    event.setJoinMessage(joinmsg.toString());
                } else {
                    event.setJoinMessage(null);
                }
            }

        }
        
        // Check for Updates!
        if (player.isOp()) {
            if (Utilities.getUtilities().needsUpdate()) {
                player.sendMessage("§e[§lFirstJoinPlus§r§e] §aA new version is available!");
                player.sendMessage("§e[§lFirstJoinPlus§r§e] §aDownload it at: §ohttp://dev.bukkit.org/server-mods/firstjoinplus/");
            }
        }
    }

    // Quit Handling
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!Utilities.getUtilities().onlyfirstjoin()) {
            if (plugin.getConfig().getBoolean("settings.showleavemessage")) {
                event.setQuitMessage(Utilities.getUtilities().format(plugin.getConfig().getString("messages.leavemessage"), event.getPlayer()));
            } else {
                event.setQuitMessage(null);
            }
        }
    }

    // Change kick message.
    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (!Utilities.getUtilities().onlyfirstjoin()) {
            if (plugin.getConfig().getBoolean("settings.showkickmessage")) {
                event.setLeaveMessage(Utilities.getUtilities().format(plugin.getConfig().getString("messages.kickmessage"), event.getPlayer()));
            } else {
                event.setLeaveMessage(null);
            }
        }
    }

}