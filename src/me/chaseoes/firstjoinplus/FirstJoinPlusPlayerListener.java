package me.chaseoes.firstjoinplus;

import java.io.*;
import org.bukkit.Bukkit;
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

	public FirstJoinPlusPlayerListener(final JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinLogin(PlayerJoinEvent event) {

		if (plugin.getConfig().getBoolean("settings.debug")) {
			System.out
					.println("[FirstJoinPlus] Debugging: A player joined the game.");
		}
		if (plugin.getConfig().getBoolean("settings.numberonjoin")) {
			File f = new File(Config.worldname + "/players/");
			int count = 0;
			for (File file : f.listFiles()) {
				if (file.isFile()) {
					count++;
				}
			}
			String number = "" + count;
			String number1 = Config.numbermessage.replace("%number%", number);
			Bukkit.getServer().broadcastMessage(number1);
		}

		Player p = event.getPlayer();
		String name = p.getName();
		String firstjmessage = Config.firstjoinmessage.replace("%name%", name);
		String jmessage = Config.joinmessage.replace("%name%", name);

		// Change join messages.
		File file = new File(Config.worldname + "/players/" + name + ".dat");
		boolean exists = file.exists();
		if (!exists) {
			if (plugin.getConfig().getBoolean("settings.showfirstjoinmessage")) {
				event.setJoinMessage(firstjmessage);
				if (plugin.getConfig().getBoolean("settings.itemonjoin")) {
					PlayerInventory inventory = p.getInventory();
		            Integer itemtogive = Config.item;
		            Integer amount = Config.amount;
		            int data = Config.data;
		            ItemStack istack = new ItemStack(itemtogive, amount, (short) 0, (byte) data);
		            inventory.addItem(istack);
				}
			}
			else {
				event.setJoinMessage("");
			}
		} else {
			if (plugin.getConfig().getBoolean("settings.showjoinmessage")) {
				event.setJoinMessage(jmessage);
			}
			else {
				event.setJoinMessage("");
			}
		}

	}

	// Change quit message.
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		String name = p.getName();
		if (plugin.getConfig().getBoolean("settings.showleavemessage")) {
			String qmessage = Config.leavemessage.replace("%name%", name);
			event.setQuitMessage(qmessage);
		} else {
			event.setQuitMessage("");
		}
		if (plugin.getConfig().getBoolean("settings.debug")) {
			System.out
					.println("[FirstJoinPlus] Debugging: A player quit the game.");
		}
	}

	// Change kick message.
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();
		String name = p.getName();
		if (plugin.getConfig().getBoolean("settings.showkickmessage")) {
			String kmessage = Config.kickmessage.replace("%name%", name);
			event.setLeaveMessage(kmessage);
		}
		else {
			event.setLeaveMessage("");
		}
		if (plugin.getConfig().getBoolean("settings.debug")) {
			System.out
					.println("[FirstJoinPlus] Debugging: A player was kicked from the game.");
		}
	}

}