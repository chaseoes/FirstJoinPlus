package me.chaseoes.firstjoinplus;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import me.chaseoes.firstjoinplus.FirstJoinPlusPlayerListener;

public class FirstJoinPlus extends JavaPlugin {

	public final Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		log.info("[FirstJoinPlus] Version" + getDescription().getVersion() + " by chaseoes" + " has been disabled!");
	}

	public void onEnable() {
		// Sorry TnT! Is verbose-logging really required for one line? ;)
		log.info("[FirstJoinPlus] Version " + getDescription().getVersion() + " by chaseoes" + " has been enabled!");

		// Listener Registration
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FirstJoinPlusPlayerListener(this), this);

		// Configuration
		try {
			getConfig().options().copyDefaults(true);
			getConfig()
					.options()
					.header("FirstJoinPlus v1.4 Configuration -- Please see https://github.com/chaseoes/FirstJoinPlus/wiki/Configuration #");
			getConfig().options().copyHeader(true);
			saveConfig();
		} catch (Exception ex) {
			getLogger().log(Level.SEVERE,
					"[FirstJoinPlus] Could not load configuration!", ex);
		}
	}

	// Commands
	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string,
			String[] strings) {
		if (cmnd.getName().equalsIgnoreCase("firstjoinplus")) {
			if (cs instanceof Player) {
				Player player = (Player) cs;
				if (!cs.isOp()) {
					player.sendMessage("§cSorry, " + player.getDisplayName()
							+ "§c, you need to be an op to do that.");
					return true;
				}
				if (strings.length < 1 || strings.length > 1) {
					cs.sendMessage("§cUsage: /firstjoinplus <reload|setspawn|spawn|items|motd>");
					return true;
				}
				if (strings[0].equalsIgnoreCase("reload")) {
					this.reloadConfig();
					this.saveConfig();
					cs.sendMessage("§aSucessfully reloaded the FirstJoinPlus config!");
					return true;
				}
				if (strings[0].equalsIgnoreCase("motd")) {
					String pc = player.getDisplayName();
					List<String> motd = this.getConfig().getStringList("motd");
					for (String motdStr : motd) {
						player.sendMessage(motdStr.replace("%name%", pc)
								.replace("&", "§"));
					}
					return true;
				}

				if (strings[0].equalsIgnoreCase("setspawn")) {
					if (!getConfig().getBoolean("settings.firstjoinspawning")) {
						cs.sendMessage("§cPlease enable 'firstjoinspawning' in the FirstJoinPlus config to do this.");
						return true;
					}
					int x = player.getLocation().getBlockX();
					int y = player.getLocation().getBlockY();
					int z = player.getLocation().getBlockZ();
					float pitch = player.getLocation().getPitch();
					float yaw = player.getLocation().getYaw();
					getConfig().set("spawn.x", x);
					getConfig().set("spawn.y", y);
					getConfig().set("spawn.z", z);
					getConfig().set("spawn.pitch", pitch);
					getConfig().set("spawn.yaw", yaw);
					saveConfig();
					cs.sendMessage("§aSucessfully set the FirstJoinPlus spawnpoint!");
					return true;
				}
				
				if (strings[0].equalsIgnoreCase("items")) {
					if (!getConfig().getBoolean("settings.itemonfirstjoin")) {
						cs.sendMessage("§cPlease enable 'itemonfirstjoin' in the FirstJoinPlus config to do that.");
						return true;
					}
					List<String> items = getConfig().getStringList("items");
					for (String itemStr : items) {
						PlayerInventory inventory = player.getInventory();
						Integer itemtogive = Integer
								.parseInt(itemStr.split("\\.")[0]);
						Integer amount = Integer.parseInt(itemStr.split("\\.")[1]);
						Byte data = Byte.parseByte(itemStr.split("\\.")[2]);
						ItemStack istack = new ItemStack(itemtogive, amount,
								(short) 0, (byte) data);
						inventory.addItem(istack);
					}
					cs.sendMessage("§aSucessfully gave you all items defined in the configuration.");
					return true;
				}

				if (strings[0].equalsIgnoreCase("spawn")) {
					if (!getConfig().getBoolean("settings.firstjoinspawning")) {
						player.sendMessage("§cPlease enable 'firstjoinspawning' in the FirstJoinPlus config to do this.");
						return true;
					}
					teleportToFirstSpawn(player);
					cs.sendMessage("§aSucessfully teleported to the FirstJoinPlus spawnpoint!");
					return true;
				}
			} else {
				cs.sendMessage("That command can only be used ingame.");
			}
			return true;
		}
		return true;
	}

	// Teleport player to the FirstJoinSpawnPoint.
	public void teleportToFirstSpawn(Player player) {
		int x = getConfig().getInt("spawn.x");
		int y = getConfig().getInt("spawn.y");
		int z = getConfig().getInt("spawn.z");
		float pitch = getConfig().getInt("spawn.pitch");
		float yaw = getConfig().getInt("spawn.yaw");
		player.teleport(new Location(player.getWorld(), x, y, z, yaw, pitch));
	}

}