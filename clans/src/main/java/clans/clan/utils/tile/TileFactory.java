package clans.clan.utils.tile;

import java.util.HashMap;

import org.bukkit.Bukkit;

import clans.clan.utils.Loc;
import clans.clan.utils.Loc2di;

public class TileFactory {
    // ------------------------------------------------------------main world
    public static final Loc tileSize = new Loc(1024.0, 1024.0, 0.0, Bukkit.getWorlds().get(0).getName());
    public static HashMap<Loc2di, Tile> tiles = new HashMap<>();

    public static Tile getTile(Loc2di location) {
        Tile tile;
        Loc2di p1 = new Loc2di();
        Loc2di p2 = new Loc2di();
        p1.world = tileSize.world;
        p2.world = tileSize.world;

        // check if cords lands on midline between tiles.
        // fix this or tile would be damaged
        if (location.x % tileSize.x == 0) {
            location.x += 1;
        }
        if (location.y % tileSize.y == 0) {
            location.y += 1;
        }

        // get smaler coordinate in tile space
        p1.x = (int) Math.floor(location.x / tileSize.x);
        p1.y = (int) Math.floor(location.y / tileSize.y);

        // convert to global cord
        p1.x *= tileSize.x;
        p1.y *= tileSize.y;

        // get larger coordinate in tile space
        p2.x = (int) Math.ceil(location.x / tileSize.x);
        p2.y = (int) Math.ceil(location.y / tileSize.y);

        // convert to global cord
        p2.x *= tileSize.x;
        p2.y *= tileSize.y;

        // calc tile midpoint
        Loc2di midPoint = new Loc2di();
        midPoint.world = tileSize.world;
        midPoint.x = (int) (p1.x + p2.x) / 2;
        midPoint.y = (int) (p1.y + p2.y) / 2;

        // clans.log(midPoint.world);
        // clans.log(midPoint.x + "|" + midPoint.y);
        // clans.log(p1.x + "|" + p1.y);
        // clans.log(p2.x + "|" + p2.y);
        // clans.log(Integer.toString(midPoint.hashCode()));
        // get tile from "database"
        tile = tiles.get(midPoint);

        // if no one requested this tile before
        if (tile == null) {
            // clans.log("new tile!");
            tile = new Tile();
            tile.owner = "";
        }

        tile.p1 = p1;
        tile.p2 = p2;
        tiles.put(midPoint, tile);
        return tile;
    }
}
