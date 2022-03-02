package org.kcsup.gramersgamecore.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.kcsup.gramersgamecore.Main;
import org.kcsup.gramersgamecore.game.GameState;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

                file.put("countdownSeconds", 30);
                file.put("requiredPlayers", 2);
                file.put("maxPlayers", 10);
                file.put("lobbySpawn", JSONObject.NULL);

                FileWriter fileWriter = new FileWriter(arenaData);
                fileWriter.write(file.toString());
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initiateArenas() {
        if(anyArenasLive()) return;

        arenas.clear();

        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            JSONArray jArenas = file.getJSONArray("arenas");
            for(Object o : jArenas) {
                JSONObject arenaJson = new JSONObject(o.toString());
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
        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            return file.getInt("countdownSeconds");
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getRequiredPlayers() {
        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            return file.getInt("requiredPlayers");
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int getMaxPlayers() {
        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            return file.getInt("maxPlayers");
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Location getLobbySpawn() {
        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            Object loc = file.get("lobbySpawn");

            if(loc == JSONObject.NULL) return null;
            else return jsonToLocation((new JSONObject(loc.toString())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setLobbySpawn(Location lobbySpawn) {
        if(lobbySpawn == null || anyArenasLive()) return;

        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            file.put("lobbySpawn", locationToJson(lobbySpawn));

            FileWriter fileWriter = new FileWriter(arenaData);
            fileWriter.write(file.toString());
            fileWriter.flush();

            initiateArenas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // You should probably never use this, manually creating arenas is probably a better idea
    private JSONObject arenaToJson(Arena arena) {
        if(arena == null) return null;

        JSONObject arenaJson = new JSONObject();
        arenaJson.put("name", arena.getName());
        arenaJson.put("spawn", locationToJson(arena.getSpawn()));
        arenaJson.put("gameSpawn", locationToJson(arena.getGameSpawn()));

        return arenaJson;
    }

    private Arena jsonToArena(JSONObject jsonObject) {
        if (jsonObject == null) return null;

        try {
            FileReader fileReader = new FileReader(arenaData);
            JSONTokener jsonTokener = new JSONTokener(fileReader);
            JSONObject file = new JSONObject(jsonTokener);
            List<Object> jArenas = file.getJSONArray("arenas").toList();
            int id = 0;
            for(Object o : jArenas) {
                JSONObject object = new JSONObject(o);
                if(Objects.equals(object.toString(), jsonObject.toString())) {
                    id = jArenas.indexOf(o);
                    break;
                }

                if(jArenas.indexOf(o) == jArenas.size()) return null;
            }

            String name = jsonObject.getString("name");
            Location spawn = jsonToLocation(jsonObject.getJSONObject("spawn"));
            Location gameSpawn = jsonToLocation(jsonObject.getJSONObject("gameSpawn"));
            if(getLobbySpawn() == null) return null;

            return new Arena(main, id, name, spawn, getLobbySpawn(), gameSpawn, getCountdownSeconds(), getRequiredPlayers(), getMaxPlayers());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject locationToJson(Location location) {
        if(location == null) return null;

        JSONObject locationJson = new JSONObject();
        locationJson.put("world", location.getWorld().getName());
        locationJson.put("x", location.getX());
        locationJson.put("y", location.getY());
        locationJson.put("z", location.getZ());
        locationJson.put("yaw", location.getYaw());
        locationJson.put("pitch", location.getPitch());

        return locationJson;
    }

    private Location jsonToLocation(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        World world = Bukkit.getWorld(jsonObject.getString("world"));
        double x = jsonObject.getDouble("x");
        double y = jsonObject.getDouble("y");
        double z = jsonObject.getDouble("z");
        float yaw = jsonObject.getFloat("yaw");
        float pitch = jsonObject.getFloat("pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }
}
