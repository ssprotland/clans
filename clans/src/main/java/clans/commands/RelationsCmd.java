package clans.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;

public class RelationsCmd {
    static CommandAPICommand create() {
        CommandAPICommand relations = new CommandAPICommand("relations").withAliases("rel")
                .executesPlayer((sender, args) -> {
                    sender.sendMessage("/Clan relations friend add <клан>     - добавить другой клан в друзья ");
                    sender.sendMessage("/Clan relations friend remove <клан>  - удалить другой клан из друзей");
                    sender.sendMessage(
                            "/Clan relations friend accept <клан>  - Принять приглашение дружбы другого клана");
                    sender.sendMessage(
                            "/Clan relations friend decline <клан> - Отклонить приглашение дружбы другого клана");
                    sender.sendMessage("/Clan relations enemy add <клан>      - Добавить враждебный клан");
                    sender.sendMessage("/Clan relations enemy remove <клан>   - Удалить враждебный клан");
                });

        CommandAPICommand friend = new CommandAPICommand("friend").withAliases("fr")
                .withSubcommand(new CommandAPICommand("add").withArguments(new StringArgument("clan"))
                        .executesPlayer((sender, args) -> {
                            ClanCmd.addFriend(sender, (String) args[0]);
                        }))
                .withSubcommand(new CommandAPICommand("remove").withArguments(new StringArgument("clan"))
                        .executesPlayer((sender, args) -> {
                            ClanCmd.removeFriend(sender, (String) args[0]);
                        }))
                .withSubcommand(new CommandAPICommand("accept")
                        .executesPlayer((sender, args) -> {
                            ClanCmd.acceptFriend(sender);
                        }))
                .withSubcommand(new CommandAPICommand("decline")
                        .executesPlayer((sender, args) -> {
                            ClanCmd.declineFriendship(sender);
                        }));

        CommandAPICommand enemy = new CommandAPICommand("enemy").withAliases("en")
                .withSubcommand(new CommandAPICommand("add").withArguments(new StringArgument("clan"))
                        .executesPlayer((sender, args) -> {
                            ClanCmd.addEnemy(sender, (String) args[0]);
                        }))
                .withSubcommand(new CommandAPICommand("remove").withArguments(new StringArgument("clan"))
                        .executesPlayer((sender, args) -> {
                            ClanCmd.removeEnemy(sender, (String) args[0]);
                        }));
        relations.withSubcommand(friend).withSubcommand(enemy);
        return relations;
    }
}
