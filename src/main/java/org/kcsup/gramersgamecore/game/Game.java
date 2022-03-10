package org.kcsup.gramersgamecore.game;

import org.bukkit.Location;
import org.kcsup.gramersgamecore.arena.Arena;

public class Game {

    private Arena arena;
    private Location gameSpawn;

    public Game(Arena arena) {
        this.arena = arena;
        gameSpawn = arena.getGameSpawn();
    }

    public void start() {
        arena.teleportPlayers(gameSpawn);
        stop();
    }

    public void stop() {
        arena.reset();
    }

}
