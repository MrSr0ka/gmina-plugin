package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.TimeSkipEvent;

public class WorldEvents implements Listener {

    private final Main plugin;
    private final Wataha wataha;
    private final Soltys soltys;  // Nowa instancja klasy Sołtys

    public WorldEvents(Main plugin) {
        this.plugin = plugin;
        this.wataha = new Wataha(plugin);
        this.soltys = new Soltys(plugin);  // Inicjalizacja Sołtys
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        soltys.updateSoltysForPlayerJoin(event.getPlayer());  // Aktualizacja dla sołtysa przy wejściu gracza
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        soltys.handleSoltysQuit(event.getPlayer());  // Zarządzanie sołtysem przy wyjściu gracza
    }

    @EventHandler
    public void onTimeSkip(TimeSkipEvent event) {
        World world = event.getWorld();
        long time = world.getTime();

        if (time >= 0 && time < 1000) { // Początek nowego dnia
            soltys.decrementSoltysDays(); // Zmniejszenie dni kadencji sołtysa

            // Sprawdzanie szansy spawnowania watahy dla każdego gracza
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getWorld().equals(world)) {
                    wataha.attemptToSpawnWolves(player); // Przekazujemy gracza, szansa 6% na spawn watahy
                }
            }
        }
    }

}
