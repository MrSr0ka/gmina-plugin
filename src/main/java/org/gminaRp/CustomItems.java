package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomItems extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Rejestracja eventów
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    // Metoda tworząca niestandardowy przedmiot
    public static ItemStack createGminnaSakwa() {
        // Tworzenie przedmiotu (barrel)
        ItemStack sakwa = new ItemStack(Material.BARREL);

        // Ustawienie nazwy przedmiotu
        ItemMeta meta = sakwa.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Gminna sakwa"); // Ustawia kolor i nazwę
            sakwa.setItemMeta(meta);
        }

        return sakwa;
    }

    // Metoda dająca "Gminną sakwę" graczowi
    public static void giveGminnaSakwa(Player player) {
        ItemStack sakwa = createGminnaSakwa();

        // Dodaj przedmiot do ekwipunku gracza
        player.getInventory().addItem(sakwa);
    }

    // Metoda otwierająca zawartość "Gminnej sakwy"
    private Inventory createGminnaSakwaInventory() {
        Inventory inventory = Bukkit.createInventory(null, 27, "Gminna sakwa"); // 27 slotów
        return inventory;
    }

    // Listener do obsługi interakcji z "Gminną sakwą"
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        // Sprawdzanie, czy przedmiot w ręce to "Gminna sakwa"
        if (event.getAction().toString().contains("RIGHT_CLICK") && itemInHand != null && itemInHand.getType() == Material.BARREL
                && itemInHand.hasItemMeta() && itemInHand.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Gminna sakwa")) {

            // Otwieranie ekwipunku sakwy
            player.openInventory(createGminnaSakwaInventory());

            // Anuluj inne interakcje (np. otwieranie drzwi)
            event.setCancelled(true);
        }
    }
}
