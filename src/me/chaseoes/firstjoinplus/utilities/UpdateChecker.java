package me.chaseoes.firstjoinplus.utilities;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

import me.chaseoes.firstjoinplus.FirstJoinPlus;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UpdateChecker {

    private String latestVersion;

    public UpdateChecker() {
        latestVersion = getCurrentVersion();
    }
    
    public String getCurrentVersion() {
        return FirstJoinPlus.getInstance().getDescription().getVersion();
    }

    public boolean needsUpdate() {
        return !latestVersion.equalsIgnoreCase(getCurrentVersion());
    }

    public void nagPlayer(Player player) {
        player.sendMessage(ChatColor.DARK_GREEN + "[FirstJoinPlus]" + ChatColor.DARK_RED + " Version " + latestVersion + " is available! Please update ASAP.");
        player.sendMessage(ChatColor.RED + "http://dev.bukkit.org/server-mods/firstjoinplus/");
    }

    public void startTask() {
        FirstJoinPlus.getInstance().getServer().getScheduler().runTaskTimerAsynchronously(FirstJoinPlus.getInstance(), new Runnable() {
            @Override
            public void run() {
                checkForUpdate();
            }
        }, 0L, 12000L);
    }

    public void checkForUpdate() {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("settings.update-checking")) {
            try {
                String channel = "https://raw.github.com/chaseoes/FirstJoinPlus/master/version";
                final URL url = new URL(channel);
                InputStream i = url.openStream();
                Scanner scan = new Scanner(i);
                String ver = scan.nextLine();
                i.close();
                if (ver.equalsIgnoreCase("0.0")) {
                    latestVersion = getCurrentVersion();
                } else {
                    latestVersion = ver;
                }
                return;
            } catch (Exception e) {
                FirstJoinPlus.getInstance().getLogger().log(Level.WARNING, "An error was encountered while attempting to check for updates.");
            }
        }
        latestVersion = getCurrentVersion();
    }

}
