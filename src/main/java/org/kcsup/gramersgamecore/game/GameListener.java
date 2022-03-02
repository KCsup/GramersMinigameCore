package org.kcsup.gramersgamecore.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.kcsup.gramersgamecore.Main;

public class GameListener implements Listener {
    private Main main;

    public GameListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        // TODO: Put arena sign interaction
    }
}
