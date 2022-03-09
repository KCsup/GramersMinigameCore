package org.kcsup.gramersgamecore.arena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.kcsup.gramersgamecore.Main;
import org.kcsup.gramersgamecore.arena.sign.ArenaSign;
import org.kcsup.gramersgamecore.game.Countdown;
import org.kcsup.gramersgamecore.game.Game;
import org.kcsup.gramersgamecore.game.GameState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Arena {
    private Main main;

    private int id;
    private String name;
    private List<UUID> players;

    // Notes of clarification
    private Location spawn; // Waiting spawn
    private Location gameSpawn; // Spawn for when the game starts

    private GameState gameState;
    private Countdown countdown;
    private Game game;

    public Arena(Main main, int id, String name, Location spawn, Location gameSpawn) {
        this.main = main;
        this.id = id;
        this.name = name;
        players = new ArrayList<>();
        this.spawn = spawn;
        this.gameSpawn = gameSpawn;
        countdown = new Countdown(this);
        game = new Game(this);

        setGameState(GameState.RECRUITING);
    }

    public void start() {
        gameState = GameState.LIVE;
        game.start();
    }

    public void reset() {
        teleportPlayers(main.getArenaManager().getLobbySpawn());

        players.clear();
        countdown = new Countdown(this);
        game = new Game(this);
        setGameState(GameState.RECRUITING);

        // TODO: Add World Resetting (Note, this should be done while the game state is "resetting")
    }

    public void resetCountdown() {
//        teleportPlayers(spawn);
        countdown.cancel();
        sendMessage(ChatColor.RED + "Waiting for more players.");
        setGameState(GameState.RECRUITING);
    }

    public void teleportPlayers(Location location) {
        for(UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;

            player.teleport(location);
        }
    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;

            player.sendMessage(message);
        }
    }

    public void sendSound(Sound sound, float volume, float pitch) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;

            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            if(player == null) continue;

            player.sendTitle(title, subtitle);
        }
    }

    public void addPlayer(Player player) {
        if(players.contains(player.getUniqueId()) || isFull()) return;

        players.add(player.getUniqueId());
        player.teleport(spawn);
        sendMessage(ChatColor.GREEN + player.getName() + " has joined!");

        if(hasRequiredPlayers()) countdown.begin();
        else reloadSign();
    }

    public void removePlayer(Player player) {
        if(!players.contains(player.getUniqueId())) return;

        players.remove(player.getUniqueId());
        player.teleport(main.getArenaManager().getLobbySpawn());

        sendMessage(ChatColor.GREEN + player.getName() + " has quit!");
        if(!hasRequiredPlayers() && gameState.equals(GameState.COUNTDOWN)) resetCountdown();
        else if(players.size() <= 1 && gameState.equals(GameState.LIVE)) reset();
        else reloadSign();
    }

    public String[] getSignLines() {
        if(getArenaSign() == null) return null;

        String[] lines = new String[4];
        Arrays.fill(lines, "");

        lines[1] = String.format("%s/%s", players.size(), main.getArenaManager().getMaxPlayers());

        ChatColor stateColor;
        switch(gameState) {
            case RECRUITING:
                stateColor = ChatColor.GREEN;
                break;
            case COUNTDOWN:
                stateColor = ChatColor.AQUA;
                break;
            case LIVE:
                stateColor = ChatColor.RED;
                break;
            case RESTARTING:
                stateColor = ChatColor.YELLOW;
                break;
            default:
                stateColor = null;
                break;
        }

        if(stateColor == null) return null;

        lines[2] = stateColor + gameState.toString();

        return lines;
    }

    public Main getMain() { return main; }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public List<UUID> getPlayers() { return players; }

    public Location getSpawn() {
        return spawn;
    }

    public Location getGameSpawn() {
        return gameSpawn;
    }

    public GameState getGameState() {
        return gameState;
    }

    public Countdown getCountdown() {
        return countdown;
    }

    public Game getGame() {
        return game;
    }

    public boolean hasRequiredPlayers() { return players.size() >= main.getArenaManager().getRequiredPlayers(); }

    public boolean isFull() {
        return players.size() >= main.getArenaManager().getMaxPlayers();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        reloadSign();
    }

    public ArenaSign getArenaSign() { return main.getSignManager().getSign(this); }

    public void reloadSign() {
        ArenaSign sign = getArenaSign();

        if(sign == null) return;

        sign.reloadSign();
    }
}
