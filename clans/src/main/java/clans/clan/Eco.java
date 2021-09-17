package clans.clan;

import org.bukkit.entity.Player;

import clans.clans;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Eco {
    double balance;

    public Eco() {
        this.balance = 0;
    }

    // ==================================================================================================
    public double getBalance() {
        return balance;
    }

    public String getBallanceString() {
        Economy economy = clans.mainInstance.getEconomy();
        return economy.format(balance);
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void substract(Double money) {
        balance -= money;
    }

    public void add(Double money) {
        balance += money;
    }

    // ==================================================================================================
    public boolean withdraw(Player player, double ammount) {
        Economy economy = clans.mainInstance.getEconomy();
        // check if clan has enought money
        if (balance < ammount) {
            return false;
        }
        // withdraw from player
        EconomyResponse resp = economy.depositPlayer(player, ammount);
        // if smtx went wrong, just return
        if (!resp.transactionSuccess()) {
            return false;
        }
        // deposit to clan
        balance -= ammount;
        return true;
    }

    public boolean deposit(Player player, double ammount) {
        Economy economy = clans.mainInstance.getEconomy();
        // withdraw from player
        EconomyResponse resp = economy.withdrawPlayer(player, ammount);
        // if smtx went wrong, just return
        if (!resp.transactionSuccess()) {
            return false;
        }
        // deposit to clan
        balance += ammount;
        return true;
    }
    // ==================================================================================================

}
