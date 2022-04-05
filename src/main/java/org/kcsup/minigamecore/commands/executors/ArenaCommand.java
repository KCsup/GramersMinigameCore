package org.kcsup.minigamecore.commands.executors;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kcsup.minigamecore.Main;
import org.kcsup.minigamecore.arena.Arena;
import org.kcsup.minigamecore.game.GameState;

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

        String name = main.getConfig().getString("command-name");
        String error = ChatColor.RED + "Invalid usage. Correct usage is:" +
                "\n- /" + name +" list" + "\n- /" + name + " join [id]" + "\n- /" + name + " leave";

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
        else if(args.length == 2) {
            String id = args[1];

            switch(args[0].toLowerCase()) {
                case "join":
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

                        if (arena.getGameState() == GameState.LIVE || arena.getGameState() == GameState.RESTARTING || arena.isFull()) {
                            player.sendMessage(ChatColor.RED + "You cannot join ths arena right now.\n" + error);
                            return false;
                        }

                        player.sendMessage(ChatColor.GREEN + "Joining Arena: " + arena.getName());
                        arena.addPlayer(player);
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "There is no arena with the Id: " + id + ".\n" + error);
                        return false;
                    }

                    break;
                case "sign":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command...");
                        return false;
                    }
                    else if(main.getSignManager().settingSign.containsKey(player)) {
                        player.sendMessage(ChatColor.RED + "You are already setting up an arena sign.");
                        return false;
                    }

                    try {
                        int arenaId = Integer.parseInt(id);

                        if(main.getArenaManager().getArena(arenaId) == null) {
                            player.sendMessage(ChatColor.RED + "There is no arena with the Id: " + id);
                            return false;
                        }

                        Arena arena = main.getArenaManager().getArena(arenaId);

                        ItemStack wand = main.getSignManager().getSignWand();

                        if(!player.getInventory().contains(wand)) player.getInventory().addItem(wand);

                        main.getSignManager().settingSign.put(player, arena);

                        return false;
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "There is no arena with the Id: " + id);
                        return false;
                    }
                case "create":
                    if(!player.isOp()) {
                        player.sendMessage(ChatColor.RED + "You must be an admin to use this command.");
                        return false;
                    }

                    main.getArenaManager().storeArena(new Arena(main, main.getArenaManager().getArenas().size(), id,
                            main.getArenaManager().getLobbySpawn(), main.getArenaManager().getLobbySpawn()));
                    break;
                default:
                    break;
            }
        } else {
            player.sendMessage(error);
        }

        return false;
    }
}
