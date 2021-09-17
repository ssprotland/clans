package clans.clan;

import java.util.List;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.RemovalStrategy;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;

import clans.clans;

public class Claim {
    private String name;
    private String world;

    public Claim() {
        this.name = "";
        this.world = "";
    }

    public String getName() {
        return this.name;
    }

    public String getWorld() {
        return this.world;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Boolean exist() {
        if (name.equals("") && world.equals(""))
            return false;
        return true;
    }

    Boolean addPlayer(String player) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        if (region == null) {
            return false;
        }
        DefaultDomain regMembers = region.getMembers();
        regMembers.addPlayer(player);
        return true;
    }

    Boolean addPlayerBulk(List<String> players) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        if (region == null) {
            return false;
        }
        DefaultDomain regMembers = region.getMembers();
        for (String player : players) {
            regMembers.addPlayer(player);
        }
        return true;
    }

    Boolean addOwner(String player) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        if (region == null) {
            return false;
        }
        DefaultDomain regOwners = region.getOwners();
        regOwners.addPlayer(player);
        return true;
    }

    Boolean addOwnerBulk(List<String> players) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        if (region == null) {
            return false;
        }
        DefaultDomain regOwners = region.getOwners();
        for (String player : players) {
            regOwners.addPlayer(player);
        }
        return true;
    }

    boolean removePlayer(String player) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        if (region == null) {
            return false;
        }
        DefaultDomain regMembers = region.getMembers();
        regMembers.removePlayer(player);
        return true;
    }

    boolean removeOwner(String player) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        if (region == null) {
            return false;
        }
        DefaultDomain regOwners = region.getOwners();
        regOwners.removePlayer(player);
        return true;
    }

    ProtectedRegion getClaim() {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if exist
        ProtectedRegion region = regions.getRegion(name);
        return region;

    }

    boolean create(BlockVector3 point1, BlockVector3 point2, String world, String clanName) {
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));
        // check if region already exist
        ProtectedRegion region = regions.getRegion("clan_" + clanName);
        if (region != null)
            return false;

        // set name
        this.setName("clan_" + clanName);
        this.setWorld(world);
        // claim
        region = new ProtectedCuboidRegion(this.getName(), point1, point2);
        // save
        regions.addRegion(region);

        return true;
    }

    Boolean remove() {
        if (!exist()) {
            return false;
        }
        RegionManager regions = clans.regContainer.get(BukkitAdapter.adapt(Bukkit.getWorld(getWorld())));
        // check if region exist
        ProtectedRegion region = regions.getRegion(this.getName());
        if (region == null)
            return false;
        // remove region
        regions.removeRegion(this.getName(), RemovalStrategy.UNSET_PARENT_IN_CHILDREN);
        // clear names

        this.setName("");
        this.setWorld("");
        return true;
    }
}
