package org.legiosha.whiteList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public final class WhiteList extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("whitelist").setExecutor(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Проверяем, включен ли вайтлист
        if (getConfig().getBoolean("whitelist-enabled")) {
            if (!getConfig().getStringList("players").contains(event.getPlayer().getName())) {
                event.getPlayer().kickPlayer(getConfig().getConfigurationSection("messages").getString("kick-reason"));
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("");
        } else {
            List<String> playerList;
            String subCommand = args[0];
            switch (subCommand) {
                case "clear":
                    editConfig(config -> config.set("players", Collections.emptyList()));
                    sender.sendMessage(getConfig().getConfigurationSection("messages").getString("clear-message"));
                    break;
                case "add":
                    if (args.length < 2) {
                        sender.sendMessage(getConfig().getConfigurationSection("messages").getString("add-subcommand-help"));
                        break;
                    }
                    playerList = getConfig().getStringList("players");
                    playerList.add(args[1]);
                    editConfig(config -> config.set("players", playerList));
                    sender.sendMessage(getConfig().getConfigurationSection("messages").getString("add-message"));
                    break;
                case "remove":
                    if (args.length < 2) {
                        sender.sendMessage(getConfig().getConfigurationSection("messages").getString("remove-subcommand-help"));
                        break;
                    }
                    playerList = getConfig().getStringList("players");
                    playerList.remove(args[1]);
                    editConfig(config -> config.set("players", playerList));
                    sender.sendMessage(getConfig().getConfigurationSection("messages").getString("remove-message"));
                    break;
                case "enable":
                    editConfig(config -> config.set("whitelist-enabled", true));
                    sender.sendMessage("Вайтлист включен.");
                    break;
                case "disable":
                    editConfig(config -> config.set("whitelist-enabled", false));
                    sender.sendMessage("Вайтлист выключен.");
                    break;
            }
        }
        return true;
    }

    private void editConfig(Consumer<FileConfiguration> action) {
        action.accept(getConfig());
        saveConfig();
    }
}
