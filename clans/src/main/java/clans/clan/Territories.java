package clans.clan;

import java.util.ArrayList;

import clans.clans;
import clans.clan.utils.Loc2di;
import clans.clan.utils.tile.Tile;
import clans.clan.utils.tile.TileFactory;

public class Territories {
    transient Clan clan;
    private ArrayList<Loc2di> tilesCord = new ArrayList<>();

    public Territories(Clan clan) {
        this.clan = clan;
    }

    public void init() {
        // init all tiles
        if (tilesCord.isEmpty()) {
            return;
        }
        tilesCord.forEach(location -> {
            Tile tile = TileFactory.getTile(location);
            // check if tile is already owned by someone
            if (!tile.getOwner().equals(""))
                return;

            tile.setOwner(clan.getName());

        });

    }

    public ArrayList<Loc2di> getTilesCords() {
        return tilesCord;
    }

    public void setTilesCords(ArrayList<Loc2di> cords) {
        tilesCord = cords;
    }

    public int quantityOfTiles() {
        return tilesCord.size();
    }

    public boolean captureTile(Loc2di location) {
        Tile tile = TileFactory.getTile(location);
        // if tile is already owned by someone else
        if (!tile.getOwner().equals("")) {
            clans.debug("already owned!");
            return false;
        }
        tile.capture(clan.getName());
        tilesCord.add(tile.getLocation());
        clans.debug("captured by " + clan.getName() + ", x:" + tile.getLocation().x + ", y:" + tile.getLocation().y);
        // captured by test, x:10, y:10
        return true;
    }

    public boolean captureTile(Tile tile) {
        // tile is already owned by someone else
        if (!tile.getOwner().equals("")) {
            clans.debug("already owned!");
            return false;
        }
        clans.debug("sucsesful capturing");
        tile.capture(clan.getName());
        tilesCord.add(tile.getLocation());
        return true;
    }

    public void clearTileOwner(Loc2di location) {
        clans.debug("contains loc" + tilesCord.contains(location));
        boolean result = tilesCord.remove(location);

        clans.debug("clearTileOwner: " + result);

        Tile tile = TileFactory.getTile(location);
        tile.setOwner("");
    }
}
