package me.chaseoes.firstjoinplus;

import me.chaseoes.firstjoinplus.utilities.Utilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void firstJoinDetection(PlayerJoinEvent event) {
        // Define our variables.
        Player player = event.getPlayer();
        Boolean b = player.hasPlayedBefore();
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("settings.debug")) {
            b = false;
            player.setLevel(0);
            player.setHealth(20);
            player.setFoodLevel(20);
        }

        // Call the first join event.
        if (!b) {
            FirstJoinPlus.getInstance().getServer().getPluginManager().callEvent(new FirstJoinEvent(event));
            return;
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (Utilities.getUtilities().invincible.contains(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("other-messages.join-message.enabled") && event.getPlayer().hasPlayedBefore()) {
            String message = FirstJoinPlus.getInstance().getConfig().getString("other-messages.join-message.message");
            if (!message.equalsIgnoreCase("%none")) {
                event.setJoinMessage(Utilities.getUtilities().formatVariables(message, event.getPlayer()));
            } else {
                event.setJoinMessage(null);
            }
        }
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("other-messages.quit-message.enabled")) {
            String message = FirstJoinPlus.getInstance().getConfig().getString("other-messages.quit-message.message");
            if (!message.equalsIgnoreCase("%none")) {
                event.setQuitMessage(Utilities.getUtilities().formatVariables(message, event.getPlayer()));
            } else {
                event.setQuitMessage(null);
            }
        }
    }
    
    @EventHandler
    public void onKick(PlayerKickEvent event) {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("other-messages.kick-message.enabled")) {
            String message = FirstJoinPlus.getInstance().getConfig().getString("other-messages.kick-message.message");
            if (!message.equalsIgnoreCase("%none")) {
                event.setLeaveMessage(Utilities.getUtilities().formatVariables(message, event.getPlayer(), event.getReason()));
            } else {
                event.setLeaveMessage(null);
            }
        }
    }

}