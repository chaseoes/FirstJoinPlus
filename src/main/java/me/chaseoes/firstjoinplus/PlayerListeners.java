package me.chaseoes.firstjoinplus;

import me.chaseoes.firstjoinplus.utilities.Utilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void firstJoinDetection(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        boolean existingPlayer = player.hasPlayedBefore();
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("settings.every-join-is-first-join")) {
            existingPlayer = false;
            Utilities.debugPlayer(player, false);
        }

        if (!existingPlayer) {
            FirstJoinPlus.getInstance().getServer().getPluginManager().callEvent(new FirstJoinEvent(event));
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (FirstJoinPlus.getInstance().godMode.contains(((Player) event.getEntity()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (FirstJoinPlus.getInstance().noPVP.contains(((Player) event.getEntity()).getName()) || FirstJoinPlus.getInstance().noPVP.contains(((Player) event.getDamager()).getName())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("other-messages.join-message.enabled")) {
            if (event.getPlayer().hasPlayedBefore() || (!FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.first-join-message.enabled") && !event.getPlayer().hasPlayedBefore())) {
                String message = FirstJoinPlus.getInstance().getConfig().getString("other-messages.join-message.message");
                if (!message.equalsIgnoreCase("%none")) {
                    event.setJoinMessage(Utilities.replaceVariables(message, event.getPlayer()));
                } else {
                    event.setJoinMessage(null);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("other-messages.quit-message.enabled")) {
            String message = FirstJoinPlus.getInstance().getConfig().getString("other-messages.quit-message.message");
            if (!message.equalsIgnoreCase("%none")) {
                event.setQuitMessage(Utilities.replaceVariables(message, event.getPlayer()));
            } else {
                event.setQuitMessage(null);
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("other-messages.kick-message.enabled")) {
            String message = FirstJoinPlus.getInstance().getConfig().getString("other-messages.kick-message.message");
            if (!message.equalsIgnoreCase("%none")) {
                event.setLeaveMessage(Utilities.replaceVariables(message, event.getPlayer(), event.getReason()));
            } else {
                event.setLeaveMessage(null);
            }
        }
    }

}
