package clans.event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Map.Entry;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import clans.clans;
import clans.clan.Clan;
import clans.clan.utils.Loc2di;
import clans.clan.utils.WEWraper.WorldWrapper;
import clans.clan.utils.WEWraper.WorldWrapper.MinMax;
import clans.clan.utils.tile.Tile;
import clans.clan.utils.tile.TileFactory;
import clans.configuration.Config;
import clans.storage.ClanCell;
import playerstoragev2.PlayerStorage;

public class Capturing {
    // create reapiting task at 6 o'clock
    public static Tile capturingTile;

    private static int timer = Config.firstStageMaxTime;
    private static Random r = new Random();

    // use only one boss bar (cuz minecraft stores all created boss bars)
    private static BossBar bar = Bukkit.createBossBar("", BarColor.RED, BarStyle.SOLID);

    public static void init() {
        r.setSeed(System.currentTimeMillis());
        scheduleRepeatAtTime(clans.getInstance(), new Runnable() {
            public void run() {
                capturing();
            }
        }, 18); // at 18 o'clock every day
        payDay();
    }

    public static void capturing() {
        boolean newTerritory = true;
        // start tile capturing event
        // find tile to capture
        Tile tile;
        // find place where to spawn castle
        Location castleLoc;
        MinMax bounds;
        CuboidRegion castleRegion;
        ProtectedRegion castleClaim;
        int scattering = 10;// how far away from midpoint of tile castle can spawn
        do {
            // generate tile
            tile = TileFactory.getTile(new Loc2di(findTileLoctaion()));
            // find place where to spawn castle
            castleLoc = tile.getLocation().toLocation();

            int maxHeightDifference = Config.maxHeightDifference - 1;
            // find apropriate place (low diffrence in min to max distance and no lava)
            do {
                Block block;
                do {
                    castleLoc = castleLoc.add(getRandomNumber(-scattering, scattering), 0d,
                            getRandomNumber(-scattering, scattering));
                    block = WorldWrapper.getTopBlock(castleLoc);
                    scattering += 10; // increase scattering, to increase possible teritory
                } while (block.getType() == Material.LAVA);

                // create XxY region
                BlockVector3 p1 = BlockVector3.at(castleLoc.getX() - (Config.castleSizeX / 2), 0,
                        castleLoc.getZ() - (Config.castleSizeY / 2));
                BlockVector3 p2 = BlockVector3.at(castleLoc.getX() + (Config.castleSizeX / 2), 255,
                        castleLoc.getZ() + (Config.castleSizeY / 2));
                castleRegion = new CuboidRegion(BukkitAdapter.adapt(Bukkit.getWorlds().get(0)), p1, p2);
                // find highest and lowest point in region
                bounds = WorldWrapper.minMaxOfRegion(castleRegion);
                maxHeightDifference++;// increase chance of finding place
            } while ((bounds.diffrence() > maxHeightDifference));
            // define private
            castleRegion.expand(BlockVector3.at(0, 255, 0), BlockVector3.at(0, -255, 0));
            String name = "tile_" + tile.getLocation().x + "," + tile.getLocation().y; // tile:8,12

            if (WorldWrapper.doesRegionExist(castleRegion.getWorld(), name)) {
                debug("region exist!");
                castleClaim = WorldWrapper.defineProtectedRegion(castleRegion.getWorld(), name, castleRegion);
                // calculate new clastle location
                // (max+min)/2
                castleLoc = BukkitAdapter.adapt(BukkitAdapter.adapt(castleRegion.getWorld()),
                        castleClaim.getMaximumPoint().add(castleClaim.getMinimumPoint()).divide(2));
                newTerritory = false;
                break;
            } else {
                castleClaim = WorldWrapper.defineProtectedRegion(castleRegion.getWorld(), name, castleRegion);
            }

            // while castle region overlap another region
        } while (WorldWrapper.regionContainsRegions(castleRegion.getWorld(), castleClaim));
        // debug("region ok!");
        // create final variable

        // set some region flags
        // pvp true
        // no block placing or breaking
        castleClaim.setFlag(Flags.PVP, State.ALLOW);
        castleClaim.setFlag(Flags.BLOCK_BREAK, State.DENY);
        castleClaim.setFlag(Flags.BLOCK_PLACE, State.DENY);
        castleClaim.setFlag(Flags.USE, State.ALLOW);
        if (newTerritory) {
            debug("new teritory!");
            // clean up territory and make platform
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(castleRegion.getWorld())) {

                // set everything above midpoint of bound's to air
                // create region form mid to max
                CuboidRegion editReg = new CuboidRegion(castleRegion.getWorld(),
                        castleRegion.getPos1().withY(bounds.mid()), castleRegion.getPos2().withY(bounds.max));
                // get block
                Block block = castleLoc.getWorld().getHighestBlockAt(castleLoc);
                block.setType(Material.AIR);
                // fill all above with it
                editSession.setBlocks(editReg, BukkitAdapter.adapt(block.getBlockData()));

                // set everything belove midpoint of bound's to material of mid block
                // create regioin from min to mid
                editReg = new CuboidRegion(castleRegion.getWorld(), castleRegion.getPos1().withY(bounds.min),
                        castleRegion.getPos2().withY(bounds.mid()));

                // get midle block that contains some soil (if not then force it to contain that
                block = WorldWrapper.getHighestBLOCKAt(castleLoc);
                if (block.isLiquid())
                    block.setType(Material.STONE);
                // fill
                editSession.setBlocks(editReg, BukkitAdapter.adapt(block.getBlockData()));
                editSession.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
            debug("castle loc: ",
                    BukkitAdapter.adapt(BukkitAdapter.adapt(castleRegion.getWorld()), castleRegion.getMinimumPoint()));

            WorldWrapper.pasteFromFile("castle/castle.schem", castleRegion.getMinimumPoint().withY(bounds.mid() + 1),
                    castleRegion.getWorld());
        }
        // initiate first stage of capturing process
        capturingFirstStage(castleLoc, castleClaim);

    }

