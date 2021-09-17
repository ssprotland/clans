package clans.clan.utils.map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import clans.clan.Clan;
import clans.clan.utils.Loc2di;
import clans.clan.utils.tile.Tile;
import clans.clan.utils.tile.TileFactory;

public class MapRender extends MapRenderer {

    @SuppressWarnings("deprecation")
    public MapRender(Clan clan, MapView mapView) {
        playerClan = clan;

        world = mapView.getWorld();

        scaleShift = mapView.getScale().getValue();

        pixelsPerCallX = 128 / (scaleShift * 3);
        pixelsPerCallY = 128 / (scaleShift * 3);
        lastpixelX = pixelsPerCallX;
        lastpixelY = pixelsPerCallY;
    }

    static NMSHandler nms = new NMSHandler();

    World world;
    int scaleShift;

    // render entire map only first time
    boolean render = true;

    // itterative rendering
    final int pixelsPerCallX;
    final int pixelsPerCallY;
    int lastpixelX;
    int lastpixelY;

    // map is relative to clan
    Clan playerClan;

    // dynamic map renderer that folow's player
    @Override
    public void render(MapView mapView, MapCanvas mapCanvas, Player player) {

        // int scaleShift = 5;
        Location playerLoc = player.getLocation();
        int playerX = playerLoc.getBlockX();
        int playerZ = playerLoc.getBlockZ();

        int cornerX = playerX - (64 << scaleShift);
        int cornerZ = playerZ - (64 << scaleShift);

        for (int pixelX = lastpixelX - pixelsPerCallX; pixelX < lastpixelX; pixelX++) {
            for (int pixelY = lastpixelY - pixelsPerCallY; pixelY < lastpixelY; pixelY++) {

                int worldX = cornerX + (pixelX << scaleShift);
                int worldZ = cornerZ + (pixelY << scaleShift) - 24;

                // render entire map?(do it only one time)
                if (render) {

                    Block highestBlock = world.getHighestBlockAt(worldX, worldZ);

                    // PlayerStorage.debug(Integer.toString(highestBlock.getY()));

                    if (highestBlock.getY() == -1) { // set white color
                        mapCanvas.setPixel(pixelX, pixelY, (byte) (8 * 4 + 2));
                        continue;
                    }
                    mapRender(highestBlock, mapCanvas, pixelX, pixelY);
                }
                titleRender(new Loc2di(worldX, worldZ, ""), mapCanvas, pixelX, pixelY);
            }
        }
        // distribute rendering acros multiple cals
        lastpixelX += pixelsPerCallX;
        if (lastpixelX >= 128 + pixelsPerCallX) {
            lastpixelX = pixelsPerCallX;

            lastpixelY += pixelsPerCallY;
            if (lastpixelY >= 128 + pixelsPerCallY) {
                lastpixelY = pixelsPerCallY;
                render = false;
            }
        }

    }

    void mapRender(Block block, MapCanvas mapCanvas, int X, int Y) {
        mapCanvas.setPixel(X, Y, (byte) nms.getBlockColor(block).getM());
    }

    void titleRender(Loc2di worldLoc, MapCanvas mapCanvas, int x, int y) {

        Tile tile = TileFactory.getTile(worldLoc);

        // if tile is uncaptured
        if (tile.getOwner().equals("")) {
            // least darker color
            mapCanvas.setPixel(x, y, (byte) (mapCanvas.getPixel(x, y) - 2));
            return;
        }
        //if player dont participate in any clan
        if (playerClan == null)
        {
            // most darker color
            mapCanvas.setPixel(x, y, (byte) (mapCanvas.getPixel(x, y) + 1));
            return;
        }
        // if tile is owned by some other clan
        if (!tile.getOwner().equals(playerClan.getName())) {
            // most darker color
            mapCanvas.setPixel(x, y, (byte) (mapCanvas.getPixel(x, y) + 1));
            return;
        }
        // if tile is owned by this clan
        if (tile.getOwner().equals(playerClan.getName())) {
            // bright color
            return;
        }
    }
}
