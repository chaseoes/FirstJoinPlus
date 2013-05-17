package main.java.me.chaseoes.firstjoinplus;

import main.java.me.chaseoes.firstjoinplus.utilities.Utilities;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstJoinEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public PlayerJoinEvent e;

    public FirstJoinEvent(PlayerJoinEvent event) {
        e = event;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return e.getPlayer();
    }

    public void setFirstJoinMessage(String message) {
        e.setJoinMessage(message);
    }
    
    public Location getLocation() {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.teleport")) {
            return Utilities.getUtilities().getFirstJoinLocation();
        }
        return e.getPlayer().getLocation();
    }
}
