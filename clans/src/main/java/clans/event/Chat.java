package clans.event;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import clans.storage.ClanCell;
import playerstoragev2.PlayerStorage;

public class Chat implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        // if first char is '*'
        if (!event.getMessage().startsWith("*")) {
            return;
        }
        // remove first character from string
        event.setMessage(event.getMessage().substring(1));

        Player player = event.getPlayer();

        ClanCell p1 = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");
        // remove if player are not in the same clan
        event.getRecipients().removeIf((pl) -> {
            ClanCell p2 = (ClanCell) PlayerStorage.getPlayer(pl.getName()).getStorageCell("clans");
            return !p1.member.equals(p2.member);
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChatPrepare(AsyncPlayerChatEvent event) {
        // ad one more '*'
        if (event.getMessage().startsWith("*")) {
            event.setMessage("*" + event.getMessage());
        }

    }
}
