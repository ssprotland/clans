package clans.clan.utils;

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
        return x + y + world.hashCode();
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /*
         * Check if o is an instance of Loc2di or not "null instanceof [type]" also
         * returns false
         */
        if (!(o instanceof Loc2di)) {
            return false;
        }

        // typecast o to Loc2di so that we can compare data members
        Loc2di c = (Loc2di) o;

        // Compare the data members and return accordingly
        return (x == c.x) && (y == c.y) && (world.equals(c.world));
    }
}
