package clans.clan;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import clans.clans;
//import clans.alliance.Alliance;
import clans.commands.messages;
import clans.storage.ClanCell;

import playerstoragev2.PlayerStorage;

public class Clan {
    private String name; // clan name
    private boolean pvpEnabled;
    private String owner; // owner of the clan
    private List<String> coOwners; // coownersd
    private List<String> members; // all clan members

    private Home home; // home location
    private Eco eco;
    private Claim claim;
    private Relations relations;
    private Territories territories;
    // private CAlliance alliance;
    // private transient String allianceInvite;

    // ==================================================================================================
    public boolean setName(String newName) {
        // check if name is already taken
        if (clans.clanList.get(newName) != null) {
            return false;
        }
        // change name in hashmap
        Clan tmpc = clans.clanList.remove(name);
        clans.clanList.put(newName, tmpc);
        // change name in clan
        name = newName;
        return true;
    }

    public String getName() {
        return name;
    }

    // ==================================================================================================
    public Relations Relations() {
        return relations;
    }

    // ==================================================================================================
    public Territories Territories() {
        return territories;
    }

    // ==================================================================================================
    public Home Home() {
        return home;
    }

    public void delHome() {
        home = null;
    }

    // ==================================================================================================
    public Eco Economy() {
        return eco;
    }

    // ==================================================================================================
    public void setPvp(boolean pvp) {
        pvpEnabled = pvp;
    }

    public boolean getPvp() {
        return pvpEnabled;
    }

    public void togglePvp() {
        pvpEnabled = !pvpEnabled;
    }

    // ==================================================================================================
    public void addMember(String playerName) {
        // add player to membvers
        members.add(playerName);
        // add to claim
        if (claim.exist()) {
            claim.addPlayer(playerName);
        }
        // add clan to player in player storage
        // check if player exist
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(playerName).getStorageCell("clans");

        playerC.member = name;
        playerC.owner = false;
        playerC.coOwner = false;
        // _player.data.put("clan", cl);

    }

