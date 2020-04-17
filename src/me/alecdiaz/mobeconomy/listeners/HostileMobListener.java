package me.alecdiaz.mobeconomy.listeners;

import me.alecdiaz.mobeconomy.MobEconomy;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Random;

public class HostileMobListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        Economy eco = MobEconomy.getEconomy();
        Entity slainEntity = e.getEntity();
        Player p = e.getEntity().getKiller();
        Random random = new Random();

        if (slainEntity instanceof Monster) {
            double chance = random.nextDouble();
            double reward = 0;
            
            if (chance < .80) {
                reward = 1.0;
            } else if (.80 <= chance && chance < 90) {
                reward = 2.0;
            } else if (.90 <= chance && chance < .99){
                reward = 3.0;
            } else {
                reward = 4.0;
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
