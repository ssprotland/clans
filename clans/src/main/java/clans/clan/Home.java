package clans.clan;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import clans.clan.utils.Loc;

public class Home {

    private Loc location;

    public Home() {
        location = new Loc();
    }

    public void setHome(Location newHome) {

        location.x = newHome.getX();
        location.y = newHome.getY();
        location.z = newHome.getZ();
        location.world = newHome.getWorld().getName();
    }

    public void setHome(Loc newHome) {

        location = newHome;
    }

    public Location getHome() {
        if (location.world.equals("")) {
            return null;
        }
        Location loc = new Location(Bukkit.getWorld(location.world), location.x, location.y, location.z);
        return loc;
    }

    public Loc getHomeL() {
        return location;
    }

    public void delHome() {
        location = null;

    }

    public boolean exist() {
        return !location.world.equals("");
    }
}
