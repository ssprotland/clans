package clans.clan.utils.WEWraper;

import java.io.File;
import java.io.FileInputStream;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import clans.clans;

public class WorldWrapper {

    static WorldWrapper instance = new WorldWrapper();

    public class MinMax {
        public int max = 0;
        public int min = 255;

        public int mid() {
            return (max + min) / 2;
        }

        public int diffrence() {
            return max - min;
        }
    }

    public static MinMax minMaxOfRegion(CuboidRegion region) {
        MinMax bounds = getInstance().new MinMax();
        Iterable<BlockVector2> surface = region.asFlatRegion();
        World world = BukkitAdapter.adapt(region.getWorld());
        // find highests and lowest surface levels
        surface.forEach(cord -> {
            int height = getHighestBLOCKYAt(world, cord);
            if (height > bounds.max)
                bounds.max = height;
            if (height < bounds.min)
                bounds.min = height;
        });

        return bounds;
    }

    public static Block getTopBlock(Location loc) {
        World world = loc.getWorld();
        return world.getHighestBlockAt(loc);
    }

    public static int getHighestBLOCKYAt(World world, BlockVector2 cord) {
        Block block = world.getHighestBlockAt(cord.getX(), cord.getZ());

        // get next block if current block is passable and NOT liquid and height not -1
        while (block.isPassable() && (!block.isLiquid()) && (block.getY() > 1)) {
            // get block below until something hard is reached
            block = block.getRelative(BlockFace.DOWN);
        }
        return block.getY();
    }

    public static Block getHighestBLOCKAt(Location loc) {
        World world = loc.getWorld();
        Block block = world.getHighestBlockAt(loc);

        // get next block if current block is passable and NOT liquid and height not -1
        while (block.isPassable() && (!block.isLiquid() && (block.getY() > 1))) {
            // get block below until something hard is reached
            block = block.getRelative(BlockFace.DOWN);
        }
        return block;
    }

    public static ProtectedRegion defineProtectedRegion(com.sk89q.worldedit.world.World world, String name,
            CuboidRegion region) {
        RegionManager regions = clans.regContainer.get(world);
        // check if region already exist
        ProtectedRegion claim = regions.getRegion(name);
        // remove if exist
        if (claim != null) {
            regions.removeRegion(claim.getId());
        }

        // claim
        claim = new ProtectedCuboidRegion(name, region.getPos1(), region.getPos2());
        // save
        regions.addRegion(claim);
        return claim;
    }

    public static boolean doesRegionExist(com.sk89q.worldedit.world.World world, String name) {
        RegionManager regions = clans.regContainer.get(world);
        // check if region already exist
        ProtectedRegion claim = regions.getRegion(name);
        // remove if exist
        if (claim == null) {
            return false;
        }
        return true;
    }

    public static void regenerate(com.sk89q.worldedit.world.World world, CuboidRegion region) {
        EditSession editSession = WorldEdit.getInstance().newEditSession(world);
        region.getWorld().regenerate(region, editSession);
        editSession.close();
    }

    public static boolean regionContainsRegions(com.sk89q.worldedit.world.World world, ProtectedRegion region) {
        RegionManager regions = clans.regContainer.get(world);
        ApplicableRegionSet set = regions.getApplicableRegions(region);
        return set.size() != 1;
    }

    public static boolean regionContainsPoint(ProtectedRegion region, Location loc) {
        return region.contains(loc.getBlockX(), (int) loc.getY(), loc.getBlockZ());
    }

    public static void pasteFromFile(String fileName, BlockVector3 point, com.sk89q.worldedit.world.World world) {
        // spawn castle
        Clipboard clipboard;
        // load from file
        File file = new File(clans.getInstance().getDataFolder(), fileName);
        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            clipboard = reader.read();
            // paste
            EditSession editSession = WorldEdit.getInstance().newEditSession(world);

            Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(point).build();

            Operations.complete(operation);
            editSession.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static WorldWrapper getInstance() {
        return instance;
    }

}
