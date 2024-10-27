package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class HelloPlayer {

    private final Main plugin;

    public HelloPlayer(Main plugin) {
        this.plugin = plugin;
    }

    public void handlePlayerJoin(Player player) {
        // Ustaw niestandardowy komunikat o dołączeniu
        String customJoinMessage = ChatColor.GREEN + "[GminaRP] " + ChatColor.AQUA + player.getName() + ChatColor.GREEN + " dołączył do Gminy!";
        Bukkit.broadcastMessage(customJoinMessage); // Wyświetlenie komunikatu dla wszystkich graczy

        // Oblicz czas trwania świata w dniach
        long worldTime = player.getWorld().getGameTime(); // czas w tickach
        long daysInWorld = worldTime / 24000; // 1 dzień = 24000 ticków

        // Ustaw stały kolor dla tytułu i podtytułu
        player.sendTitle(
                ChatColor.GREEN + " " + daysInWorld + " dzień", // Tytuł
                ChatColor.BLUE + "Witaj ponownie " + player.getName() + " !", // Podtytuł
                10, // fadeIn
                140, // stay
                10 // fadeOut
        );


        // Odtwórz dźwięki
        player.playSound(player.getLocation(), Sound.ENTITY_HORSE_AMBIENT, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_YES, 1.0f, 1.0f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 1.0f, 1.0f);

        // Odtwórz Pigstep dla wszystkich graczy na serwerze
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.MUSIC_DISC_PIGSTEP, 1.0f, 1.0f);
        }

        // Zatrzymanie Pigstep po 7 sekundach (140 ticków)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.stopSound(Sound.MUSIC_DISC_PIGSTEP);
            }
        }, 140L);

        // Efekty cząsteczek jak przy wybuchu fajerwerków
        Location location = player.getLocation();
        for (int i = 0; i < 50; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            player.getWorld().spawnParticle(Particle.FIREWORK, location.clone().add(offsetX, offsetY, offsetZ), 0);
        }
    }
}
