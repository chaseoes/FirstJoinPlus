package me.chaseoes.firstjoinplus;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import me.chaseoes.firstjoinplus.FirstJoinPlusPlayerListener;

public class FirstJoinPlus extends JavaPlugin {

	public FileConfiguration config;
	public static final Logger log = Logger.getLogger("Minecraft");

	public void onDisable() {
		log.info("[FirstJoinPlus] version" + getDescription().getVersion()
				+ " by chaseoes" + " has been disabled!");
	}

	public void onEnable() {
		log.info("[FirstJoinPlus] version " + getDescription().getVersion()
				+ " by chaseoes" + " has been enabled!");

		// Listener Stuff
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new FirstJoinPlusPlayerListener(this), this);

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
	
    @Override
    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
        if (strings.length < 1) { 
            cs.sendMessage(ChatColor.RED + "Usage: /firstjoinplus reload");
            return true;
        }
        String option = strings[0];
        if(option.equalsIgnoreCase("reload")) {
            if(strings.length != 2) { // Check the number of arguments.
            }
            
        	this.reloadConfig();
        	cs.sendMessage(ChatColor.RED + "Sucessfully reloaded the FirstJoinPlus config!");
        	return true;
        }
        return true;
    }

}