package org.gminaRp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GminaCommands implements CommandExecutor {

    private final Main plugin;
    private final WorldEvents worldEvents; // Reference to WorldEvents
    private final Soltys soltys;
    private final CustomItems customItems;

    public GminaCommands(Main plugin, WorldEvents worldEvents, Soltys soltys, CustomItems customItems) {
        this.plugin = plugin;
        this.worldEvents = worldEvents;
        this.soltys = soltys;
        this.customItems = customItems;
        plugin.getCommand("gmina").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda może być wykonana tylko przez gracza!");
            return true;
        }

        Player player = (Player) sender;

        // Handle the chatcolor command
        if (args.length == 2 && args[0].equalsIgnoreCase("chatcolor")) {
            return handleChatColorCommand(player, args[1]);
        }

        // Handle the soltys retire command
        if (args.length == 2 && args[0].equalsIgnoreCase("soltys") && args[1].equalsIgnoreCase("retire")) {
            return handleSoltysRetireCommand(player);
        }

        // Handle the soltys new command
        if (args.length == 2 && args[0].equalsIgnoreCase("soltys") && args[1].equalsIgnoreCase("new")) {
            return handleSoltysNewCommand(player);
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("buy") && args[1].equalsIgnoreCase("sakwa")) {
            customItems.giveGminnaSakwa(player); // Daj sakwę graczowi
            player.sendMessage(ChatColor.GREEN + "Otrzymałeś Gminną sakwę!");
            return true;
        }
        // If none of the above, send an error message
        player.sendMessage(ChatColor.RED + "Niepoprawne użycie komendy. Użyj: /gmina chatcolor <kolor>, /gmina soltys retire");
        return true;
    }

    // Handle the chat color command
    private boolean handleChatColorCommand(Player player, String colorName) {
        ChatColor color;

        try {
            color = ChatColor.valueOf(colorName.toUpperCase());
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Niepoprawny kolor! Dostępne kolory to: RED, BLUE, GREEN, YELLOW, AQUA, itd.");
            return true;
        }

        // Set the colored nickname
        player.setDisplayName(color + player.getName() + ChatColor.RESET);

        // Save the color to config
        FileConfiguration config = plugin.getConfig();
        config.set("players." + player.getUniqueId() + ".chatcolor", color.name());
        plugin.saveConfig();

        player.sendMessage(ChatColor.GREEN + "Twój kolor na czacie został zmieniony na " + color + colorName.toLowerCase() + ChatColor.RESET + "!");
        return true;
    }

    // Handle the Sołtys retire command
    private boolean handleSoltysRetireCommand(Player player) {
        // Call the retireSoltys method from WorldEvents
        if (soltys.retireSoltys(player)) {
            player.sendMessage(ChatColor.GREEN + "Zrezygnowałeś z bycia Sołtysem.");
        } else {
            player.sendMessage(ChatColor.RED + "Nie jesteś sołtysem, nie możesz zrezygnować.");
        }
        return true;
    }

    // Handle the new Sołtys command
    private boolean handleSoltysNewCommand(Player player) {
        // Ensure only Sołtys or players with permission can assign a new one
        if (!player.hasPermission("gmina.soltys.new")) {
            player.sendMessage(ChatColor.RED + "Nie masz uprawnień do tej komendy!");
            return true;
        }

        // Call the method to assign a new Sołtys
        soltys.chooseRandomSoltys();
        player.sendMessage(ChatColor.GREEN + "Nowy Sołtys został wybrany losowo!");
        return true;
    }


}
