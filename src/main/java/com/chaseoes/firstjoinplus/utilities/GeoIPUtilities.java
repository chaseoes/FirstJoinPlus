package com.chaseoes.firstjoinplus.utilities;

import org.bukkit.entity.Player;

import com.chaseoes.firstjoinplus.FirstJoinPlus;

public class GeoIPUtilities {

    public static String getCountry(Player player) {
        if (FirstJoinPlus.getInstance().getGeoIPLookup() != null) {
            if (FirstJoinPlus.getInstance().getGeoIPLookup().getLocation(player.getAddress().getAddress()) != null) {
                return FirstJoinPlus.getInstance().getGeoIPLookup().getLocation(player.getAddress().getAddress()).countryName;
            }
            return "unknown";
        }
        return "N/A";
    }

    public static String getCity(Player player) {
        if (FirstJoinPlus.getInstance().getGeoIPLookup() != null) {
            if (FirstJoinPlus.getInstance().getGeoIPLookup().getLocation(player.getAddress().getAddress()) != null) {
                return FirstJoinPlus.getInstance().getGeoIPLookup().getLocation(player.getAddress().getAddress()).city;
            }
            return "unknown";
        }
        return "N/A";
    }

}
