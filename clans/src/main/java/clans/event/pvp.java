package clans.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import clans.clan.Clan;
import clans.storage.ClanCell;
import playerstoragev2.PlayerStorage;
import playerstoragev2.storage.PlayerS;
import clans.clans;

public class pvp implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {

        if (event.getEntity() instanceof Player) {
            Player defender = (Player) event.getEntity();

            if (event.getDamager() instanceof Player) {
                Player damager = (Player) event.getDamager();

                // get playerstorage player for damager
                PlayerS damagerS = PlayerStorage.getPlayer(damager.getName());

                // check if damager has clan
                ClanCell damagerC = (ClanCell) damagerS.getStorageCell("clans");
                if (damagerC.member.equals("")) {
                    return;
                }

                // get playerstorage player for defender
                PlayerS defenderS = PlayerStorage.getPlayer(defender.getName());

                // check if player has clan
                ClanCell defenderC = (ClanCell) defenderS.getStorageCell("clans");
                if (defenderC.member.equals("")) {
                    return;
                }
                // check if players has the same clan
                if (!defenderC.member.equals(damagerC.member)) {
                    // TODO: check if in the same aliance
                    return;
                }
                // get clan
                Clan clan = clans.clanList.get(defenderC.member);
                // chec if clan has pvp on
                if (clan.getPvp()) {
                    return;
                }
                event.setCancelled(true);
            }
        }

    }

}
