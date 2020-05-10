package me.alecdiaz.mobeconomy.listeners;

import me.alecdiaz.mobeconomy.MobEconomy;
import me.alecdiaz.mobeconomy.PlayerConfig;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.io.File;
import java.util.Random;

public class MonsterDeathListener implements Listener {
    private final MobEconomy mobEconomy;

    public MonsterDeathListener(MobEconomy mobEconomy) { this.mobEconomy = mobEconomy; }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Economy eco = MobEconomy.getEconomy();
        Entity slainEntity = e.getEntity();
        Player p = e.getEntity().getKiller();
        Chunk chunk = e.getEntity().getChunk();
        Random random = new Random();

        FileConfiguration config = mobEconomy.getConfig();

        PlayerConfig.create(p);

        int killCount = PlayerConfig.get().getInt(chunk.toString());

        PlayerConfig.get().set(chunk.toString(), killCount + 1);
        PlayerConfig.save();

        double bonusChance = random.nextDouble();

        // Exponential decay
        double reward = config.getDouble("DEFAULT") * Math.pow((1-.05), killCount);

        System.out.println(killCount);

        if (bonusChance >= .75) {
            reward *= 1.5;
        }

        try {
            EconomyResponse response = eco.depositPlayer(p, reward);
            if (response.transactionSuccess()){
                assert p != null;
                p.sendMessage(ChatColor.GREEN + eco.format(response.amount) + " earned for killing a " +
                        slainEntity.getName());
            }
        } catch(NullPointerException ignored) {}
    }
}
