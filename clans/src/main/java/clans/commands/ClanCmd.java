package clans.commands;

import java.util.List;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import clans.Storage;
import clans.clans;
import clans.clan.Clan;
import clans.clan.utils.Loc2di;
import clans.clan.utils.map.MapRender;
import clans.storage.ClanCell;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import playerstoragev2.PlayerStorage;
import playerstoragev2.storage.PlayerS;

public class ClanCmd {

    public static void help(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "==clan help==");
        sender.sendMessage(messages.help);
    }

    public static void help2(CommandSender sender) {
        help(sender);
    }

    public static void delete(Player player) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }
        PlayerS playerS = PlayerStorage.getPlayer(player.getName());
        ClanCell Ccell = (ClanCell) playerS.getStorageCell("clans");
        Ccell.clanDelete = true;
        // TO DO: send player text to confirm deleting
        //
        // create clickable message to player
        //
        TextComponent message = new TextComponent(messages.delete + ChatColor.GREEN + clan.getName() + "\n");

        TextComponent accept = new TextComponent(messages.hereA);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan delete yes"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messages.deleteH)));

        TextComponent decline = new TextComponent(messages.hereD);
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan delete no"));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messages.DdeleteH)));

        message.addExtra(messages.click);
        message.addExtra(accept);
        message.addExtra(messages.Tdelete);

        message.addExtra(messages.click);
        message.addExtra(decline);
        message.addExtra(messages.TDdelete);
        player.spigot().sendMessage(message);
        // clan.sendMessageToOwner(messages.clanDeleting);
        // clan.deleteClan();
    }

    public static void delete(Player player, String del) {
        PlayerS playerS = PlayerStorage.getPlayer(player.getName());
        ClanCell Ccell = (ClanCell) playerS.getStorageCell("clans");
        if (del.equals("yes")) {
            // delete clan

            // return if clan_del == false
            if (!Ccell.clanDelete) {
                return;
            }
            Clan clan = getPlayerOwnClan(player);
            if (clan == null) {
                return;
            }
            clan.deleteClan();
            Ccell.clanDelete = false;
            player.sendMessage(messages.deletS);

        }
        if (del.equals("no")) {
            // return if clan_del == false
            Ccell.clanDelete = false;
            player.sendMessage(messages.delteD);

        }
    }

    // delete for console

    public static void delete(CommandSender sender, String clanName) {
        Clan clan = clans.clanList.get(clanName);
        if (clan == null) {
            sender.sendMessage("clan not found");
            return;
        }
        clan.sendMessageToOwner(messages.clanDeleting);
        clan.deleteClan();
        sender.sendMessage("clan deleted!");
    }

    public static void sethome(Player player) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }
        clan.Home().setHome(player.getLocation());
        player.sendMessage(messages.homeUpdate);
    }

    // TO/DO:messages

    public static void invite(Player player, Player invited) {
        Clan clan = getPlayerCoOwnClan(player);
        if (clan == null) {
            return;
        }
        ClanCell invitedC = (ClanCell) PlayerStorage.getPlayer(invited.getName()).getStorageCell("clans");
        // ClanCell invitedC = (ClanCell) invitedS.getStorageCell("clans");

        // check if invited already has a clan(dont print any message)
        Clan clan2 = getPlayerClan(invited, false);
        if (clan2 != null) {
            player.sendMessage(messages.alreadyInC);
            return;
        }
        // fill clan_invite with clan name
        invitedC.invite = clan.getName();
        //
        // create clickable message to player
        //
        TextComponent message = new TextComponent(messages.invite + ChatColor.GREEN + clan.getName() + "\n");

        TextComponent accept = new TextComponent(messages.hereA);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messages.acceptH)));

        TextComponent decline = new TextComponent(messages.hereD);
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan deny"));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messages.declineH)));

        message.addExtra(messages.click);
        message.addExtra(accept);
        message.addExtra(messages.accept);

        message.addExtra(messages.click);
        message.addExtra(decline);
        message.addExtra(messages.decline);

        invited.spigot().sendMessage(message);
        // send message to inviter
        player.sendMessage(messages.inviteSSuc);

    }

    public static void accept(Player player) {
        // get player storage player
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");

        if (playerC.invite.equals("")) {
            // you dont have any invites
            player.sendMessage(messages.invitesNull);
            return;
        }

        // get clan by name
        Clan clan = clans.clanList.get(playerC.invite);
        // add plyer to clan
        clan.addMember(player.getName());
        // reset invite
        playerC.invite = "";
        // send message to player
        player.sendMessage(messages.inviteA);
        // send message to clan owner
        clan.sendMessageToOwner(messages.inviteAO);

    }

    public static void deny(Player player) {

        // get player storage player
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");
        // if player has any invite
        if (playerC.invite.equals("")) {
            // get clan by name
            Clan clan = clans.clanList.get(playerC.invite);
            // invite decline for owner
            clan.sendMessageToOwner(messages.inviteDO);
            // reset invite
            playerC.invite = "";
            // invite decline
            player.sendMessage(messages.inviteD);
        } else {
            player.sendMessage(messages.invitesNull);
        }
    }

    public static void kick(Player owner, String playerName) {
        Clan clan = getPlayerCoOwnClan(owner);
        if (clan == null) {
            return;
        }
        if (!clan.deleteMemder(playerName)) {
            owner.sendMessage(messages.playerNull);
            return;
        }
        owner.sendMessage(messages.playerKO);
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return;
        }
        player.sendMessage(messages.playerK);
    }

    // override to have player list

    public static void kick(Player owner, Player player) {
        kick(owner, player.getName());
    }

    public static void togglePvp(Player owner) {
        Clan clan = getPlayerCoOwnClan(owner);
        if (clan == null) {
            return;
        }
        clan.togglePvp();
        owner.sendMessage(messages.clanPvp + Boolean.toString(clan.getPvp()));
    }

    public static void create(Player owner, String clanName) {
        // get player in playerstorage
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(owner.getName()).getStorageCell("clans");

        // check if player has clan
        if (!playerC.member.equals("")) {
            // first, players needds to leave existing clan
            owner.sendMessage(messages.needExitClan);
            return;
        }

        Clan clan = Clan.createClan(clanName);
        if (clan == null) {
            owner.sendMessage(messages.nameAlreadyTaken);
            return;
        }
        clan.setOwner(owner.getName());
        owner.sendMessage(messages.clancreated);

    }

    public static void leave(Player player) {
        Clan clan = getPlayerClan(player);
        if (clan == null) {
            return;
        }
        if (clan.getOwner().equals(player.getName())) {
            player.sendMessage(messages.needDeleteClan);
            return;
        }
        clan.deleteMemder(player.getName());
        player.sendMessage(messages.playerLeave);

    }

    static double getLenght(Location loc) {
        // return sqrt(x^2+y^2+z^2)
        return Math.sqrt((loc.getX() * loc.getX()) + (loc.getY() * loc.getY()) + (loc.getZ() * loc.getZ()));
    }

    public static void tpHome(Player player) {
        Clan clan = getPlayerClan(player);
        if (clan == null) {
            return;
        }
        player.sendMessage(messages.tp);
        if (clan.Home().getHome() == null) {
            player.sendMessage(messages.homeNull);
            return;
        }
        Vector locStart = player.getLocation().toVector();

        new BukkitRunnable() {
            int i = 0;

            @Override
            public void run() {
                i++;
                // every second check if player moved more than treshold
                Vector locNow = player.getLocation().toVector();

                locNow.subtract(locStart);

                // clans.log(locStart.toString());
                // clans.log(locNow.toString());
                // clans.log(dif.toString());

                if (locNow.length() >= 1) {
                    this.cancel();
                }
                // if past 5 seconds
                player.sendMessage(ChatColor.GOLD + Integer.toString(i));
                if (i >= 5) {
                    this.cancel();
                    player.sendMessage(messages.tp);
                    player.teleport(clan.Home().getHome());
                    player.sendMessage(messages.done);
                }
            }
        }.runTaskTimer(clans.mainInstance, 0, 20);
    }

    // load messages

    public static void mload(CommandSender sender) {
        clans.loadSet();
    }

    // load data

    public static void dload(CommandSender sender) {
        Storage.load();
    }

    // save data

    public static void save(CommandSender sender) {
        Storage.save();
    }

    // @Subcommand("sync")
    // @NeedsOp
    // public static void sync(CommandSender sender) {
    // Storage.sync();
    // }

    public static void list(CommandSender sender) {
        sender.sendMessage(messages.Clist);
        for (String clan : clans.clanList.keySet()) {
            sender.sendMessage(clan);
        }
    }

    public static void info(Player player) {
        Clan clan = getPlayerClan(player);
        if (clan == null) {
            return;
        }
        info((CommandSender) player, clan.getName());
    }

    public static void info(CommandSender sender, String clanName) {
        Clan clan = clans.clanList.get(clanName);
        if (clan == null) {
            return;
        }
        sender.sendMessage(messages.info + clan.getName());
        sender.sendMessage(messages.info1 + clan.getOwner());
        // ==
        sender.sendMessage(messages.info1_2);
        List<String> coOwners = clan.getCoOwners();
        String ScoOwners = "";
        for (String coOwner : coOwners) {
            ScoOwners += coOwner + ", ";
        }
        sender.sendMessage(ChatColor.DARK_GRAY + ScoOwners);
        // ==
        sender.sendMessage(messages.info2);
        List<String> members = clan.getMembers();
        String Smembers = "";
        for (String member : members) {
            Smembers += member + ", ";
        }
        sender.sendMessage(ChatColor.DARK_GRAY + Smembers);
        // ==
        sender.sendMessage(messages.info6);
        List<String> friends = clan.Relations().getFriends();
        String Sfriends = "";
        for (String friend : friends) {
            Sfriends += friend + ", ";
        }
        sender.sendMessage(ChatColor.GRAY + Sfriends);
        // ==
        sender.sendMessage(messages.info7);
        List<String> enemysTo = clan.Relations().getEnemysOther();
        String SenemysTo = "";
        for (String enemyTo : enemysTo) {
            SenemysTo += enemyTo + ", ";
        }
        sender.sendMessage(ChatColor.GRAY + SenemysTo);
        // ==
        sender.sendMessage(messages.info8);
        List<String> enemysFrom = clan.Relations().getEnemysThis();
        String SenemysFrom = "";
        for (String enemyFrom : enemysFrom) {
            SenemysFrom += enemyFrom + ", ";
        }
        sender.sendMessage(ChatColor.GRAY + SenemysFrom);
        // ==
        sender.sendMessage(ChatColor.GOLD + "Кол-во тайлов у клана: " + clan.Territories().quantityOfTiles());
        sender.sendMessage(ChatColor.GOLD + "Координаты этих тайлов: ");
        String tileCords = "";
        for (Loc2di tile : clan.Territories().getTilesCords()) {
            tileCords += "x:" + tile.x + " y:" + tile.y + ", "; // "x:10 y:10, "
        }
        sender.sendMessage(ChatColor.GRAY + tileCords);
        // ==
        sender.sendMessage(messages.info3 + clan.getPvp());
        sender.sendMessage(messages.info4 + clan.Economy().getBalance());
        sender.sendMessage(messages.info5 + clan.Home().exist());
    }

    public static void deposit(Player player, Double ammount) {
        Clan clan = getPlayerClan(player);
        if (clan == null) {
            return;
        }
        if (!clan.Economy().deposit(player, ammount)) {
            player.sendMessage(messages.PNEmoney);
            return;
        }
        player.sendMessage(messages.deposit);
    }

    public static void withdraw(Player player, Double ammount) {
        Clan clan = getPlayerCoOwnClan(player);
        if (clan == null) {
            return;
        }
        if (!clan.Economy().withdraw(player, ammount)) {
            player.sendMessage(messages.CNEmoney);
            return;
        }
        player.sendMessage(messages.withdraw);
    }

    @SuppressWarnings("deprecation")
    public static void claim(Player owner) {
        Clan clan = getPlayerOwnClan(owner);
        if (clan == null) {
            return;
        }
        BukkitPlayer actor = BukkitAdapter.adapt(owner); // WorldEdit's native Player class extends Actor
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        Region region; // declare the region variable
        // note: not necessarily the player's current world, see the concepts page
        World selectionWorld = localSession.getSelectionWorld();
        try {
            if (selectionWorld == null)
                throw new IncompleteRegionException();
            region = localSession.getSelection(selectionWorld);
        } catch (IncompleteRegionException ex) {
            actor.printError("Please make a region selection first."); // !depricated
            return;
        }
        CuboidRegion reg = region.getBoundingBox();

        // claim
        clan.claim(owner, reg.getMaximumPoint(), reg.getMinimumPoint());

    }

    public static void unclaim(Player owner) {
        Clan clan = getPlayerOwnClan(owner);
        if (clan == null) {
            return;
        }
        clan.unclaim(owner);
    }

    public static void addFriend(Player player, String friend) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }
        Clan friendClan = clans.clanList.get(friend);
        if (friendClan == null) {
            player.sendMessage(messages.clanNull);
            return;
        }
        friendClan.Relations().friendshipRequest(clan.getName());
        // send message to friendClan owner
        Player friendPlayer = Bukkit.getPlayer(friendClan.getOwner());
        // player is offline
        if (friendPlayer == null) {
            player.sendMessage(messages.Fsend);
            return;
        }

        TextComponent message = new TextComponent(messages.Finvite + ChatColor.GREEN + friendClan.getName() + "\n");

        TextComponent accept = new TextComponent(messages.hereA);
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan relations friend accept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messages.accept)));

        TextComponent decline = new TextComponent(messages.hereD);
        decline.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan relations friend decline"));
        decline.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(messages.declineH)));

        message.addExtra(messages.click);
        message.addExtra(accept);
        message.addExtra(messages.Faccept);

        message.addExtra(messages.click);
        message.addExtra(decline);
        message.addExtra(messages.Fdecline);
        friendPlayer.spigot().sendMessage(message);
        player.sendMessage(messages.Fsend);

    }

    public static void removeFriend(Player player, String friend) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }

        Clan friendClan = clans.clanList.get(friend);
        if (friendClan == null) {
            // just in case when clan is deleted
            clan.Relations().removeFriend(friend);
            return;
        }
        // remove friend from each other
        clan.Relations().removeFriend(friendClan.getName());
        friendClan.Relations().removeFriend(clan.getName());
        // friendship removed
        player.sendMessage(messages.FRemoved);

    }

    public static void acceptFriend(Player player) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }

        // check if clan has friend invite
        if (clan.Relations().getFriendshipRequest().equals("")) {
            // clan dont have any friendship invites
            player.sendMessage(messages.FinvNull);
            return;
        }

        Clan friend = clans.clanList.get(clan.Relations().getFriendshipRequest());
        if (friend == null) {
            // error
            return;
        }

        // add friends in both clans
        clan.Relations().addFriend(friend.getName());
        friend.Relations().addFriend(clan.getName());
        // clear friendship request
        clan.Relations().friendshipRequest("");

        player.sendMessage(messages.Fsuc);
    }

    public static void declineFriendship(Player player) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }
        // clear friendship request
        clan.Relations().friendshipRequest("");

        // you declined friendship invite
        player.sendMessage(messages.FinvRemoved);
    }

    public static void addEnemy(Player player, String enemy) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }
        Clan enemyClan = clans.clanList.get(enemy);
        if (enemyClan == null) {
            player.sendMessage(messages.clanNull);
            return;
        }
        // add enemy in both clans
        clan.Relations().addEnemyOther(enemyClan.getName());
        enemyClan.Relations().addEnemyThis(clan.getName());
        // war declared!
        player.sendMessage(messages.Edec);
    }

    public static void removeEnemy(Player player, String enemy) {
        Clan clan = getPlayerOwnClan(player);
        if (clan == null) {
            return;
        }

        Clan enemyClan = clans.clanList.get(enemy);
        if (enemyClan == null) {
            clan.Relations().removeEnemyOther(enemy);
            player.sendMessage(messages.Erem);
            return;
        }

        clan.Relations().removeEnemyOther(enemyClan.getName());
        enemyClan.Relations().removeEnemyThis(clan.getName());
        player.sendMessage(messages.Erem);
    }

    public static void balance(Player player) {
        Clan clan = getPlayerClan(player);
        if (clan == null) {
            return;
        }
        player.sendMessage(messages.info4 + clan.Economy().getBalance());
    }

    public static void raise(Player owner, Player player) {
        Clan clan = getPlayerOwnClan(owner);
        if (clan == null) {
            return;
        }
        if (!clan.addCoOwner(player.getName())) {
            owner.sendMessage(messages.playerNull);
        }
    }

    public static void lower(Player owner, Player player) {
        Clan clan = getPlayerOwnClan(owner);
        if (clan == null) {
            return;
        }

        if (!clan.delCoOwner(player.getName())) {
            owner.sendMessage(messages.playerNull);
        }
    }

    public static void getMap(Player player, int zoom) {
        // TODO: map coloring relative to clan
        Clan clan = getPlayerClan(player);
        ItemStack i = new ItemStack(Material.FILLED_MAP, 1);
        MapView view = Bukkit.createMap(Bukkit.getServer().getWorlds().get(0));
        MapMeta mapMeta = (MapMeta) i.getItemMeta();
        mapMeta.setMapView(view);
        switch (zoom) {
            case 1:
                view.setScale(Scale.NORMAL);
                break;
            case 2:
                view.setScale(Scale.FAR);
                break;
            case 3:
                view.setScale(Scale.FARTHEST);
                break;
            default:
                view.setScale(Scale.NORMAL);
                break;
        }

        view.setTrackingPosition(false);

        view.setCenterX(0);
        view.setCenterZ(0);
        view.setUnlimitedTracking(false);

        // clear map renderers
        for (MapRenderer renderer : view.getRenderers())
            view.removeRenderer(renderer);
        // add custom renderer
        view.addRenderer(new MapRender(clan, view));

        // set map id
        // NBTItem nbti = new NBTItem(i);
        // nbti.setInteger("map", view.getId());
        // i = nbti.getItem();
        i.setItemMeta(mapMeta);
        // i.setDurability((short) view.getId());
        player.getInventory().setItemInMainHand(i);
    }

    static Clan getPlayerOwnClan(Player player) {
        // get player and check if player exist
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");

        // check if player has clan
        if (playerC.member.equals("")) {
            player.sendMessage(messages.dontHaveClan);
            return null;
        }

        // check if player is ONLY clan owner
        if (!playerC.owner) {
            player.sendMessage(messages.dontOwnClan);
            return null;
        }
        return clans.clanList.get(playerC.member);
    }

    static Clan getPlayerCoOwnClan(Player player) {
        // get player and check if player exist
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");

        // check if player has clan
        if (playerC.member.equals("")) {
            player.sendMessage(messages.dontHaveClan);
            return null;
        }

        // check if player is clan co owner OR owner
        if (!(playerC.coOwner || playerC.owner)) {
            player.sendMessage(messages.dontOwnClan);
            return null;
        }
        return clans.clanList.get(playerC.member);
    }

    static Clan getPlayerClan(Player player) {
        return getPlayerClan(player, true);
    }

    static Clan getPlayerClan(Player player, Boolean printmsg) {
        // get player and check if player exist
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(player.getName()).getStorageCell("clans");

        // check if player has clan
        if (playerC.member.equals("")) {
            if (printmsg) {
                player.sendMessage(messages.dontHaveClan);
            }

            return null;
        }
        return clans.clanList.get(playerC.member);
    }
}
