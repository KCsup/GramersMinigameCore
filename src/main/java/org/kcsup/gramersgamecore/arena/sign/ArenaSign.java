package org.kcsup.gramersgamecore.arena.sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.kcsup.gramersgamecore.arena.Arena;

public class ArenaSign {

    private Location location;
    private Arena arena;
    private Sign sign;
    private String[] lines;

    public ArenaSign(Location location, Arena arena, String[] lines) {
        this.location = location;
        this.arena = arena;
        this.lines = lines;

        if(this.location.getBlock().getType() == Material.WALL_SIGN ||
            this.location.getBlock().getType() == Material.SIGN_POST) {
            sign = (Sign) this.location.getBlock();
            reloadSign();
        }
    }

    public void reloadSign() {
        if(sign == null) return;

        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(line != null) sign.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
            else sign.setLine(i, null);
        }
        sign.update();
    }

    public Location getLocation() {
        return location;
    }

    public Arena getArena() {
        return arena;
    }

    public String[] getLines() {
        return lines;
    }

    public String getLineAt(int index) {
        try {
            return lines[index];
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
    }
}
