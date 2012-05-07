package me.chaseoes.firstjoinplus;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.chaseoes.firstjoinplus.FirstJoinPlusPlayerListener;

public class FirstJoinPlus extends JavaPlugin {

	public FileConfiguration config;
	public static final Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		log.info("[FirstJoinPlus] Version" + getDescription().getVersion()
				+ " by chaseoes" + " has been disabled!");
	}

	public void onEnable() {
		log.info("[FirstJoinPlus] Version " + getDescription().getVersion()
				+ " by chaseoes" + " has been enabled!");

		// Listener Registration
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FirstJoinPlusPlayerListener(this), this);

		// Configuration
		config = getConfig();
		this.config.options().copyDefaults(true);
		saveConfig();
		try {
			this.config.options().copyDefaults(true);
			saveConfig();
			Config.initialize(this.config, getDataFolder(), getLogger());
		} catch (Exception ex) {
			getLogger().log(Level.SEVERE,
					"[FirstJoinPlus] Could not load configuration!", ex);
		}
	}

	// Commands
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string,
			String[] strings) {
		Player player = (Player) cs;
		if (cmnd.getName().equalsIgnoreCase("firstjoinplus")) {
			if (!cs.isOp()) {
				player.sendMessage(ChatColor.RED + "Sorry, " + cs.getName()
						+ ", you need to be an op to do that.");
				return true;
			}
			if (strings.length < 1) {
				cs.sendMessage(ChatColor.RED
						+ "Usage: /firstjoinplus <reload|setspawn|spawn>");
				return true;
			}
			if (strings[0].equalsIgnoreCase("reload")) {
				this.reloadConfig();
				this.saveConfig();
				cs.sendMessage(ChatColor.RED
						+ "Sucessfully reloaded the FirstJoinPlus config!");
				return true;
			}
			if (strings[0].equalsIgnoreCase("motd")) {

				return true;
			}
			if (strings[0].equalsIgnoreCase("setspawn")) {
				if (!this.getConfig().getBoolean("settings.firstjoinspawning")) {
					cs.sendMessage(ChatColor.GREEN
							+ "Please enable 'firstjoinspawning' in the FirstJoinPlus config to do this.");
					return true;
				}
				if (strings.length != 2) {
				}
				Player p = (Player) cs;
				int x = p.getLocation().getBlockX();
				int y = p.getLocation().getBlockY();
				int z = p.getLocation().getBlockZ();
				float pitch = p.getLocation().getPitch();
				float yaw = p.getLocation().getYaw();
				getConfig().set("spawn.x", x);
				getConfig().set("spawn.y", y);
				getConfig().set("spawn.z", z);
				getConfig().set("spawn.pitch", pitch);
				getConfig().set("spawn.yaw", yaw);
				saveConfig();
				cs.sendMessage(ChatColor.GREEN
						+ "Sucessfully set the FirstJoinPlus spawnpoint!");
				return true;
			}
			if (strings[0].equalsIgnoreCase("spawn")) {
				if (!this.getConfig().getBoolean("settings.firstjoinspawning")) {
					cs.sendMessage(ChatColor.GREEN
							+ "Please enable 'firstjoinspawning' in the FirstJoinPlus config to do this.");
					return true;
				}
				if (strings.length != 2) {
				}
				Player p = (Player) cs;
				int x = getConfig().getInt("spawn.x");
				int y = getConfig().getInt("spawn.y");
				int z = getConfig().getInt("spawn.z");
				float pitch = getConfig().getInt("spawn.pitch");
				float yaw = getConfig().getInt("spawn.yaw");
				p.teleport(new Location(p.getWorld(), x, y, z, yaw, pitch));
				cs.sendMessage(ChatColor.GREEN
						+ "Sucessfully teleported to the FirstJoinPlus spawnpoint!");
				return true;
			}
			return true;
		}
		return true;
	}

}