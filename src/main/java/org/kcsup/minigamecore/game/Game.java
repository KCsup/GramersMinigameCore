package org.kcsup.minigamecore.game;

import org.kcsup.minigamecore.arena.Arena;

public class Game {

    private Arena arena;

    public Game(Arena arena) {
        this.arena = arena;
    }

    public void start() {
        stop();
    }

    public void stop() {
        arena.reset();
    }

}
