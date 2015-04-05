package net.mineyourmind.wither;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Slind on 06.08.2014.
 */
public class Wither extends JavaPlugin implements Listener {

    private Set<String> allowedWorlds;
    private String restrictionMessage;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents( this, this );
        loadConfig();
        getLogger().info("Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled.");
    }

    public void loadConfig() {
        saveDefaultConfig();
        reloadConfig();
        FileConfiguration config = getConfig();
        allowedWorlds = new HashSet<String>(config.getStringList("allowedWorlds"));
        restrictionMessage = config.getString("restrictionMessage");
    }

    @EventHandler
    public void onBlockPlace(final BlockPlaceEvent e) {
        Block placedBlock = e.getBlockPlaced();
        if (placedBlock.getType().equals(Material.SKULL) && placedBlock.getRelative(BlockFace.DOWN).getType().equals(Material.SOUL_SAND)) {
            if (!allowedWorlds.contains(placedBlock.getWorld().getName())) {
                e.getPlayer().sendMessage(ChatColor.GOLD + "[MyM-Wither] " + ChatColor.RED + restrictionMessage);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onCreatureSpawnEvent(final CreatureSpawnEvent e) {
        if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) {
            final Location l = e.getLocation();
            final World w = l.getWorld();
            if (!allowedWorlds.contains(w.getName())) {
                for (Player p : w.getPlayers()) {
                    if (l.distance(p.getLocation()) < 20 ) {
                        p.sendMessage(ChatColor.GOLD + "[MyM-Wither] " + ChatColor.RED + restrictionMessage);
                    }
                }
                e.setCancelled(true);
                return;
            }
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (e.getLocation() != null) {
                        w.playEffect(e.getLocation(), Effect.SMOKE, 8);
                        w.createExplosion(e.getLocation(), 5);
                    }
                }
            }, 216L);
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    if (e.getLocation() != null) {
                        w.spawnEntity(e.getLocation(), EntityType.WITHER);
                        w.playSound(e.getLocation(), Sound.WITHER_SPAWN, 1, 1);
                        e.getEntity().remove();
                    }
                }
            }, 218L);
        }
    }

}
