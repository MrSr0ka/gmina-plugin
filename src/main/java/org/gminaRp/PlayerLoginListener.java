package org.gminaRp;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class PlayerLoginListener implements Listener {

    private final Main plugin;

    public PlayerLoginListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getConfig();

        // Sprawdzamy, czy gracz ma zapisany kolor w pliku config
        if (config.contains("players." + player.getUniqueId() + ".chatcolor")) {
            String colorName = config.getString("players." + player.getUniqueId() + ".chatcolor");
            ChatColor color = ChatColor.valueOf(colorName);

            // Ustawiamy graczowi jego kolorowy nick
            player.setDisplayName(color + player.getName() + ChatColor.RESET);
        }
    }
}
