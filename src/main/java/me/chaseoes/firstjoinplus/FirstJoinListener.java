package me.chaseoes.firstjoinplus;

import java.util.ArrayList;
import java.util.List;

import me.chaseoes.firstjoinplus.utilities.Utilities;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FirstJoinListener implements Listener {

    private final JavaPlugin plugin;

    public FirstJoinListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFirstJoin(final FirstJoinEvent event) {

        // Variables!
        final Player player = event.getPlayer();

        // Show the first join message.
        if (plugin.getConfig().getBoolean("on-first-join.show-first-join-message")) {
            event.setFirstJoinMessage(Utilities.getUtilities().formatVariables(plugin.getConfig().getString("settings.first-join-message"), player));
        }

        // Give a player an item on their first join.
        if (plugin.getConfig().getBoolean("on-first-join.give-items.enabled")) {
            // Utilities.getUtilities().giveFirstJoinItems(player);
        }

        // Show the first join MOTD.
        if (plugin.getConfig().getBoolean("on-first-join.send-motd.enabled")) {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    for (String motdStr : plugin.getConfig().getStringList("on-first-join.send-motd.messages")) {
                        player.sendMessage(Utilities.getUtilities().formatVariables(motdStr, player));
                    }
                }
            }, plugin.getConfig().getLong("on-first'join.send-motd.delay"));
        }

        // Teleport the player to the first join spawnpoint.
        if (plugin.getConfig().getBoolean("on-first-join.teleport")) {
            final Location loc = event.getLocation();
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                @Override
                public void run() {
                    player.teleport(loc);
                    if (plugin.getConfig().getBoolean("on-first-join.show-smoke")) {
                        Utilities.getUtilities().playSmoke(loc);
                    }
                    
                    if (plugin.getConfig().getBoolean("on-first-join.launch-firework")) {
                        Utilities.getUtilities().playFirework(event.getLocation());
                    }
                }
            }, plugin.getConfig().getLong("settings.teleport-delay"));
        }

        // Show some fancy smoke!
        if (!plugin.getConfig().getBoolean("on-first-join.teleport")) {
            Utilities.getUtilities().playSmoke(event.getLocation());
        }
        
        // Launch a firework!
        if (plugin.getConfig().getBoolean("on-first-join.launch-firework") && !plugin.getConfig().getBoolean("on-first-join.teleport")) {
            Utilities.getUtilities().playFirework(event.getLocation());
        }

        // Play a sound!
        if (plugin.getConfig().getBoolean("on-first-join.play-notify-sound")) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                if (p.hasPermission("firstjoinplus.notify")) {
                    Sound s = Sound.valueOf(plugin.getConfig().getString("settings.notify-sound"));
                    p.playSound(p.getLocation(), s, 1, 1);
                }
            }
        }

        // Give some XP!
        if (plugin.getConfig().getInt("on-first-join.give-xp") != 0) {
            player.setLevel(plugin.getConfig().getInt("on-first-join.give-xp"));
        }

        // Run some commands!
        if (plugin.getConfig().getBoolean("on-first-join.run-commands.enabled")) {
            for (String command : plugin.getConfig().getStringList("on-first-join.run-commands.commands-to-run")) {
                String cmnd = Utilities.getUtilities().formatVariables(command, player);
                player.performCommand(cmnd);
            }
        }

        // Run some more commands!
        if (plugin.getConfig().getBoolean("on-first-join.run-console-commands.enabled")) {
            for (String command : plugin.getConfig().getStringList("on-first-join.run-console-commands.commands-to-run")) {
                String cmnd = Utilities.getUtilities().formatVariables(command, player);
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmnd);
            }
        }

        // Make the player invincible for x seconds.
        Integer invincible = plugin.getConfig().getInt("on-first-join.set-invincible");
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
        if (plugin.getConfig().getBoolean("on-first-join.give-written-books.enabled")) {
            Utilities.getUtilities().giveWrittenBooks(player);
        }
        
        // Apply potion effects!
        if (plugin.getConfig().getBoolean("on-first-join.apply-potion-effects.enabled")) {
            List<PotionEffect> effects = new ArrayList<PotionEffect>();
            for (String s : plugin.getConfig().getStringList("on-first-join.apply-potion-effects.effects")) {
                String[] effect = s.split("\\:");
                effects.add(new PotionEffect(PotionEffectType.getByName(effect[0].toUpperCase()), Integer.parseInt(effect[2]) * 20, (Integer.parseInt(effect[1])) - 1));
            }
            player.addPotionEffects(effects);
        }

    }

}
