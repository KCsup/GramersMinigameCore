package org.kcsup.gramersgamecore.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.kcsup.gramersgamecore.Main;
import org.kcsup.gramersgamecore.arena.Arena;
import org.kcsup.gramersgamecore.game.GameState;

public class ArenaCommand implements CommandExecutor {
    private Main main;

    public ArenaCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return false;
        }

        Player player = (Player) sender;

        String error = ChatColor.RED + "Invalid usage. Correct usage is:" +
                "\n- /arena list" + "\n- /arena join [id]" + "\n- /arena leave";

        if(args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    if(main.getArenaManager().getArenas().isEmpty()) {
                        player.sendMessage(ChatColor.RED + "There are no available arenas at this time...");
                        return false;
                    }
                    StringBuilder arenaList = new StringBuilder(ChatColor.GREEN + "Current Arenas:");
                    for(Arena arena : main.getArenaManager().getArenas()) {
                        arenaList.append("\n- ").append(arena.getName()).append(" {").append(arena.getId())
                                .append("} [").append(arena.getGameState()).append("]");
                    }
                    player.sendMessage(arenaList.toString());
                    break;
                case "leave":
                    if(main.getArenaManager().isPlaying(player)) {
                        Arena arena = main.getArenaManager().getArena(player);

                        player.sendMessage(ChatColor.GREEN + "Leaving Arena: " + arena.getName());
                        arena.removePlayer(player);
                    }
                    else player.sendMessage(ChatColor.RED + "You aren't currently in any arena.");
                    break;
                default:
                    player.sendMessage(error);
                    break;
            }
        }
        else if(args.length == 2 && args[0].equalsIgnoreCase("join")) {
            String id = args[1];

            try {
                Arena arena;
                if (main.getArenaManager().getArena(id) != null)
                    arena = main.getArenaManager().getArena(id);
                else if (main.getArenaManager().getArena(Integer.parseInt(id)) != null)
                    arena = main.getArenaManager().getArena(Integer.parseInt(id));
                else {
                    player.sendMessage(ChatColor.RED + "There is no arena with the Id: " + id + ".\n" + error);
                    return false;
                }

                if (arena.getGameState() != GameState.RECRUITING || arena.isFull()) {
                    player.sendMessage(ChatColor.RED + "You cannot join ths arena right now.\n" + error);
                    return false;
                }

                player.sendMessage(ChatColor.GREEN + "Joining Arena: " + arena.getName());
                arena.addPlayer(player);
            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "There is no arena with the Id: " + id + ".\n" + error);
                return false;
            }
        } else {
            player.sendMessage(error);
        }

        return false;
    }
}
