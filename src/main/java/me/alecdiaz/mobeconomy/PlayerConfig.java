package me.alecdiaz.mobeconomy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public class PlayerConfig {

    static File cfile;
    static FileConfiguration config;
    static File folder = new File(MobEconomy.getInstance().getDataFolder(), "player data" + File.separator);
    static File df = MobEconomy.getInstance().getDataFolder();

    public static void create(Player p) {
        cfile = new File(df, "player data" + File.separator + p.getUniqueId() + ".yml");
        if (!df.exists()) df.mkdir();
        if (!cfile.exists()) {
            try {
                cfile.createNewFile();
            } catch(Exception e) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error creating " + cfile.getName() + "!");
            }
        }
        config = YamlConfiguration.loadConfiguration(cfile);
    }

    public static File getfolder() {
        return folder;
    }

    public static File getfile() {
        return cfile;
    }

    public static void load(Player p) {
        cfile = new File(df, "player data" + File.separator + p.getUniqueId() + ".yml");
        config = YamlConfiguration.loadConfiguration(cfile);
    }

    public static FileConfiguration get() {
        return config;
    }

    public static void save() {
        try {
            config.save(cfile);
        } catch(Exception e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Error saving " + cfile.getName() + "!");
        }
    }
}