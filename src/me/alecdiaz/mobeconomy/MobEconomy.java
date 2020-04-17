package me.alecdiaz.mobeconomy;

import me.alecdiaz.mobeconomy.listeners.HostileMobListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class MobEconomy extends JavaPlugin {

    private static Economy economy = null;

    @Override
    public void onEnable() {
        // Plugin startup logic
        if (!setupEconomy() ) {
            System.out.println("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
//        Bukkit.getPluginManager().registerEvents(new BreakBlockListener(), this);
        Bukkit.getPluginManager().registerEvents(new HostileMobListener(), this);
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public static Economy getEconomy() {
        return economy;
    }

}
