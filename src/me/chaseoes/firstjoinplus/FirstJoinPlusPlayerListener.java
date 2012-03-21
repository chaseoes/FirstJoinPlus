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
import org.bukkit.plugin.java.JavaPlugin;
public class FirstJoinPlusPlayerListener extends JavaPlugin implements Listener {

	    @EventHandler(priority = EventPriority.HIGHEST)
	    public void onJoinLogin(PlayerJoinEvent event) {
	        System.out.println("Debugging: Player Joined!");
	    	
	        if (this.getConfig().getBoolean("numberonjoin")) {
                File f = new File(Config.worldname + "/players/");
                int count = 0;
                for (File file : f.listFiles()) {
                	if (file.isFile()) {
                		count++;
                    }
                }
                // Convert count to a string.
                String number = "" + count;
                String number1 = Config.numbermessage.replace("%number%", number);
                Bukkit.getServer().broadcastMessage(number1);
                System.out.println("Debugging: numberonjoin is true!");
            } 
            else {
            	System.out.println("Debugging: numberonjoin is false!");
            }
            
	        Player p = event.getPlayer();
	        String name = p.getName();
	        String firstjmessage = Config.firstjoinmessage.replace("%name%", name);
	        String jmessage = Config.joinmessage.replace("%name%", name);
	        File file = new File(Config.worldname + "/players/" + name + ".dat");
	        boolean exists = file.exists();
	        // Change message on first join.
	        if (!exists) {
	            event.setJoinMessage(firstjmessage);
	        } 
	        // Change message on join.
	        else {
	            event.setJoinMessage(jmessage); 
	        }
	        
		}

	    // Change quit message.
	    @EventHandler
	    public void onPlayerQuit(PlayerQuitEvent event) {
	        Player p = event.getPlayer();
	        String name = p.getName();
	    	String qmessage = Config.leavemessage.replace("%name%", name);
	        event.setQuitMessage(qmessage);
	    }
	    
	    // Change kick message.
	    @EventHandler
	    public void onPlayerKick(PlayerKickEvent event) {
	        Player p = event.getPlayer();
	        String name = p.getName();
	        String kmessage = Config.kickmessage.replace("%name%", name);
	        event.setLeaveMessage(kmessage);
	    }

}