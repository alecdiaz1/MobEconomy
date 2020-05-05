package me.alecdiaz.mobeconomy.listeners;

import me.alecdiaz.mobeconomy.MobEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class MonsterDeathListener implements Listener {
    private final MobEconomy mobEconomy;

    public MonsterDeathListener(MobEconomy mobEconomy) { this.mobEconomy = mobEconomy; }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Economy eco = MobEconomy.getEconomy();
        Entity slainEntity = e.getEntity();
        Player p = e.getEntity().getKiller();
        Random random = new Random();

        FileConfiguration config = mobEconomy.getConfig();

        double bonusChance = random.nextDouble();
        double reward;

        if (slainEntity instanceof Monster) {
            Block middle = e.getEntity().getLocation().getBlock();
            if (isMobInFarm(middle)) {
                reward = config.getDouble("FARMED");

                double noDropChance = random.nextDouble();
                if (noDropChance <= .40) {
                    e.getDrops().clear();
                }
            } else {
                reward = config.getDouble("DEFAULT");
            }

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

    public boolean isMobInFarm(Block middle) {
        int radius = 2;
        for (int x = radius; x >= -radius; x--) {
            for (int y = radius; y >= -radius; y--) {
                for (int z = radius; z >= -radius; z--) {
                    if (middle.getRelative(x, y, z).getType() == Material.HOPPER) {
                        mobEconomy.getServer().broadcastMessage("died on hopper");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
