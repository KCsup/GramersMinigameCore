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
import java.util.List;
import java.util.UUID;

public class Arena {
    private Main main;

    private int id;
    private String name;
    private List<UUID> players;
    private Location spawn;
    private Location lobbySpawn;
    private Location gameSpawn;

    private GameState gameState;
    private Countdown countdown;
    private int countdownSeconds;
    private Game game;
    private int requiredPlayers;
    private int maxPlayers;

    public Arena(Main main, int id, String name, Location spawn, Location lobbySpawn, Location gameSpawn, int countdownSeconds, int requiredPlayers, int maxPlayers) {
        this.main = main;
        this.id = id;
        this.name = name;
        players = new ArrayList<>();
        this.spawn = spawn;
        this.lobbySpawn = lobbySpawn;
        this.gameSpawn = gameSpawn;
        gameState = GameState.RECRUITING;
        countdown = new Countdown(this);
        this.countdownSeconds = countdownSeconds;
        game = new Game(this);
        this.requiredPlayers = requiredPlayers;
        this.maxPlayers = maxPlayers;

        // TODO: ADD SIGN UPDATES
    }

    public void start() {
        gameState = GameState.LIVE;
        game.start();
    }

    public void reset() {
        teleportPlayers(lobbySpawn);

        gameState = GameState.RECRUITING;
        players.clear();
        countdown = new Countdown(this);
        game = new Game(this);

        // TODO: Add World Resetting (Note, this should be done while the game state is "resetting")
    }

    public void resetCountdown() {
//        teleportPlayers(spawn);
        gameState = GameState.RECRUITING;
        countdown.cancel();
        sendMessage(ChatColor.RED + "Waiting for more players.");
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

        if(hasRequiredPlayers()) {
            countdown.begin();
        }
    }

    public void removePlayer(Player player) {
        if(!players.contains(player.getUniqueId())) return;

        players.remove(player.getUniqueId());
        player.teleport(lobbySpawn);

        sendMessage(ChatColor.GREEN + player.getName() + " has quit!");
        if(!hasRequiredPlayers() && gameState.equals(GameState.COUNTDOWN)) resetCountdown();
        else if(players.size() <= 1 && gameState.equals(GameState.LIVE)) reset();
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

    public Location getLobbySpawn() {
        return lobbySpawn;
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

    public int getCountdownSeconds() { return countdownSeconds; }

    public Game getGame() {
        return game;
    }

    public int getRequiredPlayers() {
        return requiredPlayers;
    }

    public boolean hasRequiredPlayers() { return players.size() >= requiredPlayers; }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isFull() {
        return players.size() >= maxPlayers;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public ArenaSign getArenaSign() { return main.getSignManager().getSign(this); }
}
