package org.kcsup.gramersgamecore.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Util {

    /* Location Json Structure
    {
        "world": String **World name
        "x": double **X position
        "y": double **Y position
        "z": double **Z position
        "yaw": float **Yaw rotation
        "pitch": float **Pitch rotation
    }
     */
    public static JSONObject locationToJson(Location location) {
        if(location == null) return null;

        try {
            JSONObject locationJson = new JSONObject();
            locationJson.put("world", location.getWorld().getName());
            locationJson.put("x", location.getX());
            locationJson.put("y", location.getY());
            locationJson.put("z", location.getZ());
            locationJson.put("yaw", location.getYaw());
            locationJson.put("pitch", location.getPitch());

            return locationJson;
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Location jsonToLocation(JSONObject jsonObject) {
        if(jsonObject == null) return null;

        try {
            World world = Bukkit.getWorld(jsonObject.getString("world"));
            double x = jsonObject.getDouble("x");
            double y = jsonObject.getDouble("y");
            double z = jsonObject.getDouble("z");
            float yaw = jsonObject.getFloat("yaw");
            float pitch = jsonObject.getFloat("pitch");

            return new Location(world, x, y, z, yaw, pitch);
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getJsonFile(File jsonFile) throws FileNotFoundException, JSONException {
        if(jsonFile == null) return null;

        FileReader fileReader = new FileReader(jsonFile);
        JSONTokener tokener = new JSONTokener(fileReader);
        return new JSONObject(tokener);
    }

}