package me.chaseoes.firstjoinplus;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
	public static File dataDir;
	public static String worldname;
	public static Boolean debug;
	public static Boolean numberonjoin;
	public static Boolean itemonjoin;
	public static Boolean showfirstjoinmessage;
	public static Boolean showjoinmessage;
	public static Boolean showleavemessage;
	public static Boolean showkickmessage;
	
	public static String firstjoinmessage;
	public static String joinmessage;
	public static String leavemessage;
	public static String kickmessage;
	public static String numbermessage;
	
	public static Integer item;
	public static Integer amount;
	public static Integer data;
	
	public static Integer x;
	public static Integer y;
	public static Integer z;
	public static Integer pitch;
	public static Integer yaw;

	public static void initialize(FileConfiguration config, File pluginDir,
			Logger log) {
		try {
			dataDir = pluginDir;
			if (!dataDir.exists()) {
				dataDir.mkdir();
			}

			ConfigurationSection settings = config.getConfigurationSection("settings");
			ConfigurationSection messages = config.getConfigurationSection("messages");
			ConfigurationSection items = config.getConfigurationSection("items");
			ConfigurationSection spawn = config.getConfigurationSection("spawn");

			// Settings Section
			worldname = settings.getString("worldname");
			debug = settings.getBoolean("debug");
			numberonjoin = settings.getBoolean("numeronjoin");
			itemonjoin = settings.getBoolean("itemonjoin");
			showjoinmessage = settings.getBoolean("showfirstjoinmessage");
			showjoinmessage = settings.getBoolean("showjoinmessage");
			showleavemessage = settings.getBoolean("showleavemessage");
			showkickmessage = settings.getBoolean("showkickmessage");

			// Messages Section
			firstjoinmessage = messages.getString("firstjoinmessage").replace("&", "§");
			joinmessage = messages.getString("joinmessage").replace("&", "§");
			leavemessage = messages.getString("leavemessage").replace("&", "§");
			kickmessage = messages.getString("kickmessage").replace("&", "§");
			numbermessage = messages.getString("numbermessage").replace("&","§");
			
			// Items Section
			item = items.getInt("item");
			amount = items.getInt("amount");
			data = items.getInt("data");
			
			// Spawn Section
			x = spawn.getInt("x");
			y = spawn.getInt("y");
			z = spawn.getInt("z");
			pitch = spawn.getInt("pitch");
			yaw = spawn.getInt("yaw");

		} catch (Exception ex) {
			log.log(Level.SEVERE, "Unable to load config!", ex);
		}
	}
}