    public boolean deleteMemder(String playerName) {
        // find player and remove it from list of members
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).equalsIgnoreCase(playerName)) {
                // remove from clan list
                members.remove(i);
                // remove from claim
                if (claim.exist()) {
                    claim.removePlayer(playerName);
                }
                // remove clan from payer storage
                ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(playerName).getStorageCell("clans");
                playerC.member = "";
                playerC.owner = false;
                playerC.coOwner = false;
                // return true if all is ok
                return true;
            }
        }
        // we cant find player
        return false;
    }

    public List<String> getMembers() {
        return members;
    }

    // ==================================================================================================
    public boolean addCoOwner(String playerName) {
        // if player dont exist in members
        if (!members.remove(playerName)) {
            return false;
        }

        coOwners.add(playerName);
        claim.addOwner(playerName);
        // add clan to player in player storage
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(playerName).getStorageCell("clans");

        playerC.member = name;
        playerC.owner = false;
        playerC.coOwner = true;
        return true;
    }

    public boolean delCoOwner(String playerName) {
        // if cant find coowner
        if (!coOwners.remove(playerName)) {
            return false;
        }
        // remove from coowners in playerstorage
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(playerName).getStorageCell("clans");

        playerC.member = name;
        playerC.owner = false;
        playerC.coOwner = false;
        // add to members
        members.add(playerName);
        claim.removeOwner(playerName);
        return true;
    }

    public List<String> getCoOwners() {
        return coOwners;
    }

    // ==================================================================================================
    public void setOwner(String own) {
        // set player clan and owner in player storage
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(own).getStorageCell("clans");

        playerC.member = name;
        playerC.owner = true;
        playerC.coOwner = false;
        // set clan owner in clan
        owner = own;
    }

    public String getOwner() {
        return owner;
    }

    public void sendMessageToOwner(String message) {
        Player own = Bukkit.getPlayer(owner);
        if (own == null) {
            return;
        }
        own.sendMessage(message);
    }

    // ==================================================================================================
    public boolean claim(Player owner, BlockVector3 point1, BlockVector3 point2) {
        // check if clan already has claim
        if (claim.exist()) {
            owner.sendMessage(messages.regionEXS);
            return false;
        }

        // check if clan can pay for claim
        // get volume
        BlockVector3 tpoint1 = point1;
        BlockVector3 tpoint2 = point2;

        // shift region to center(tpoint1 == 0)
        tpoint2 = tpoint2.subtract(tpoint1);
        tpoint1 = tpoint1.subtract(tpoint1);
        Long volume = (long) (tpoint2.getBlockX() * tpoint2.getBlockY() * tpoint2.getBlockZ());
        volume = Math.abs(volume); // we dont wana negative volume
        // check if clan has enought money
        if (volume > eco.getBalance()) {
            owner.sendMessage(messages.NEmoney + clans.mainInstance.getEconomy().format(volume));
            return false;
        }

        // claim
        if (!claim.create(point1, point2, owner.getWorld().getName(), getName())) {
            owner.sendMessage(messages.regionEXSERR);
            return false;
        }

        // add members to claim
        claim.addPlayerBulk(members);
        claim.addOwnerBulk(coOwners);
        claim.addOwner(getOwner());
        // set flags
        claim.getClaim().setFlag(Flags.PVP, State.ALLOW);

        eco.substract((double) volume);

        owner.sendMessage(messages.Sregion);
        return true;
    }

    public boolean unclaim(Player own) {
        // if claim dont exist
        if (!claim.exist()) {
            own.sendMessage(messages.NEregion);
            return false;
        }
        // calculate region cost
        BlockVector3 point1 = claim.getClaim().getMaximumPoint();
        BlockVector3 point2 = claim.getClaim().getMinimumPoint();
        // shift region to center(tpoint1 == 0)
        point2 = point2.subtract(point1);
        point1 = point1.subtract(point1);
        Long volume = (long) (point2.getBlockX() * point2.getBlockY() * point2.getBlockZ());
        volume = Math.abs(volume); // we dont wana negative volume
        // return money for region
        eco.add((double) volume);
        // remove region
        claim.remove();

        own.sendMessage(messages.SRegDel + clans.mainInstance.getEconomy().format(volume));

        return true;
    }

    public Claim Claim() {
        return claim;
    }

    // ==================================================================================================
    public static Clan createClan(String clanName) {
        // check if clan already exist
        if (clans.clanList.containsKey(clanName)) {
            return null;
        }
        // create new clan, init it, put it in hash map and return it
        Clan tmpc = createClanD(clanName);
        clans.clanList.put(clanName, tmpc);
        return tmpc;
    }

    // just create new clan, dont check or register anything
    public static Clan createClanD(String clanName) {
        // create new clan, init it and return it
        Clan tmpc = new Clan();
        tmpc.members = new ArrayList<>();
        tmpc.coOwners = new ArrayList<>();
        tmpc.home = new Home();
        tmpc.name = clanName;
        tmpc.claim = new Claim();
        tmpc.eco = new Eco();
        tmpc.relations = new Relations();
        tmpc.territories = new Territories(tmpc);
        return tmpc;
    }

    public boolean deleteClan() {
        // remove clan from player storage
        // for members
        for (String member : members) {
            ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(member).getStorageCell("clans");

            playerC.member = "";
            playerC.owner = false;
            playerC.coOwner = false;
        }
        // for coowners
        for (String coowner : coOwners) {
            ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(coowner).getStorageCell("clans");
            playerC.member = "";
            playerC.owner = false;
            playerC.coOwner = false;
        }
        // for owner
        ClanCell playerC = (ClanCell) PlayerStorage.getPlayer(owner).getStorageCell("clans");
        playerC.member = "";
        playerC.owner = false;
        playerC.coOwner = false;

        claim.remove();
        // remove clan from hash map of clans
        clans.clanList.remove(getName());
        return true;
    }

    // ==================================================================================================
    public void setOwnerL(String own) {
        this.owner = own;
    }

    public void addMemberL(String memb) {
        members.add(memb);
    }

    public void addcoownerL(String coown) {
        coOwners.add(coown);
    }
}
