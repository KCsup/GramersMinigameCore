package org.kcsup.gramersgamecore.game;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.kcsup.gramersgamecore.arena.Arena;

public class Countdown extends BukkitRunnable {
    private Arena arena;
    private int seconds;

    public Countdown(Arena arena) {
        this.arena = arena;
        this.seconds = arena.getCountdownSeconds();
    }

    public void begin() {
        arena.setGameState(GameState.COUNTDOWN);
        this.runTaskTimer(arena.getMain(), 0, 20);

        // TODO: sign stuff fuck you
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

        if(arena.getPlayers().size() < arena.getRequiredPlayers()) {
            cancel();
            arena.setGameState(GameState.RECRUITING);
            arena.sendMessage(ChatColor.RED + "Waiting for more players.");

            // TODO: more sign shit I hate you
            return;
        }

        seconds--;
    }
}
