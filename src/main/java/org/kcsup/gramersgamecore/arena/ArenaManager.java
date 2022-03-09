package org.kcsup.gramersgamecore.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kcsup.gramersgamecore.Main;
import org.kcsup.gramersgamecore.game.GameState;
import org.kcsup.gramersgamecore.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArenaManager {
    private Main main;
    private File arenaData;
    private List<Arena> arenas;

    public ArenaManager(Main main) {
        this.main = main;
        arenas = new ArrayList<>();
        filesCheck();

        if(arenaData.exists()) initiateArenas();
    }

    private void filesCheck() {
        String arenaDataPath = main.getDataFolder() + "/arenaData.json";
        arenaData = new File(arenaDataPath);
        if(!arenaData.exists()) {
            try {
                arenaData.createNewFile();

                JSONObject file = new JSONObject();
                file.put("arenas", new JSONArray());

                Util.putJsonFile(arenaData, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initiateArenas() {
        if(anyArenasLive()) return;

        arenas.clear();

        try {
            JSONObject file = Util.getJsonFile(arenaData);
            JSONArray jArenas = file.getJSONArray("arenas");
            for(Object o : jArenas) {
                JSONObject arenaJson = (JSONObject) o;
                Arena arena = jsonToArena(arenaJson);
                if(arena != null) arenas.add(arena);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Arena> getArenas() { return arenas; }

    public boolean isPlaying(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player.getUniqueId())) return true;
        }

        return false;
    }

    public Arena getArena(Player player) {
        for(Arena arena : arenas) {
            if(arena.getPlayers().contains(player.getUniqueId())) return arena;
        }

        return null;
    }

    public Arena getArena(int id) {
        for(Arena arena : arenas) {
            if(id == arena.getId()) return arena;
        }

        return null;
    }

    public Arena getArena(String name) {
        for(Arena arena : arenas) {
            if(Objects.equals(name, arena.getName())) return arena;
        }

        return null;
    }

    public boolean anyArenasLive() {
        for(Arena arena : arenas) {
            if (arena.getGameState() != GameState.RECRUITING) {
                return true;
            }
        }

        return false;
    }

    public int getCountdownSeconds() {
        return main.getConfig().getInt("countdown");
    }

    public int getRequiredPlayers() {
        return main.getConfig().getInt("required-players");
    }

    public int getMaxPlayers() {
        return main.getConfig().getInt("max-players");
    }

    public Location getLobbySpawn() {
        return new Location(Bukkit.getWorld(main.getConfig().getString("lobby-spawn.world")),
                main.getConfig().getDouble("lobby-spawn.x"),
                main.getConfig().getDouble("lobby-spawn.y"),
                main.getConfig().getDouble("lobby-spawn.z"),
                main.getConfig().getInt("lobby-spawn.yaw"),
                main.getConfig().getInt("lobby-spawn.pitch"));
    }

    public void setLobbySpawn(Location lobbySpawn) {
        if(lobbySpawn == null || anyArenasLive()) return;

        main.getConfig().set("lobby-spawn.world", lobbySpawn.getWorld().getName());
        main.getConfig().set("lobby-spawn.x", lobbySpawn.getX());
        main.getConfig().set("lobby-spawn.y", lobbySpawn.getY());
        main.getConfig().set("lobby-spawn.z", lobbySpawn.getZ());
        main.getConfig().set("lobby-spawn.yaw", lobbySpawn.getYaw());
        main.getConfig().set("lobby-spawn.pitch", lobbySpawn.getPitch());
    }

    // You should probably never use this, manually creating arenas is probably a better idea
    private JSONObject arenaToJson(Arena arena) {
        if(arena == null) return null;

        JSONObject arenaJson = new JSONObject();
        arenaJson.put("name", arena.getName());
        arenaJson.put("spawn", Util.locationToJson(arena.getSpawn()));
        arenaJson.put("gameSpawn", Util.locationToJson(arena.getGameSpawn()));

        return arenaJson;
    }


    private Arena jsonToArena(JSONObject jsonObject) {
        if (jsonObject == null) return null;

        try {
            JSONObject file = Util.getJsonFile(arenaData);
            List<Object> jArenas = file.getJSONArray("arenas").toList();
            int id = 0;
            for(Object o : jArenas) {
                JSONObject object = (JSONObject) o;
                if(Objects.equals(object.toString(), jsonObject.toString())) {
                    id = jArenas.indexOf(o);
                    break;
                }

                if(jArenas.indexOf(o) == jArenas.size()) return null;
            }

            String name = jsonObject.getString("name");
            Location spawn = Util.jsonToLocation(jsonObject.getJSONObject("spawn"));
            Location gameSpawn = Util.jsonToLocation(jsonObject.getJSONObject("gameSpawn"));
            if(getLobbySpawn() == null) return null;

            return new Arena(main, id, name, spawn, gameSpawn);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
