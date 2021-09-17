package clans.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import clans.clan.utils.tile.Tile;
import clans.clan.utils.tile.TileFactory;
import clans.event.Capturing;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;

public class TileCmd {
    static CommandAPICommand create() {
        CommandAPICommand tileCmd = new CommandAPICommand("tile");

        // teleport to border of capturing tile
        tileCmd.withSubcommand(new CommandAPICommand("tp").executesPlayer((player, args) -> {

            Tile tile = Capturing.capturingTile;

            if (tile == null) {
                // todo no tile is being captured now!
                player.sendMessage(ChatColor.RED + "Не один тайл не захватывается!");
                return;
            }

            Location location = new Location(Bukkit.getWorld(tile.getLocation().world), tile.getLocation().x, 0,
                    tile.getLocation().y);

            // *get random number from 1 to 4 (that represents direction) <- -> /\ \/
            int rnd = (int) Math.round((Math.random() * (4 - 1)) + 1);
            //
            if (rnd == 1) {
                location.add(TileFactory.tileSize.x, 0, 0);
            }
            if (rnd == 2) {
                location.add(-TileFactory.tileSize.x, 0, 0);
            }
            if (rnd == 3) {
                location.add(0, 0, TileFactory.tileSize.y);
            }
            if (rnd == 4) {
                location.add(0, 0, -TileFactory.tileSize.y);
            }
            location.add(0.5, 1, 0.5);//to teleport player at block center;
            location.setY(Bukkit.getWorld(tile.getLocation().world).getHighestBlockYAt((int) location.getX(),
                    (int) location.getY()));
            player.teleport(location);

        }));

        tileCmd.withSubcommand(new CommandAPICommand("map")
                .withArguments(new MultiLiteralArgument("close", "medium", "far")).executesPlayer((sender, args) -> {
                    int zoom;
                    switch ((String) args[0]) {
                        case "close":
                            zoom = 1;
                            break;
                        case "medium":
                            zoom = 2;
                            break;
                        case "far":
                            zoom = 3;
                            break;
                        default:
                            zoom = 1;
                            break;
                    }
                    ClanCmd.getMap(sender, zoom);
                }));

        return tileCmd;
    }
}
