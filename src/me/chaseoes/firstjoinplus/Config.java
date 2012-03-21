package me.chaseoes.firstjoinplus;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
  public static File dataDir;
  public static String worldname;
  public static String firstjoinmessage;
  public static String leavemessage;
  public static Boolean numberonjoin;
  public static String kickmessage;
  public static String numbermessage;
  public static String joinmessage;

  public static void initialize(FileConfiguration config, File pluginDir, Logger log)
  {
    try
    {
      dataDir = pluginDir;
      if (!dataDir.exists()) {
        dataDir.mkdir();
      }

      ConfigurationSection settings = config.getConfigurationSection("settings");
      ConfigurationSection messages = config.getConfigurationSection("messages");

      worldname = settings.getString("worldname");
      numberonjoin = settings.getBoolean("numeronjoin");
      
      firstjoinmessage = messages.getString("firstjoinmessage").replace("&", "§");
      joinmessage = messages.getString("joinmessage").replace("&", "§");
      leavemessage = messages.getString("leavemessage").replace("&", "§");
      kickmessage = messages.getString("kickmessage").replace("&", "§");
      numbermessage = messages.getString("numbermessage").replace("&", "§");
      
    } catch (Exception ex) {
      log.log(Level.SEVERE, "Unable to load config!", ex);
    }
  }
}