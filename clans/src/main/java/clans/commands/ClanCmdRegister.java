package clans.commands;

import java.util.Set;

import org.bukkit.entity.Player;

import clans.clans;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;

public class ClanCmdRegister {
    public static void register() {
        CommandAPICommand clan = new CommandAPICommand("clan").withAliases("c").executes((sender, args) -> {
            ClanCmd.help(sender);
        });

        CommandAPICommand help = new CommandAPICommand("help").executes((sender, args) -> {
            ClanCmd.help(sender);
        });
        CommandAPICommand list = new CommandAPICommand("list").executes((sender, args) -> {
            ClanCmd.list(sender);
        });

        CommandAPICommand delete = new CommandAPICommand("delete").executesPlayer((sender, args) -> {
            ClanCmd.delete(sender);
        });

        CommandAPICommand deleteWArg = new CommandAPICommand("delete").withArguments(new StringArgument("yes/no"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.delete(sender, (String) args[0]);
                });

        CommandAPICommand deleteCli = new CommandAPICommand("delete").withArguments(new StringArgument("clan"))
                .executes((sender, args) -> {
                    ClanCmd.delete(sender, (String) args[0]);
                });

        CommandAPICommand setHome = new CommandAPICommand("sethome").executesPlayer((sender, args) -> {
            ClanCmd.sethome(sender);
        });

        CommandAPICommand home = new CommandAPICommand("home").executesPlayer((sender, args) -> {
            ClanCmd.tpHome(sender);
        });

        CommandAPICommand invite = new CommandAPICommand("invite").withArguments(new PlayerArgument("player"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.invite(sender, (Player) args[0]);
                });

        CommandAPICommand accept = new CommandAPICommand("accept").executesPlayer((sender, args) -> {
            ClanCmd.accept(sender);
        });

        CommandAPICommand deny = new CommandAPICommand("deny").executesPlayer((sender, args) -> {
            ClanCmd.deny(sender);
        });

        CommandAPICommand kick = new CommandAPICommand("kickoff").withArguments(new StringArgument("player"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.kick(sender, (String) args[0]);
                });

        CommandAPICommand kickPlayer = new CommandAPICommand("kick").withArguments(new PlayerArgument("player"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.kick(sender, (Player) args[0]);
                });

        CommandAPICommand pvp = new CommandAPICommand("pvp").executesPlayer((sender, args) -> {
            ClanCmd.togglePvp(sender);
        });

        CommandAPICommand create = new CommandAPICommand("create").withArguments(new StringArgument("name"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.create(sender, (String) args[0]);
                });

        CommandAPICommand leave = new CommandAPICommand("leave").executesPlayer((sender, args) -> {
            ClanCmd.leave(sender);
        });

        CommandAPICommand info = new CommandAPICommand("info").executesPlayer((sender, args) -> {
            ClanCmd.info(sender);
        });

        CommandAPICommand infoCli = new CommandAPICommand("info")
                .withArguments(
                        new StringArgument("clan").replaceSuggestions(infoarg -> convert(clans.clanList.keySet())))
                .executes((sender, args) -> {
                    ClanCmd.info(sender, (String) args[0]);
                });

        CommandAPICommand promote = new CommandAPICommand("promote").withArguments(new PlayerArgument("player"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.raise(sender, (Player) args[0]);
                });

        CommandAPICommand demote = new CommandAPICommand("demote").withArguments(new PlayerArgument("player"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.lower(sender, (Player) args[0]);
                });

        CommandAPICommand eco = EcoCmd.create();
        CommandAPICommand claim = ClaimCmd.create();
        CommandAPICommand relations = RelationsCmd.create();
        CommandAPICommand test = TestCmd.create();
        CommandAPICommand tile = TileCmd.create();

        CommandAPICommand control = Control.create();
        clan.withSubcommand(help)// help
                .withSubcommand(list)// list
                .withSubcommand(delete).withSubcommand(deleteWArg).withSubcommand(deleteCli) // delete
                .withSubcommand(setHome).withSubcommand(home)// home
                .withSubcommand(invite).withSubcommand(accept).withSubcommand(deny)// invite
                .withSubcommand(kick).withSubcommand(kickPlayer)// kick
                .withSubcommand(pvp)// pvp
                .withSubcommand(create)// create clan
                .withSubcommand(leave)// leave clan
                .withSubcommand(info).withSubcommand(infoCli)// info about clan
                .withSubcommand(promote).withSubcommand(demote)// player hierarchy
                .withSubcommand(eco)// economy
                .withSubcommand(claim)// claim
                .withSubcommand(relations)// relations
                .withSubcommand(control)// save/load
                .withSubcommand(test)// testing featuress
                .withSubcommand(tile)// tile
                .register();

    }

    public static String[] convert(Set<String> setOfString) {

        // Create String[] of size of setOfString
        String[] arrayOfString = new String[setOfString.size()];

        // Copy elements from set to string array
        // using advanced for loop
        int index = 0;
        for (String str : setOfString)
            arrayOfString[index++] = str;

        // return the formed String[]
        return arrayOfString;
    }
}
