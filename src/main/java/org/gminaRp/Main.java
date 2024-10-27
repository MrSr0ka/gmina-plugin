package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private HelloPlayer helloPlayer;
    private WorldEvents worldEvents;
    private GminaCommands gminaCommands;
    private PlayerLoginListener playerLoginListener;
    private Wataha wataha;
    private Soltys soltys;
    private CustomItems customItems;

    @Override
    public void onEnable() {
        getLogger().info("Plugin został włączony!");
        saveDefaultConfig();
        // Tworzenie instancji HelloPlayer
        helloPlayer = new HelloPlayer(this);
        worldEvents = new WorldEvents(this);
        soltys = new Soltys(this);
        gminaCommands = new GminaCommands(this, worldEvents, soltys, customItems);
        playerLoginListener = new PlayerLoginListener(this);
        wataha = new Wataha(this);



        // Rejestracja eventów
        Bukkit.getPluginManager().registerEvents(this, this); // Rejestruj Main
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin został wyłączony!");
    }

    // Obsługa eventu PlayerJoinEvent
    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        helloPlayer.handlePlayerJoin(event.getPlayer());  // Wywołanie funkcji z HelloPlayer
    }
}
