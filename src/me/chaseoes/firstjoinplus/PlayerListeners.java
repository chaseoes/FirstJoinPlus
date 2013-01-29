package me.chaseoes.firstjoinplus;

import me.chaseoes.firstjoinplus.utilities.Utilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void firstJoinDetection(PlayerJoinEvent event) {
        // Define our variables.
        Player player = event.getPlayer();
        Boolean b = player.hasPlayedBefore();
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("settings.debug")) {
            b = false;
        }

        // Call the first join event.
        if (!b) {
            FirstJoinPlus.getInstance().getServer().getPluginManager().callEvent(new FirstJoinEvent(event));
            return;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check for Updates!
        final Player player = event.getPlayer();
        if (player.hasPermission("firstjoinplus.notify-update") && FirstJoinPlus.getInstance().update.needsUpdate()) {
            FirstJoinPlus.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(FirstJoinPlus.getInstance(), new Runnable() {
                @Override
                public void run() {
                    FirstJoinPlus.getInstance().update.nagPlayer(player);
                }
            }, 100L);
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

}