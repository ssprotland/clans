package clans.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;

public class Control {
    static CommandAPICommand create() {

        CommandAPICommand control = new CommandAPICommand("control").withAliases("ctrl")
                .withPermission(CommandPermission.OP).executes((sender, args) -> {
                    sender.sendMessage("mload- load messages");
                    sender.sendMessage("dload- load clan data");
                    sender.sendMessage("save - save clan data");
                });
        control.withSubcommand(new CommandAPICommand("mload").executes((sender, args) -> {
            ClanCmd.mload(sender);
        }));
        control.withSubcommand(new CommandAPICommand("dload").executes((sender, args) -> {
            ClanCmd.dload(sender);
        }));
        control.withSubcommand(new CommandAPICommand("save").executes((sender, args) -> {
            ClanCmd.save(sender);
        }));
        return control;
    }

}
