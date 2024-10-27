package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.HashMap;
import java.util.Random;

public class Wataha implements Listener {

    private final Main plugin;
    private final HashMap<Player, Integer> angryWolvesCount = new HashMap<>();
    private final Random random = new Random();

    public Wataha(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void attemptToSpawnWolves(Player player) {
        // 6% szansy na pojawienie się wilków
        if (random.nextDouble() <= 0.10) {
            int wolfCount = getRandomWolfCount(); // Losowanie ilości wilków
            spawnAngryWolvesNearPlayer(player, wolfCount);
        }
    }

    public void spawnAngryWolvesNearPlayer(Player player, int wolfCount) {
        if (player == null || wolfCount <= 0) return; // Sprawdzamy poprawność

        World world = player.getWorld();
        Location playerLocation = player.getLocation();

        for (int i = 0; i < wolfCount; i++) {
            double distance = 10 + (random.nextDouble() * 10);
            double angle = random.nextDouble() * 2 * Math.PI;
            double offsetX = distance * Math.cos(angle);
            double offsetZ = distance * Math.sin(angle);
            Location spawnLocation = playerLocation.clone().add(offsetX, 0, offsetZ);
            spawnLocation.setY(world.getHighestBlockYAt(spawnLocation));

            Wolf wolf = (Wolf) world.spawnEntity(spawnLocation, EntityType.WOLF);
            wolf.setAngry(true);
            wolf.setTarget(player);
            wolf.setCustomName("Wataha");
            wolf.setCustomNameVisible(true);
        }

        angryWolvesCount.put(player, wolfCount);
        Bukkit.broadcastMessage(ChatColor.GREEN + "[GminaRP] " + ChatColor.YELLOW + player.getName() + " został zaatakowany przez watahę!");
    }

    @EventHandler
    public void onWolfDeath(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.WOLF) {
            Wolf wolf = (Wolf) event.getEntity();
            Player player = (Player) wolf.getTarget();

            if (angryWolvesCount.containsKey(player)) {
                int remainingWolves = angryWolvesCount.get(player) - 1;

                if (remainingWolves <= 0) {
                    angryWolvesCount.remove(player);
                    Bukkit.broadcastMessage(ChatColor.GREEN + "[GminaRP] " + ChatColor.YELLOW + player.getName() + " odpędził watahę!");
                } else {
                    angryWolvesCount.put(player, remainingWolves);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Sprawdź, czy gracz miał wilki
        if (angryWolvesCount.containsKey(player)) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "[GminaRP] " + ChatColor.RED + "Wilki zabiły " + ChatColor.WHITE + player.getName() + " i odeszły!");

            // Usuwamy wilki, które mają nazwę "Wataha"
            removeAngryWolves(player);

            // Wykonaj komendę do zabicia wilków
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[type=wolf,name=Wataha]");

            // Usunięcie gracza z mapy
            angryWolvesCount.remove(player);
        }
    }

    private void removeAngryWolves(Player player) {
        for (Wolf wolf : player.getWorld().getEntitiesByClass(Wolf.class)) {
            if (wolf.getCustomName() != null && wolf.getCustomName().equals("Wataha") && wolf.getTarget() != null && wolf.getTarget().equals(player)) {
                wolf.remove(); // Usuwamy wilka
            }
        }
    }

    public int getRandomWolfCount() {
        return random.nextInt(4) + 2; // Zwraca losową liczbę od 2 do 5
    }
}
