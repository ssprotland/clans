package clans.clan.utils;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Loc2di {

    public int x;
    public int y;
    public String world;

    public Loc2di() {
        this.x = 0;
        this.y = 0;
        this.world = "";
    }

    public Loc2di(int x, int y, String world) {
        this.x = x;
        this.y = y;
        this.world = world;
    }

    public Loc2di(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockZ();
        this.world = loc.getWorld().getName();
    }

    public Location toLocation() {
        Location loc = new Location(Bukkit.getWorld(this.world), this.x, 0, this.y);
        return loc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, world);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Loc2di)) {
            return false;
        }
        Loc2di loc2di = (Loc2di) o;
        return x == loc2di.x && y == loc2di.y && Objects.equals(world, loc2di.world);
    }
}
