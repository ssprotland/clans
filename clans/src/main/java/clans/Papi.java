package clans;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import clans.clan.Clan;
import clans.storage.ClanCell;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.clip.placeholderapi.expansion.Relational;
import playerstoragev2.PlayerStorage;
import playerstoragev2.storage.PlayerS;

public class Papi extends PlaceholderExpansion implements Relational {

    @Override
    public @NotNull String getAuthor() {
        return "vlad_mod";
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clans";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) {
            return "";
        }

        // %clans_name%
        if (identifier.equals("name")) {
            //player existance check
            PlayerS players1 = PlayerStorage.getOnlinePlayer(player.getName());
            if (players1 == null) {
                return "";
            }
            
            ClanCell Ccell = (ClanCell) players1.getStorageCell("clans");
            if (Ccell == null) {
                return "";
            }
            return Ccell.member;
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%)
        // was provided
        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player2, Player player1, String identifier) {

        if (player1 == null) {
            return "";
        }
        if (player2 == null) {
            return "";
        }
        // clans.log(player1.getName());
        // clans.log(player2.getName());

        if (identifier.equals("name")) {
            PlayerS players1 = PlayerStorage.getOnlinePlayer(player1.getName());
            PlayerS players2 = PlayerStorage.getOnlinePlayer(player2.getName());
            // check if player is loaded or not
            if (players1 == null || players2 == null) {
                return "";
            }

            ClanCell Ccell1 = (ClanCell) players1.getStorageCell("clans");
            ClanCell Ccell2 = (ClanCell) players2.getStorageCell("clans");
            // check if player is in clan
            if (Ccell1.member.equals("")) {
                return "";
            }
            // if in same clan
            if (Ccell1.member.equals(Ccell2.member)) {
                return ChatColor.BLUE + Ccell1.member;
            }
            // if in friendly clan
            // get both clans
            Clan clan1 = clans.clanList.get(Ccell1.member);
            if (clan1.Relations().hasFriend(Ccell2.member)) {
                return ChatColor.GREEN + Ccell1.member;
            }
            // if is enemy
            if (clan1.Relations().hasEnemyThis(Ccell2.member) || clan1.Relations().hasEnemyOther(Ccell2.member)) {
                return ChatColor.RED + Ccell1.member;
            }
            // then neutral
            return ChatColor.GRAY + Ccell1.member;
        }

        return null;
    }

}
