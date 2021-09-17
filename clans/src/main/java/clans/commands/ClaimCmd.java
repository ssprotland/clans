package clans.commands;

import dev.jorel.commandapi.CommandAPICommand;

public class ClaimCmd {
    static CommandAPICommand create() {
        CommandAPICommand claim = new CommandAPICommand("claim").executesPlayer((sender, args) -> {
            ClanCmd.claim(sender);
        });

        claim.withSubcommand(new CommandAPICommand("remove").executesPlayer((sender, args) -> {
            ClanCmd.unclaim(sender);
        }));

        return claim;
    }
}