    static int scheduleRepeatAtTime(Plugin plugin, Runnable task, int hour) {
        Calendar cal = Calendar.getInstance();

        long now = cal.getTimeInMillis();

        if (cal.get(Calendar.HOUR_OF_DAY) >= hour)
            cal.add(Calendar.DATE, 1); // do it tomorrow if now is after "hours"

        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        long offset = cal.getTimeInMillis() - now;
        long ticks = offset / 50L; // there are 50 milliseconds in a tick

        return Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, task, ticks, 1728000L);
        // 24 hrs/day * 60 mins/hr * 60 secs/min * 20 ticks/sec = 1728000 ticks
    }

    static void capturingFirstStage(Location castleLoc, ProtectedRegion castleClaim) {
        // set what tile is beeing captured
        capturingTile = TileFactory.getTile(new Loc2di(castleLoc));

        // send message about tile capturing
        Loc2di castleLocation = new Loc2di(castleLoc);
        Bukkit.broadcastMessage(ChatColor.RED + "???????? ?????????? ?????? ??????????????!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "????????????????????: " + castleLocation.x + "," + castleLocation.y + "!");
        Bukkit.broadcastMessage(ChatColor.GREEN + "?????????? ??????????????????????????????????, ????????: /clan tile teleport!");

        // initiate first stage of capturing process
        // update timer
        timer = Config.firstStageMaxTime;
        // create bossbar with tile cords
        bar.setTitle(
                ChatColor.GOLD + "???????? ?????????? ?????? ??????????????! ????????????????????: " + castleLocation.x + "," + castleLocation.y);
        // show this title to all players on server
        Bukkit.getOnlinePlayers().forEach(player -> {
            bar.addPlayer(player);
        });
        bar.setProgress(1);// full bar
        bar.setVisible(true);
        // update timer
        timer = Config.firstStageMaxTime;
        // create task to update bar (timer) until somebody will enter the castle, or
        // until timer runs out
        new BukkitRunnable() {
            @Override
            public void run() {
                bar.setProgress(((double) timer / (double) Config.firstStageMaxTime));
                timer -= 1;
                // if timer run out
                if (timer <= 0) {
                    // disable BossBar and this task
                    bar.setVisible(false);
                    bar.removeAll();
                    this.cancel();
                    Bukkit.broadcastMessage(ChatColor.GOLD + "???????? ?????????????????? ?????????????????????? ??????????????????!");
                }
                // check if someone is in castle
                // TO DO:
                Iterator<? extends Player> iterator = Bukkit.getOnlinePlayers().iterator();
                while (iterator.hasNext()) {

                    Player player = iterator.next();
                    // if someone is in castle (castle claim)
                    if (WorldWrapper.regionContainsPoint(castleClaim, player.getLocation())) {
                        // get clan of player that enters the castle
                        Clan clan = getPlayerClan(player, true);
                        // if player do not participate in any clan
                        if (clan == null) {
                            continue;
                        }
                        // exit from while loop, and initiate second stage of capturing.
                        // disable BossBar and this task
                        bar.setVisible(false);
                        bar.removeAll();
                        this.cancel();
                        capturingSecondStage(castleLoc, castleClaim, clan);
                        break;
                    }
                }
            }
        }.runTaskTimer(clans.getInstance(), 0L, 20L);// execute every second
    }

    static void capturingSecondStage(Location castleLoc, ProtectedRegion castleClaim, Clan firstClan) {
        HashMap<String, Integer> clanlist = new HashMap<String, Integer>();// clans whos members are in castle
        Loc2di castleLocation = new Loc2di(castleLoc); // just to print int
        // message about second stage
        Bukkit.broadcastMessage(ChatColor.RED + "???????? ?????? ???????????????? ???????????? " + firstClan.getName() + "!");
        Bukkit.broadcastMessage(ChatColor.GOLD + "????????????????????: " + castleLocation.x + "," + castleLocation.y + "!");
        Bukkit.broadcastMessage(ChatColor.GREEN + "?????????? ??????????????????????????????????, ????????: /clan tile teleport!");
        // create new bossbar
        // create bossbar with tile cords and clan that is capturing it

        bar.setTitle(ChatColor.GOLD + "???????? ?????? ???????????????? ???????????? " + firstClan.getName() + "! ????????????????????: "
                + castleLocation.x + "," + castleLocation.y);
        // show this title to all players on server
        Bukkit.getOnlinePlayers().forEach(player -> {
            bar.addPlayer(player);
        });
        bar.setProgress(1);// full bar
        bar.setVisible(true);
        // task to update bossbar timer and clan name
        timer = Config.secondStageMaxTime;

        new BukkitRunnable() {
            String lastClanName = firstClan.getName();
            Clan capturingClan = firstClan; // clan that capture castle

            @Override
            public void run() {

                // update timer
                bar.setProgress((double) timer / (double) Config.secondStageMaxTime);
                timer -= 1;
                // spawn zombie
                // every 15 sec
                if ((timer % 15 == 0) && Config.addZombie) {
                    debug("zombie spawn");
                    Location zombieLoc = new Location(castleLoc.getWorld(),
                            getRandomNumber(castleLoc.getBlockX() - Config.zombieSpawnRadius,
                                    castleLoc.getBlockX() + Config.zombieSpawnRadius),
                            0, getRandomNumber(castleLoc.getBlockZ() - Config.zombieSpawnRadius,
                                    castleLoc.getBlockZ() + Config.zombieSpawnRadius));
                    zombieLoc.setY(
                            zombieLoc.getWorld().getHighestBlockYAt(zombieLoc.getBlockX(), zombieLoc.getBlockZ()) + 2);
                    debug("zombie loc", zombieLoc);
                    zombieLoc.getWorld().spawn(zombieLoc, Zombie.class);

                }
                // if timer run out
                if (timer <= 0) {
                    // disable BossBar and this task
                    Tile tile = TileFactory.getTile(new Loc2di(castleLoc));
                    capturingTile = null;
                    bar.setVisible(false);
                    bar.removeAll();
                    this.cancel();
                    // check if tile is captured by some clan
                    if (capturingClan == null) {
                        Bukkit.broadcastMessage(ChatColor.RED + "???????? ???? ?????? ?????????? ????????????????!");
                        return;
                    }
                    // assign tile to capturingClan
                    // if tile was previosly captured by other clan
                    if (!capturingClan.Territories().captureTile(tile)) {
                        clans.debug("already captured");
                        // uncapture
                        Clan lastClan = clans.clanList.get(tile.getOwner());

                        debug(lastClan.getName());
                        debug(lastClan.Territories().getTilesCords());

                        lastClan.Territories().clearTileOwner(tile.getLocation());
                        // something is WERY wrong
                        if (!capturingClan.Territories().captureTile(tile)) {
                            Bukkit.broadcastMessage("???????????????????? ????????????. ????????????????????, ???????????????? ???? ???????? ??????????????????????????????!");
                            return;
                        }
                    }
                    Bukkit.broadcastMessage(
                            ChatColor.RED + "???????? ?????? ???????????????? ?? ?????????????? ?????????? " + capturingClan.getName() + "!");
                    return;
                }
                // assign castle to clan, whos players is presended more than others in castle
                Iterator<? extends Player> iterator = Bukkit.getOnlinePlayers().iterator();
                while (iterator.hasNext()) {

                    Player player = iterator.next();
                    // if someone is in castle (castle claim)
                    if (WorldWrapper.regionContainsPoint(castleClaim, player.getLocation())) {
                        // get clan of player that enters the castle
                        Clan clan = getPlayerClan(player, true);
                        // if player do not participate in any clan
                        if (clan == null) {
                            continue;
                        }
                        // add 1 to clan in hashmap of clans (or create new record)
                        Integer members = clanlist.get(clan.getName());
                        if (members == null)
                            members = 0;
                        clanlist.put(clan.getName(), members + 1);
                    }
                }
                // decide which clan capture the castle
                String clanName = getLarger(clanlist);
                // if name are difrent and isn't empty
                if ((!lastClanName.equals(clanName)) && (!clanName.equals("")) && Config.rememberLastEnterance) {
                    debug("capture update!");
                    debug(clanName);
                    // update last name
                    lastClanName = clanName;
                    capturingClan = clans.clanList.get(clanName); // get clan by name
                    debug(capturingClan.getName());
                    // update title
                    bar.setTitle(ChatColor.GOLD + "???????? ?????? ???????????????? ???????????? " + capturingClan.getName()
                            + "! ????????????????????: " + castleLocation.x + "," + castleLocation.y);
                } else {
                    debug("capture update 2!");
                    debug(clanName);
                    capturingClan = clans.clanList.get(clanName);
                    // if no one is in castle
                    if (capturingClan == null) {
                        bar.setTitle(ChatColor.GOLD + "???????? ????????????????! ????????????????????: " + castleLocation.x + ","
                                + castleLocation.y);
                    } else {
                        // update title
                        bar.setTitle(ChatColor.GOLD + "???????? ?????? ???????????????? ???????????? " + capturingClan.getName()
                                + "! ????????????????????: " + castleLocation.x + "," + castleLocation.y);
                    }

                }
                // clear calnlist
                clanlist.clear();

            }
        }.runTaskTimer(clans.getInstance(), 0L, 20L);// execute every second

    }

    // get string that represents largest integer
    static String getLarger(HashMap<String, Integer> hashmap) {
        int max = 0;
        String largest = "";

        for (Entry<String, Integer> entry : hashmap.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                largest = entry.getKey();
            }
        }
        return largest;
    }

    static double getRandomNumber(int min, int max) {
        return (r.nextDouble() * (max - min)) + min;
    }

    static void debug(Location loc) {
        clans.debug(loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
    }

    static void debug(ArrayList<Loc2di> locations) {
        locations.forEach(location -> {
            debug(location);
        });
    }

    static void debug(String msg) {
        clans.debug(msg);
    }

    static void debug(String msg, int num) {
        clans.debug(msg + num);
    }

    static void debug(String prefix, Location loc) {
        clans.debug(prefix + loc.getX() + "|" + loc.getY() + "|" + loc.getZ());
    }

    static void debug(Loc2di loc) {
        clans.debug(loc.x + "|" + loc.y + "|" + loc.world);
    }

    static Clan getPlayerClan(Player player, Boolean printmsg) {
        // get player and check if player exist
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");

        // check if player has clan
        if (playerC.member.equals("")) {
            if (printmsg) {
                player.sendMessage("???? ???????????? ???????? ?? ?????????? ?????????? ?????????????? ?????????????? ?? ??????????????!");
            }

            return null;
        }
        return clans.clanList.get(playerC.member);
    }

    static Location findTileLoctaion() {

        Location spawnpoint = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
        Location tileLoc;
        // find location of tile that will be enought far away from spawn point
        do {
            tileLoc = new Location(spawnpoint.getWorld(),
                    getRandomNumber(Config.minTileDistance, Config.maxTileDistance), 0d,
                    getRandomNumber(Config.minTileDistance, Config.maxTileDistance));
        } while (spawnpoint.distance(tileLoc) < Config.distanceToSpawnPoint);

        debug(tileLoc);
        return tileLoc;
    }

    // pay money to each clan
    public static void payDay() {

        scheduleRepeatAtTime(clans.getInstance(), new Runnable() {
            public void run() {
                // add money to each clan respectivly to tile count
                clans.clanList.forEach((name, clan) -> {
                    clan.Economy().add((double) (clan.Territories().quantityOfTiles() * 20));
                });

            }
        }, 0); // at 0 o'clock every day
    }
}
