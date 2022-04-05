package org.kcsup.minigamecore.arena;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.kcsup.minigamecore.Main;
import org.kcsup.minigamecore.arena.sign.ArenaSign;
import org.kcsup.minigamecore.game.Countdown;
import org.kcsup.minigamecore.game.Game;
import org.kcsup.minigamecore.game.GameState;

import java.util.ArrayList;
import java.util.List;

public class Arena {
    private Main main;

    private int id;
    private String name;
    private List<Player> players;

    // Notes of clarification
    private Location spawn; // Waiting spawn
    private Location gameSpawn; // Spawn for when the game starts

    private GameState gameState;
    private Countdown countdown;
    private Game game;

    private ArenaSign arenaSign;

    public Arena(Main main, int id, String name, Location spawn, Location gameSpawn) {
        this.main = main;
        this.id = id;
        this.name = name;
        players = new ArrayList<>();
        this.spawn = spawn;
        this.gameSpawn = gameSpawn;
        countdown = new Countdown(this);
        game = new Game(this);

        arenaSign = null;

        setGameState(GameState.RECRUITING);
    }

    public void start() {
        setGameState(GameState.LIVE);
        teleportPlayers(gameSpawn);
        game.start();
        sendMessage(ChatColor.GREEN + "--------------------------------------------\n" +
                ChatColor.YELLOW + main.getConfig().getString("game-name") + "\n" +
                ChatColor.AQUA + main.getConfig().getString("game-description") + "\n" +
                ChatColor.GREEN + "--------------------------------------------");
    }

    public void reset() {
        setGameState(GameState.RESTARTING);

        teleportPlayers(getLobbySpawn());

        players.clear();
        countdown = new Countdown(this);
        game = new Game(this);
        setGameState(GameState.RECRUITING);

        // TODO: Add World Resetting (Note, this should be done while the game state is "resetting")
    }

    public void resetCountdown() {
//        teleportPlayers(spawn);
        countdown.stop();
        sendMessage(ChatColor.RED + "Waiting for more players.");
        setGameState(GameState.RECRUITING);
    }

    public void teleportPlayers(Location location) {
        for(Player player : players) {
            if(player == null) continue;

            player.teleport(location);
        }
    }

    public void sendMessage(String message) {
        for (Player player : players) {
            if(player == null) continue;

            player.sendMessage(message);
        }
    }

    public void sendSound(Sound sound, float volume, float pitch) {
        for (Player player : players) {
            if(player == null) continue;

            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public void sendTitle(String title, String subtitle) {
        for (Player player : players) {
            if(player == null) continue;

            player.sendTitle(title, subtitle);
        }
    }

    public void addPlayer(Player player) {
        if(players.contains(player) || isFull()) return;

        players.add(player);
        player.teleport(spawn);
        sendMessage(ChatColor.GREEN + player.getName() + " has joined!");

        if(hasRequiredPlayers() && !countdown.hasBegun()) countdown.begin();
        else reloadSign();
    }

    public void removePlayer(Player player) {
        if(!players.contains(player)) return;

        players.remove(player);
        player.teleport(getLobbySpawn());

        sendMessage(ChatColor.GREEN + player.getName() + " has quit!");
        if(!hasRequiredPlayers() && gameState.equals(GameState.COUNTDOWN)) resetCountdown();
        else if(players.size() <= 1 && gameState.equals(GameState.LIVE)) reset();
        else reloadSign();
    }

    public String[] getSignLines() {
        if(arenaSign == null) return null;

        String[] lines = new String[4];
        lines[0] = name;
        lines[3] = "";

        lines[1] = String.format("%s/%s", players.size(), getMaxPlayers());

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

    public Main getMain() {
        return main;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() { return players; }

    public int getRequiredPlayers() {
        return main.getArenaManager().getRequiredPlayers();
    }

    public int getMaxPlayers() {
        return main.getArenaManager().getMaxPlayers();
    }

    public Location getLobbySpawn() {
        return main.getArenaManager().getLobbySpawn();
    }

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

    public int getCountdownSeconds() {
        return main.getArenaManager().getCountdownSeconds();
    }

    public Game getGame() {
        return game;
    }

    public boolean hasRequiredPlayers() { return players.size() >= getRequiredPlayers(); }

    public boolean isFull() {
        return players.size() >= getMaxPlayers();
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;

        reloadSign();
    }

    public ArenaSign getArenaSign() {
        return arenaSign;
    }

    public void setArenaSign(ArenaSign arenaSign) {
        this.arenaSign = arenaSign;
    }

    public void reloadSign() {
        if(arenaSign != null) arenaSign.reloadSign();
    }
}
