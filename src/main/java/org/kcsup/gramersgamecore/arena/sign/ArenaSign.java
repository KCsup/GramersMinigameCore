package org.kcsup.gramersgamecore.arena.sign;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.kcsup.gramersgamecore.arena.Arena;

public class ArenaSign {

    private Location location;
    private Arena arena;
    private Sign sign;

    public ArenaSign(Location location, Arena arena) {
        this.location = location;
        this.arena = arena;

        if(this.location.getBlock() instanceof Sign) {
            sign = (Sign) this.location.getBlock();
            reloadSign();
        }
    }

    public void reloadSign() {
        if(arena == null) return;

        reloadSign(arena.getSignLines());
    }

    public void reloadSign(String[] lines) {
        if(sign == null || lines == null) return;

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
}
