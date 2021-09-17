package clans.commands;

import clans.event.Capturing;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public class TestCmd {
    static CommandAPICommand create() {
        CommandAPICommand claim = new CommandAPICommand("test").withPermission(CommandPermission.fromString("tester"));
        claim.withSubcommand(new CommandAPICommand("get-map").executesPlayer((sender, args) -> {
            ClanCmd.getMap(sender,3);
        }));

        claim.withSubcommand(new CommandAPICommand("initCapturing").executes((sender, args) -> {
            Capturing.capturing();
        }));

        return claim;
    }

}
