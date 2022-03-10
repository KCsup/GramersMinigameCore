package org.kcsup.gramersgamecore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.kcsup.gramersgamecore.arena.Arena;
import org.kcsup.gramersgamecore.arena.ArenaManager;
import org.kcsup.gramersgamecore.arena.sign.SignManager;
import org.kcsup.gramersgamecore.commands.ArenaCommand;
import org.kcsup.gramersgamecore.game.GameListener;

public final class Main extends JavaPlugin {
    private ArenaManager arenaManager;
    private SignManager signManager;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        signManager = new SignManager(this);
        arenaManager = new ArenaManager(this);

        arenaManager.initiateArenas();
        signManager.reloadAllSigns();

        Bukkit.getPluginManager().registerEvents(new GameListener(this), this);

        getCommand("arena").setExecutor(new ArenaCommand(this));
    }

    public ArenaManager getArenaManager() { return arenaManager; }

    public SignManager getSignManager() {
        return signManager;
    }

    @Override
    public void onDisable() {
        for(Arena arena : arenaManager.getArenas()) arena.reset();
    }
}
