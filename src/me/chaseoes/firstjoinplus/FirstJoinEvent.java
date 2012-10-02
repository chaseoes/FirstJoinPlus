package me.chaseoes.firstjoinplus;

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
}
