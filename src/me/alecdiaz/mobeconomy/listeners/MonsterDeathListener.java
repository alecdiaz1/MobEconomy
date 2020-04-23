package me.alecdiaz.mobeconomy.listeners;

import me.alecdiaz.mobeconomy.MobEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Boss;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;
import java.util.Set;

public class MonsterDeathListener implements Listener {
    private final MobEconomy mobEconomy;

    public MonsterDeathListener(MobEconomy mobEconomy) {
        this.mobEconomy = mobEconomy;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        Economy eco = MobEconomy.getEconomy();
        Entity slainEntity = e.getEntity();
        Player p = e.getEntity().getKiller();
        Random random = new Random();

        FileConfiguration config = mobEconomy.getConfig();
        String mobName = slainEntity.getName().toUpperCase();

        double chance = random.nextDouble();
        double reward;

        if (slainEntity instanceof Monster || slainEntity instanceof Boss) {
            if (config.contains(mobName)) {
                reward = config.getDouble(mobName);
            } else {
                if (slainEntity instanceof Monster) {
                    reward = config.getDouble("DEFAULT");
                } else {
                    reward = config.getDouble("DEFAULT_BOSS");
                }
            }

            if (chance >= .75) {
                reward = reward * 1.5;
            }

            EconomyResponse response = eco.depositPlayer(p, reward);
            if (response.transactionSuccess()){
                assert p != null;
                p.sendMessage(ChatColor.GREEN + eco.format(response.amount) + " earned for killing a " +
                        slainEntity.getName());
            }
        }
    }
}
