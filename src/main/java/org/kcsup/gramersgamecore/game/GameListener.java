package org.kcsup.gramersgamecore.game;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.kcsup.gramersgamecore.Main;
import org.kcsup.gramersgamecore.arena.Arena;
import org.kcsup.gramersgamecore.arena.sign.ArenaSign;
import org.kcsup.gramersgamecore.util.Util;

public class GameListener implements Listener {
    private Main main;

    public GameListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if(e.hasBlock()) {
            Block block = e.getClickedBlock();

            if(player.isOp()) {
                if(Util.isSignMaterial(block.getType()) && player.getItemInHand().equals(main.getSignManager().getSignWand())
                        && !main.getSignManager().isSign(block.getLocation())
                        && main.getSignManager().settingSign.containsKey(player)) {
                    e.setCancelled(true);

                    player.sendMessage("Storing Sign for Arena: " + main.getSignManager().settingSign.get(player).getId());
                    main.getSignManager().storeSign(new ArenaSign(block.getLocation(),
                            main.getSignManager().settingSign.get(player)));
                    main.getSignManager().settingSign.remove(player);
                    player.getInventory().remove(main.getSignManager().getSignWand());

                    return;
                }
            }

            if(main.getSignManager().isSign(block.getLocation())) {
                e.setCancelled(true);

                ArenaSign sign = main.getSignManager().getSign(block.getLocation());
                if(sign == null) return;

                Arena arenaFromSign = sign.getArena();
                if(arenaFromSign == null) return;

                switch(arenaFromSign.getGameState()) {
                    case RECRUITING:
                        arenaFromSign.addPlayer(player);
                        break;
                    case COUNTDOWN:
                        player.sendMessage(ChatColor.RED + "The game you're trying to join is currently full. Try again later.");
                        break;
                    default:
                        player.sendMessage(ChatColor.RED + "The game you're trying to join is currently live. Try again later.");
                        break;
                }
            }
        }
    }
}
