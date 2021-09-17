package clans.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.DoubleArgument;

public class EcoCmd {
    static CommandAPICommand create() {
        CommandAPICommand eco = new CommandAPICommand("economy").withAliases("eco").executesPlayer((sender, args) -> {
            sender.sendMessage("/clan eco deposit  - перевести деньги на счет клана");
            sender.sendMessage("/clan eco withdraw - Снять средства со счета клана");
        });

        eco.withSubcommand(new CommandAPICommand("deposit").withArguments(new DoubleArgument("ammount"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.deposit(sender, (Double) args[0]);
                }));

        eco.withSubcommand(new CommandAPICommand("withdraw").withArguments(new DoubleArgument("ammount"))
                .executesPlayer((sender, args) -> {
                    ClanCmd.withdraw(sender, (Double) args[0]);
                }));
        eco.withSubcommand(new CommandAPICommand("balance").withAliases("bal").executesPlayer((sender, args) -> {
            ClanCmd.balance(sender);
        }));

        return eco;
    }

}
