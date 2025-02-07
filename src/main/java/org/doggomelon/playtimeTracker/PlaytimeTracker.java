package org.doggomelon.playtimeTracker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class PlaytimeTracker extends JavaPlugin implements Listener, CommandExecutor {

    private File playtimeFile;
    private FileConfiguration playtimeConfig;
    private final HashMap<UUID, Long> loginTimes = new HashMap<>();

    @Override
    public void onEnable() {
        createPlaytimeFile();
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("playtime").setExecutor(this);
    }

    @Override
    public void onDisable() {
        savePlaytimeData();
    }

    private void createPlaytimeFile() {
        playtimeFile = new File(getDataFolder(), "playtime.yml");
        if (!playtimeFile.exists()) {
            saveResource("playtime.yml", false);
        }
        playtimeConfig = YamlConfiguration.loadConfiguration(playtimeFile);
    }

    private void savePlaytimeData() {
        try {
            playtimeConfig.save(playtimeFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        loginTimes.put(player.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (loginTimes.containsKey(uuid)) {
            long playtime = System.currentTimeMillis() - loginTimes.get(uuid);
            addPlaytime(uuid, playtime);
            loginTimes.remove(uuid);
        }
    }

    private void addPlaytime(UUID uuid, long playtimeMillis) {
        String path = "players." + uuid.toString();
        long previousTime = playtimeConfig.getLong(path, 0);
        playtimeConfig.set(path, previousTime + playtimeMillis);
        savePlaytimeData();
    }

    public long getPlaytime(UUID uuid) {
        long storedTime = playtimeConfig.getLong("players." + uuid.toString(), 0);

        // Check if the player is currently online and add the active session time
        if (loginTimes.containsKey(uuid)) {
            long currentSessionTime = System.currentTimeMillis() - loginTimes.get(uuid);
            return storedTime + currentSessionTime;
        }

        return storedTime;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("playtime")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                long playtimeMillis = getPlaytime(player.getUniqueId());
                long playtimeSeconds = playtimeMillis / 1000;
                long hours = playtimeSeconds / 3600;
                long minutes = (playtimeSeconds % 3600) / 60;
                long seconds = playtimeSeconds % 60;
                player.sendMessage("Your total playtime: " + hours + "h " + minutes + "m " + seconds + "s");
            } else {
                sender.sendMessage("Only players can use this command!");
            }
            return true;
        }
        return false;
    }
}
