package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class Soltys {

    private final Main plugin;
    private Player currentSoltys;
    private long remainingDaysUntilNewSoltys = 7;
    private BukkitRunnable hologramTask;

    private ScoreboardManager scoreboardManager;
    private Scoreboard scoreboard;
    private Objective objective;

    public Soltys(Main plugin) {
        this.plugin = plugin;

        // Inicjalizacja scoreboardu
        scoreboardManager = Bukkit.getScoreboardManager();
        scoreboard = scoreboardManager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("soltysInfo", "dummy", ChatColor.GOLD + "Sołtys");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        // Wczytanie aktualnego sołtysa z configu
        loadCurrentSoltys();
    }

    private void loadCurrentSoltys() {
        FileConfiguration config = plugin.getConfig();
        String soltysUUIDString = config.getString("sołtys");

        if (soltysUUIDString != null) {
            UUID soltysUUID = UUID.fromString(soltysUUIDString);
            Optional<Player> optionalPlayer = (Optional<Player>) Bukkit.getOnlinePlayers().stream()
                    .filter(p -> p.getUniqueId().equals(soltysUUID))
                    .findFirst();

            if (optionalPlayer.isPresent()) {
                currentSoltys = optionalPlayer.get();
                currentSoltys.setPlayerListName(ChatColor.GOLD + "Sołtys " + currentSoltys.getName());
                displayHologram(currentSoltys);
                updateScoreboard();
            } else {
                chooseRandomSoltys();
            }
        } else {
            chooseRandomSoltys();
        }
    }

    public void chooseRandomSoltys() {
        List<Player> onlinePlayers = Bukkit.getOnlinePlayers().stream().collect(Collectors.toList());

        if (!onlinePlayers.isEmpty()) {
            Random random = new Random();
            Player randomPlayer = onlinePlayers.get(random.nextInt(onlinePlayers.size()));
            setSoltys(randomPlayer);
            Bukkit.broadcastMessage(ChatColor.GREEN + "[GminaRP] " + ChatColor.YELLOW + randomPlayer.getName() + " został nowym Sołtysem!");

            remainingDaysUntilNewSoltys = 7; // Resetuj na 7 dni
            updateScoreboard();
            updateSoltysUI();
        }
    }

    public void setSoltys(Player player) {
        if (currentSoltys != null) {
            currentSoltys.setPlayerListName(currentSoltys.getName());
            removeHologram();
        }

        currentSoltys = player;
        player.setPlayerListName(ChatColor.GOLD + "Sołtys " + player.getName());

        FileConfiguration config = plugin.getConfig();
        config.set("sołtys", player.getUniqueId().toString());
        plugin.saveConfig();

        displayHologram(player);
        player.sendTitle(ChatColor.GOLD + "Sołtys", "", 10, 70, 20);
        updateScoreboard();
    }

    private void displayHologram(Player player) {
        hologramTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    Location loc = player.getLocation().add(0, 2, 0);
                    player.getWorld().spawnParticle(Particle.GLOW, loc, 1, 0, 0, 0);
                } else {
                    cancel();
                }
            }
        };
        hologramTask.runTaskTimer(plugin, 0L, 5L); // Aktualizacja hologramu co 5 ticków
    }

    private void removeHologram() {
        if (hologramTask != null) {
            hologramTask.cancel(); // Anuluj zadanie
            hologramTask = null;
        }
    }

    public void updateSoltysForPlayerJoin(Player player) {
        if (currentSoltys == null) {
            chooseRandomSoltys();
        } else {
            if (player.getUniqueId().equals(currentSoltys.getUniqueId())) {
                player.setPlayerListName(ChatColor.GOLD + "Sołtys " + currentSoltys.getName());
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        displayHologram(player);
                    }
                }.runTaskLater(plugin, 20L);
            }
            updateScoreboard();
        }

        player.setScoreboard(scoreboard);
    }

    public void handleSoltysQuit(Player player) {
        if (currentSoltys != null && player.getUniqueId().equals(currentSoltys.getUniqueId())) {
            removeHologram();
        }
    }

    public void decrementSoltysDays() {
        if (remainingDaysUntilNewSoltys > 0) {
            remainingDaysUntilNewSoltys--; // Zmniejszaj czas kadencji co nowy dzień
            updateScoreboard();
        } else {
            chooseRandomSoltys(); // Wybierz nowego sołtysa, gdy czas się skończy
        }
    }

    public boolean retireSoltys(Player player) {
        if (currentSoltys != null && currentSoltys.getUniqueId().equals(player.getUniqueId())) {
            Bukkit.broadcastMessage(ChatColor.GREEN + "[GminaRP] " + ChatColor.YELLOW + player.getName() + " zrezygnował z bycia Sołtysem.");
            remainingDaysUntilNewSoltys = 7; // Resetuj pozostały czas do nowego losowania
            chooseRandomSoltys();
            return true;
        } else {
            player.sendMessage(ChatColor.RED + "Nie jesteś sołtysem, nie możesz zrezygnować.");
            return false;
        }
    }

    private void updateScoreboard() {
        if (currentSoltys != null) {
            scoreboard.getEntries().forEach(scoreboard::resetScores); // Resetuj wcześniejsze wyniki

            String remainingTimeMessage = ChatColor.GOLD + "Dni do końca kadencji:"; // Konwersja long na String
            objective.setDisplayName(ChatColor.GOLD + "Sołtys: " + ChatColor.WHITE + currentSoltys.getName());
            objective.getScore(remainingTimeMessage).setScore((int) remainingDaysUntilNewSoltys);  // Ustaw punkt na 0

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setScoreboard(scoreboard); // Ustaw scoreboard dla każdego gracza
            }
        }
    }

    private void updateSoltysUI() {
        if (currentSoltys != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                String soltysName = currentSoltys.getName();
                String remainingTimeMessage = "Koniec kadencji: " + remainingDaysUntilNewSoltys + " dni";
                player.sendTitle(ChatColor.GOLD + "Sołtys: " + soltysName, remainingTimeMessage, 10, 70, 20);
            }
        }
    }
}

