package me.chaseoes.firstjoinplus;

import java.util.List;
import java.io.*;

import org.bukkit.Bukkit;
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

public class FirstJoinPlusPlayerListener implements Listener {

	private final JavaPlugin plugin;
	public String number;

	public FirstJoinPlusPlayerListener(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinLogin(PlayerJoinEvent event) {
		// Variables
		Player p = event.getPlayer();
		String name = p.getDisplayName();

		// Debugging!
		if (debuggling()) {
			log("Debugging: A player joined the game.");
		}

		// Define the amount of players who have joined in total.
		File f = new File(Config.worldname + "/players/");
		int count = 0;
		for (File file : f.listFiles()) {
			if (file.isFile()) {
				count++;
				number = "" + count;
			}
		}
		// Broadcast that amount.
		if (plugin.getConfig().getBoolean("settings.numberonjoin")) {
			Bukkit.getServer().broadcastMessage(
					Config.numbermessage.replace("%number%", number));
		}

		// Change join messages.
		File file = new File(Config.worldname + "/players/" + name + ".dat");
		boolean exists = file.exists();
		if (!exists) {
			if (plugin.getConfig().getBoolean("settings.showfirstjoinmessage")) {
				// Show the first join message.
				 event.setJoinMessage(Config.firstjoinmessage.replace("%name%",
				 name)
				 .replace("%number%", number));
					if (plugin.getConfig().getBoolean("settings.numberonfirstjoin")) {
						Bukkit.getServer().broadcastMessage(
								Config.numbermessage.replace("%number%", number));
					}
				// Give a player an item on their first join.
				if (plugin.getConfig().getBoolean("settings.itemonjoin")) {
					if (debuggling()) {
						log("Debugging: Attempting to give a player an item...");
					}
					List<String> items = plugin.getConfig().getStringList(
							"items");
					for (String itemStr : items) {
						PlayerInventory inventory = p.getInventory();
						Integer itemtogive = Integer.parseInt(itemStr
								.split("\\.")[0]);
						Integer amount = Integer
								.parseInt(itemStr.split("\\.")[1]);
						Byte data = Byte.parseByte(itemStr.split("\\.")[2]);
						ItemStack istack = new ItemStack(itemtogive, amount,
								(short) 0, (byte) data);
						inventory.addItem(istack);
						if (debuggling()) {
							log("Debugging: Gave the player the item.");
						}
					}

				}
				
				if (plugin.getConfig().getBoolean("settings.showfirstjoinmotd")) {
					List<String> motd = plugin.getConfig().getStringList(
							"motd");
					for (String motdStr : motd) {
						p.sendMessage(motdStr.replace("%name%", name).replace("&", "§"));
						if (debuggling()) {
							log("Showing the first join MOTD.");
						}
					}
				}

				// Teleport the player to the first join spawnpoint.
				if (plugin.getConfig().getBoolean("settings.firstjoinspawning")) {
					int x = plugin.getConfig().getInt("spawn.x");
					int y = plugin.getConfig().getInt("spawn.y");
					int z = plugin.getConfig().getInt("spawn.z");
					float pitch = plugin.getConfig().getInt("spawn.pitch");
					float yaw = plugin.getConfig().getInt("spawn.yaw");
					p.teleport(new Location(p.getWorld(), x, y, z, yaw, pitch));
				}

			} else {
				if (!onlyfirstjoin()) {
					event.setJoinMessage(null);
				}
			}
		} else {
			if (plugin.getConfig().getBoolean("settings.showjoinmessage")) {
				if (!onlyfirstjoin()) {
					event.setJoinMessage(Config.joinmessage.replace("%name%",
							name));
				}
			} else {
				// If the join message is disabled.
				if (!onlyfirstjoin()) {
					event.setJoinMessage(null);
				}
			}
		}

	}

	// Change quit message.
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!onlyfirstjoin()) {
			Player p = event.getPlayer();
			String name = p.getName();
			if (plugin.getConfig().getBoolean("settings.showleavemessage")) {
				String qmessage = Config.leavemessage.replace("%name%", name);
				event.setQuitMessage(qmessage);
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
			Player p = event.getPlayer();
			String name = p.getName();
			if (plugin.getConfig().getBoolean("settings.showkickmessage")) {
				event.setLeaveMessage(Config.kickmessage
						.replace("%name%", name));
			} else {
				event.setLeaveMessage(null);
			}
			if (debuggling()) {
				log("Debugging: A player was kicked from the game.");
			}
		}
	}

	public void log(String l) {
		System.out.println("[FirstJoinPlus] " + l);
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

}