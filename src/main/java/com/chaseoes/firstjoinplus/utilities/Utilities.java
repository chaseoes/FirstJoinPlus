package com.chaseoes.firstjoinplus.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;


import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.FireworkMeta;

import com.chaseoes.firstjoinplus.FirstJoinEvent;
import com.chaseoes.firstjoinplus.FirstJoinPlus;

public class Utilities {

    public static int getTotalPlayerCount() {
        return FirstJoinPlus.getInstance().getServer().getOfflinePlayers().length;
    }

    public static Location getFirstJoinLocation() {
        World world = FirstJoinPlus.getInstance().getServer().getWorld(FirstJoinPlus.getInstance().getConfig().getString("on-first-join.teleport.world"));
        int x = FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.teleport.x");
        int y = FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.teleport.y");
        int z = FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.teleport.z");
        float yaw = FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.teleport.yaw");
        float pitch = FirstJoinPlus.getInstance().getConfig().getInt("on-first-join.teleport.pitch");
        return new Location(world, x + 0.5, y, z + 0.5, yaw, pitch);
    }

    public static List<ItemStack> getFirstJoinKit() {
        List<ItemStack> kit = new ArrayList<ItemStack>();
        for (String s : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.first-join-kit.items")) {
            String[] item = s.split("\\:");

            Material mat = Material.AIR;
            try {
                mat = Material.getMaterial(item[0].toUpperCase());
            } catch (Exception e) {
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "Error encountered while attempting to give a new player the first join kit. Unknown item name: " + item[0]);
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "Find and double check item names using this page:");
                FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
            }

            int amount = 1;
            if (item.length > 1) {
                if (item[1] != null && NumberUtils.isDigits(item[1])) {
                    amount = Integer.parseInt(item[1]);
                } else {
                    FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "Error encountered while attempting to give a new player the first join kit.");
                    FirstJoinPlus.getInstance().getLogger().log(Level.SEVERE, "The item amount must be a number: " + item[1]);
                }
            }

            ItemStack is = new ItemStack(mat, amount);
            kit.add(is);
        }
        return kit;
    }

    public static List<ItemStack> getWrittenBooks(Player viewingPlayer) {
        List<ItemStack> books = new ArrayList<ItemStack>();
        for (String file : FirstJoinPlus.getInstance().getConfig().getStringList("on-first-join.give-written-books.book-files")) {
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta) book.getItemMeta();
            File f = new File(FirstJoinPlus.getInstance().getDataFolder() + "/" + file);
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));
                StringBuilder sb = new StringBuilder();
                String line = br.readLine();
                int i = 0;

                while (line != null) {
                    i++;
                    line = replaceVariables(line, viewingPlayer);
                    if (i != 1 && i != 2) {
                        if (line.equalsIgnoreCase("/newpage") || line.equalsIgnoreCase("/np")) {
                            bm.addPage(sb.toString());
                            sb = new StringBuilder();
                        } else {
                            sb.append(translateColors(line));
                            sb.append("\n");
                        }
                    } else {
                        if (i == 1) {
                            bm.setTitle(translateColors(line));
                        }
                        if (i == 2) {
                            bm.setAuthor(translateColors(line));
                        }
                    }
                    line = br.readLine();
                }

                br.close();
                bm.addPage(sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                FirstJoinPlus.getInstance().getLogger().log(Level.WARNING, "Error encountered while trying to give a new player a written book! (File: " + file + ")");
                FirstJoinPlus.getInstance().getLogger().log(Level.WARNING, "Please check that the file exists and is readable.");
            }

            book.setItemMeta(bm);
            books.add(book);
        }
        return books;
    }

    public static void launchRandomFirework(Location location) {
        Random random = new Random();
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = fw.getFireworkMeta();

        meta.setPower(1 + random.nextInt(4));
        FireworkEffect.Builder builder = FireworkEffect.builder().
                trail(random.nextBoolean()).
                flicker(random.nextBoolean());

        builder.with(FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)]);
        Set<Color> colors = new HashSet<Color>();
        for (int i = 0; i < 3; i++) {
            colors.add(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }

        builder.withColor(colors);
        meta.addEffect(builder.build());
        fw.setFireworkMeta(meta);
    }

    public static Player getRandomPlayer() {
        return (Player) FirstJoinPlus.getInstance().getServer().getOnlinePlayers().toArray()[new Random().nextInt(FirstJoinPlus.getInstance().getServer().getOnlinePlayers().size())]; 
    }

    public static String replaceVariables(String string, Player player) {
        string = string.replace("%player_name", player.getName());
        string = string.replace("%player_display_name", player.getDisplayName());
        string = string.replace("%player_uuid", player.getUniqueId().toString());
        string = string.replace("%player_country", GeoIPUtilities.getCountry(player));
        string = string.replace("%player_city", GeoIPUtilities.getCity(player));
        string = string.replace("%total_players", getTotalPlayerCount() + "");
        string = string.replace("%random_player", getRandomPlayer().getName());
        string = string.replace("%random_player_display_name", getRandomPlayer().getDisplayName());
        string = string.replace("%new_line", "\n");
        return translateColors(string);
    }

    public static String replaceVariables(String string, Player player, String reason) {
        return replaceVariables(string.replace("%reason", reason), player);
    }

    public static String translateColors(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String formatCommandResponse(String string) {
        return ChatColor.YELLOW + "[FJP] " + ChatColor.GRAY + string;
    }

    public static String getNoPermissionMessage() {
        return formatCommandResponse(ChatColor.RED + "You don't have permission to do that.");
    }

    public static void copyDefaultFiles() {
        FirstJoinPlus.getInstance().getConfig().options().header("FirstJoinPlus Version " + FirstJoinPlus.getInstance().getDescription().getVersion() + " Configuration -- Configuration Help: http://dev.bukkit.org/bukkit-plugins/firstjoinplus/ #");
        FirstJoinPlus.getInstance().getConfig().options().copyDefaults(true);
        FirstJoinPlus.getInstance().getConfig().options().copyHeader(true);
        FirstJoinPlus.getInstance().saveConfig();

        String[] files = new String[] { };
        if (FirstJoinPlus.getInstance().getConfig().getBoolean("on-first-join.give-written-books.enabled")) {
            files = new String[] { "rules.txt" };
        }

        for (String resourcePath : files) {
            if (resourcePath == null || resourcePath.equals("")) {
                return;
            }

            resourcePath = resourcePath.replace('\\', '/');
            InputStream in = FirstJoinPlus.getInstance().getResource(resourcePath);
            if (in == null) {
                return;
            }

            File outFile = new File(FirstJoinPlus.getInstance().getDataFolder(), resourcePath);
            int lastIndex = resourcePath.lastIndexOf('/');
            File outDir = new File(FirstJoinPlus.getInstance().getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

            if (!outDir.exists()) {
                outDir.mkdirs();
            }

            try {
                if (!outFile.exists()) {
                    OutputStream out = new FileOutputStream(outFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.close();
                    in.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    public static void debugPlayer(Player player, boolean b) {
        player.getInventory().clear();
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);
        player.setHealth(player.getMaxHealth());
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setExhaustion(0);
        player.setExp(0);
        player.setFoodLevel(20);
        player.setLevel(0);
        player.setSaturation(0);
        player.closeInventory();
        player.setGameMode(FirstJoinPlus.getInstance().getServer().getDefaultGameMode());

        if (b) {
            FirstJoinPlus.getInstance().getServer().getPluginManager().callEvent(new FirstJoinEvent(new PlayerJoinEvent(player, player.getName() + " joined for the first time!")));
        }
    }

}
