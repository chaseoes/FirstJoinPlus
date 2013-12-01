package com.chaseoes.firstjoinplus;


import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

import com.chaseoes.firstjoinplus.utilities.Utilities;

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

    public Location getFirstJoinLocation() {
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.teleport.enabled")) {
            return Utilities.getFirstJoinLocation();
        }
        return e.getPlayer().getLocation();
    }
}
