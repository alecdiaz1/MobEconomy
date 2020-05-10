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

        if (p != null) {
            if (slainEntity instanceof Monster) {
                PlayerConfig.create(p);

                double decayRate = config.getDouble("DECAY_RATE");
                int decayTrigger = config.getInt("DECAY_TRIGGER");
                int secondsForRecovery = config.getInt("SECONDS_FOR_RECOVERY");
                double minReward = config.getDouble("MINIMUM_REWARD");

                int currentKillCount = PlayerConfig.get().getInt(chunk.toString() + ".killCount");
                long lastKillTime = PlayerConfig.get().getLong(chunk.toString() + ".lastKillTime");
                double elapsedTimeSeconds = (System.currentTimeMillis() - lastKillTime) / 1000.0;
                int elapsedTimeMinutes = (int) (elapsedTimeSeconds / secondsForRecovery);

                int newKillCount = currentKillCount - elapsedTimeMinutes;

                if (newKillCount < 0) {
                    newKillCount = 0;
                    PlayerConfig.get().set(chunk.toString() + ".killCount", newKillCount);
                }

                PlayerConfig.get().set(chunk.toString() + ".killCount", newKillCount + 1);
                PlayerConfig.get().set(chunk.toString() + ".lastKillTime", System.currentTimeMillis());
                PlayerConfig.save();

                double bonusChance = random.nextDouble();
                double reward;

                if (newKillCount > 10) {
                    reward = config.getDouble("DEFAULT") * Math.pow((1 - decayRate), newKillCount - decayTrigger);
                } else {
                    reward = config.getDouble("DEFAULT");
                }

                if (reward < minReward) {
                    reward = minReward;
                }

                if (bonusChance >= .75) {
                    reward *= 1.5;
                }

                try {
                    EconomyResponse response = eco.depositPlayer(p, reward);
                    if (response.transactionSuccess()){
                        p.sendMessage(ChatColor.GREEN + eco.format(response.amount) + " earned for killing a " +
                                slainEntity.getName());
                    }
                } catch(NullPointerException ignored) {}
            }
        }
    }
}
