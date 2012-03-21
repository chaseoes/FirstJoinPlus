package me.chaseoes.firstjoinplus;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.chaseoes.firstjoinplus.FirstJoinPlusPlayerListener;

public class FirstJoinPlus extends JavaPlugin {
	  
	  public FileConfiguration config;
	  public static final Logger log = Logger.getLogger("Minecraft");

	  public void onDisable() {
	      log.info("[FirstJoinPlus] version" + getDescription().getVersion() + " by chaseoes" + " has been disabled!");
	  }

	  public void onEnable() {
          log.info("[FirstJoinPlus] version " + getDescription().getVersion() + " by chaseoes" + " has been enabled!");
        
		  // Listener Stuff
		  PluginManager pm = getServer().getPluginManager();
          pm.registerEvents(new FirstJoinPlusPlayerListener(), this);
          
	      this.getConfig().options().copyDefaults(true);
	      saveConfig();
	      
          // Config
          config = getConfig();
        
	      try {
	          this.config.options().copyDefaults(true);
	          saveConfig();
	          Config.initialize(this.config, getDataFolder(), getLogger());
	      } 
	    
	      catch (Exception ex) {
	          getLogger().log(Level.SEVERE, "Could not load config!", ex);
	      } 
	      
	  }
}