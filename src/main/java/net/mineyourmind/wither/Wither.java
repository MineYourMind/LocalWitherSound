package net.mineyourmind.wither;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Slind on 06.08.2014.
 */
public class Wither extends JavaPlugin implements Listener {

    private Boolean restrictWitherToNether;

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
        restrictWitherToNether = config.getBoolean("restrictWitherToNether");
    }

    @EventHandler
    public void onCreatureSpawnEvent(final CreatureSpawnEvent e) {
        if (e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.BUILD_WITHER)) {
            final Location l = e.getLocation();
            final World w = l.getWorld();
            if (!w.getEnvironment().equals(World.Environment.NETHER) && restrictWitherToNether ) {
                for (Player p : w.getPlayers()) {
                    if (l.distance(p.getLocation()) < 20 ) {
                        p.sendMessage(ChatColor.GOLD + "[MyM-Wither] " + ChatColor.RED + "Withers can only be spawned in the Nether!");
                    }
                }
                e.setCancelled(true);
                return;
            }
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    w.playEffect(l, Effect.SMOKE, 8);
                    w.createExplosion(l, 5);
                }
            }, 216L);
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    w.spawnEntity(l, EntityType.WITHER);
                    w.playSound(l, Sound.WITHER_SPAWN, 1, 1);
                }
            }, 218L);
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    e.getEntity().remove();
                }
            }, 220L);
        }
    }

}
