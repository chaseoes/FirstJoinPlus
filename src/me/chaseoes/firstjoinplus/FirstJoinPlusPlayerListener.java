package me.chaseoes.firstjoinplus;

import java.util.List;
import java.util.logging.Logger;
import java.io.*;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import uk.org.whoami.geoip.GeoIPLookup;

public class FirstJoinPlusPlayerListener implements Listener {

	private final JavaPlugin plugin;
	public final Logger log = Logger.getLogger("Minecraft");
	public String number;
	public GeoIPLookup geoiptools;

	public FirstJoinPlusPlayerListener(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinLogin(PlayerJoinEvent event) {
		// Define our variables!
		Player player = event.getPlayer();

		// Update checking!
		if (plugin.getConfig().getBoolean("settings.updatecheck") && player.isOp() && needsUpdate()) {
			player.sendMessage("§b[FirstJoinPlus] Your version of FirstJoinPlus is out of date!");
			player.sendMessage("§b[FirstJoinPlus] Please update at:");
			player.sendMessage("§b[FirstJoinPlus] §dhttp://www.dev.bukkit.org/server-mods/firstjoinplus/");
		}

		// Debugging!
		if (debuggling()) {
			log("Debugging: A player joined the game.");
		}

		// Define the amount of players who have joined in total.
		try {
		File f = new File(plugin.getConfig().getString("settings.worldname") + "/players/");
		int count = 0;
		for (File file : f.listFiles()) {
			if (file.isFile()) {
				count++;
				number = "" + count;
			}
		}
		} catch (NullPointerException ex) {
			log.severe("[FirstJoinPlus] The 'worldname' option is incorrectly set or player.dat files can not be found!");
			log.severe("[FirstJoinPlus] Various aspects of the plugin may not work until this is fixed.");
			log.severe("[FirstJoinPlus] Please set it to the world new players spawn in (as defined in server.properties).");
		}

		// Check if it's their first join.
		File file = new File(plugin.getConfig().getString("settings.worldname") + "/players/" + player.getName() + ".dat");
		boolean exists = file.exists();
		if (!exists) {
			// Show the first join message.
			if (plugin.getConfig().getBoolean("settings.showfirstjoinmessage")) {
				String firstjoinmessage = format(plugin.getConfig().getString("messages.firstjoinmessage"), player);
				event.setJoinMessage(firstjoinmessage);
			}

			// Show the number of players who have joined in total.
			if (plugin.getConfig().getBoolean("settings.numberonfirstjoin")) {
				plugin.getServer().broadcastMessage(format(plugin.getConfig().getString("messages.numbermessage"), player));
			}

			// Give a player an item on their first join.
			if (plugin.getConfig().getBoolean("settings.itemonfirstjoin")) {
				if (debuggling()) {
					log("Debugging: Attempting to give a player an item...");
				}
				List<String> items = plugin.getConfig().getStringList("items");
				for (String itemStr : items) {
					PlayerInventory inventory = player.getInventory();
					Integer itemtogive = Integer
							.parseInt(itemStr.split("\\.")[0]);
					Integer amount = Integer.parseInt(itemStr.split("\\.")[1]);
					Byte data = Byte.parseByte(itemStr.split("\\.")[2]);
					ItemStack istack = new ItemStack(itemtogive, amount,
							(short) 0, (byte) data);
					inventory.addItem(istack);
					if (debuggling()) {
						log("Debugging: Gave the player the item.");
					}
				}

			}

			// Show the first join MOTD.
			if (plugin.getConfig().getBoolean("settings.showfirstjoinmotd")) {
				List<String> motd = plugin.getConfig().getStringList("motd");
				for (String motdStr : motd) {
					player.sendMessage(format(motdStr, player));
					if (debuggling()) {
						log("Debugging: Showing the first join MOTD.");
					}
				}
			}

			// Teleport the player to the first join spawnpoint.
			if (plugin.getConfig().getBoolean("settings.firstjoinspawning")) {
				if (debuggling()) {
					log("Debugging: First join spawning enabled - teleporting the player to the first join spawnpoint.");
				}
				final Player p = event.getPlayer();
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
					public void run() {
						teleportToFirstSpawn(p); // Delayed task to teleport them 2 ticks after any other plugins.
					}
				}, 2L);
				if (debuggling()) {
					log("Debugging: Successfully teleported.");
				}
			}

			// Show some fancy smoke!
			if (plugin.getConfig().getBoolean("settings.showfirstjoinsmoke")) {
				if (debuggling()) {
					log("Debugging: Someone is 'smokin on their join!");
				}
				for (int i = 0; i <= 18; i++)
					player.getWorld().playEffect(player.getLocation(), Effect.SMOKE, i);
			}
			
			// Run some commands!
			if (plugin.getConfig().getBoolean("settings.commandsonfirstjoin")) {
				if (debuggling()) {
					log("Debugging: Running some commands.");
				}
				List<String> commands = plugin.getConfig().getStringList("commands");
				for (String command : commands) {
					player.performCommand(command);
				}
			}

		} else {
			if (plugin.getConfig().getBoolean("settings.showjoinmessage")) {
				if (!onlyfirstjoin()) {
					event.setJoinMessage(format(plugin.getConfig().getString("messages.joinmessage"), player));
				}
			} else {
				event.setJoinMessage(null);
			}
			if (plugin.getConfig().getBoolean("settings.numberonjoin")) {
				Bukkit.getServer().broadcastMessage(format(plugin.getConfig().getString("messages.numbermessage"), player));
			}
		}
	}

	// Change quit message.
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!onlyfirstjoin()) {
			Player player = event.getPlayer();
			if (plugin.getConfig().getBoolean("settings.showleavemessage")) {
				event.setQuitMessage(format(plugin.getConfig().getString("messages.leavemessage"), player));
			} else {
				event.setQuitMessage(null);
			}
			if (debuggling()) {
				log("Debugging: A player quit the game.");
			}
		}
	}

	// Change kick message.
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		if (!onlyfirstjoin()) {
			Player player = event.getPlayer();
			if (plugin.getConfig().getBoolean("settings.showkickmessage")) {
				event.setLeaveMessage(format(plugin.getConfig().getString("messages.kickmessage"), player));
			} else {
				event.setLeaveMessage(null);
			}
			if (debuggling()) {
				log("Debugging: A player was kicked from the game.");
			}
		}
	}

	public void log(String l) {
		log.info("[FirstJoinPlus] " + l);
	}

	public boolean debuggling() {
		if (plugin.getConfig().getBoolean("settings.debug")) {
			return true;
		} else {
			return false;
		}
	}

	public boolean onlyfirstjoin() {
		if (plugin.getConfig().getBoolean("settings.onlyfirstjoin")) {
			return true;
		} else {
			return false;
		}
	}

	public void teleportToFirstSpawn(Player player) {
		int x = plugin.getConfig().getInt("spawn.x");
		int y = plugin.getConfig().getInt("spawn.y");
		int z = plugin.getConfig().getInt("spawn.z");
		float pitch = plugin.getConfig().getInt("spawn.pitch");
		float yaw = plugin.getConfig().getInt("spawn.yaw");
		player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
	}

	public boolean needsUpdate() {
		String pageurl = UpdateChecker.fetch("http://emeraldsmc.com/fjp/");
		if (pageurl.equalsIgnoreCase(plugin.getDescription().getVersion())) {
			return false;
		} else {
			return true;
		}
	}
	
	public String colorize(String s){
		if(s == null) return null;
		return s.replaceAll("&([l-o0-9a-f])", "\u00A7$1");
	}
	
	public String format(String string, Player player) {
		String format = string.replace("%name%", player.getDisplayName()).replace("%number%", number).replace("%city%", getCity(player)).replace("%country%", getCountry(player));
		return colorize(format);
	}
	
	public String getCity(Player player) {
        if (geoiptools != null) {
                return geoiptools.getLocation(player.getAddress().getAddress()).city;
        } else {
            return "unknown";
        }
    }

    public String getCountry(Player player) {
        if (geoiptools != null) {
                return geoiptools.getCountry(player.getAddress().getAddress()).getName();
        } else {
            return "unknown";
        }
    }

}