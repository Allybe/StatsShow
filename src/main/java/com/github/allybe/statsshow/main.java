package com.github.allybe.statsshow;

import com.github.allybe.statsshow.commands.statsCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        System.out.println("StatsShow starting");
        System.out.println("No bitches is starting");


        this.getCommand("showstats").setExecutor(new statsCommand());
        this.getCommand("showstats").setTabCompleter(new tabCompletion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        System.out.println("No bitches is ending");
    }
}
