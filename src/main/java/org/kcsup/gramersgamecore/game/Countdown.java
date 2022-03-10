package org.kcsup.gramersgamecore.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.kcsup.gramersgamecore.arena.Arena;

public class Countdown extends BukkitRunnable {
    private Arena arena;
    private int seconds;

    public Countdown(Arena arena) {
        this.arena = arena;
    }

    public void begin() {
        seconds = arena.getCountdownSeconds();
        arena.setGameState(GameState.COUNTDOWN);
        runTaskTimer(arena.getMain(), 0, 20);
    }

    @Override
    public void run() {
        if(seconds == 0) {
            cancel();
            arena.start();
            return;
        }

        if(seconds % 30 == 0 || seconds <= 10) {
            if(seconds == 1) {
                arena.sendMessage(ChatColor.GREEN + "Game will start in 1 second.");
            } else {
                arena.sendMessage(ChatColor.GREEN + "Game will start in " + seconds + " seconds.");
            }
        }

        seconds--;
    }
}
