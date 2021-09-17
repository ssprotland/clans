package clans;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

//import clans.alliance.Alliance;
import clans.clan.Clan;
import clans.clan.utils.Loc2di;
import clans.clan.utils.tile.TileFactory;
import clans.commands.ClanCmdRegister;
import clans.commands.messages;
import clans.event.Capturing;
import clans.event.Chat;
import clans.event.pvp;
import clans.storage.ClanCell;

import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import playerstoragev2.PlayerStorage;

public class clans extends JavaPlugin {
    // hasMap of all clans

    public static HashMap<String, Clan> clanList = new HashMap<>();

    // public static HashMap<String, Alliance> allianceList = new HashMap<>();
    static File folder;
    static File customConfigFile;
    static FileConfiguration customConfig;

    private Economy econ;
    private Permission perms;

    public static RegionContainer regContainer;

    public static clans mainInstance;

    @Override
    public void onEnable() {
        mainInstance = this;
        if (!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        this.setupPermissions();
        // this.setupChat();

        PlayerStorage.registerStorageCell(ClanCell.class, "clans");
        // inti worldGuard
        regContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();

        // init clan storage
        folder = getDataFolder();
        Storage.file = new File(getDataFolder(), "clans.json");
        // Storage.init();
        Storage.load();
        // load message file
        initSet();
        loadSet();
        // register commands
        ClanCmdRegister.register();
        // register events
        getServer().getPluginManager().registerEvents(new pvp(), this);
        getServer().getPluginManager().registerEvents(new Chat(), this);
        // save clan state every hour
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            public void run() {
                Storage.save();
            }
        }, 0, (60 * 20 * 60));

        // Small check to make sure that PlaceholderAPI is installed
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new Papi().register();
            log("placeholder registered!");
        }

        // add testing tiles
     
        Capturing.init();

    }

    void test() {
        // create test clan if dont exist
        Clan clan = clanList.get("test");
        if (clan == null)
            clan = Clan.createClan("test");

        for (int i = 0; i < 10; i++) {
            clan.Territories().captureTile(new Loc2di(i, 0, ""));
        }
        TileFactory.tiles.forEach((loc, tile) -> {
            log(tile.getOwner());
            log(tile.getLocation().x + "|" + tile.getLocation().y);
        });

    }

    @Override
    public void onDisable() {
        Storage.save();
    }

    public static void log(String str) {
        Bukkit.getLogger().info("[clans] " + str);
    }

    // public static Clan getClan(String name) {
    // Clan clan = clanList.get(name);
    // if (clan == null) {
    // Storage.load(name);
    // clan = clanList.get(name);
    // }
    // return clan;
    // }
    public static Plugin getInstance() {
        return mainInstance;
    }

    public static void loadSet() {
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        // get all messages from custom config
        messages.help = customConfig.getString("help");
        messages.help = ChatColor.translateAlternateColorCodes('&', messages.help);
        //
        messages.clanDeleting = customConfig.getString("clan_deleting");
        messages.clanDeleting = ChatColor.translateAlternateColorCodes('&', messages.clanDeleting);
        //
        messages.nameAlreadyTaken = customConfig.getString("name_already_taken");
        messages.nameAlreadyTaken = ChatColor.translateAlternateColorCodes('&', messages.nameAlreadyTaken);
        //
        messages.clanSucsesfulyRen = customConfig.getString("clan_sucsesfuly_renamed");
        messages.clanSucsesfulyRen = ChatColor.translateAlternateColorCodes('&', messages.clanSucsesfulyRen);
        //
        messages.homeUpdate = customConfig.getString("home_update");
        messages.homeUpdate = ChatColor.translateAlternateColorCodes('&', messages.homeUpdate);
        //
        messages.invite = customConfig.getString("invation_in_clan");
        messages.invite = ChatColor.translateAlternateColorCodes('&', messages.invite);
        //
        messages.click = customConfig.getString("click");
        messages.click = ChatColor.translateAlternateColorCodes('&', messages.click);
        //
        messages.hereA = customConfig.getString("hereA");
        messages.hereA = ChatColor.translateAlternateColorCodes('&', messages.hereA);
        //
        messages.acceptH = customConfig.getString("acceptH");
        messages.acceptH = ChatColor.translateAlternateColorCodes('&', messages.acceptH);
        //
        messages.hereD = customConfig.getString("hereD");
        messages.hereD = ChatColor.translateAlternateColorCodes('&', messages.hereD);
        //
        messages.declineH = customConfig.getString("declineH");
        messages.declineH = ChatColor.translateAlternateColorCodes('&', messages.declineH);
        //
        messages.accept = customConfig.getString("accept");
        messages.accept = ChatColor.translateAlternateColorCodes('&', messages.accept);
        //
        messages.decline = customConfig.getString("decline");
        messages.decline = ChatColor.translateAlternateColorCodes('&', messages.decline);
        //
        messages.inviteA = customConfig.getString("inviteA");
        messages.inviteA = ChatColor.translateAlternateColorCodes('&', messages.inviteA);
        //
        messages.inviteD = customConfig.getString("inviteD");
        messages.inviteD = ChatColor.translateAlternateColorCodes('&', messages.inviteD);
        //
        messages.inviteAO = customConfig.getString("inviteAO");
        messages.inviteAO = ChatColor.translateAlternateColorCodes('&', messages.inviteAO);
        //
        messages.inviteDO = customConfig.getString("inviteDO");
        messages.inviteDO = ChatColor.translateAlternateColorCodes('&', messages.inviteDO);
        //
        messages.clanDeleting = customConfig.getString("clan_deleting");
        messages.clanDeleting = ChatColor.translateAlternateColorCodes('&', messages.clanDeleting);
        //
        messages.invitesNull = customConfig.getString("you_dont_have_any_invites");
        messages.invitesNull = ChatColor.translateAlternateColorCodes('&', messages.invitesNull);
        //
        messages.playerNull = customConfig.getString("playerNull");
        messages.playerNull = ChatColor.translateAlternateColorCodes('&', messages.playerNull);
        //
        messages.playerKO = customConfig.getString("playerkickedO");
        messages.playerKO = ChatColor.translateAlternateColorCodes('&', messages.playerKO);
        //
        messages.playerK = customConfig.getString("playerkicked");
        messages.playerK = ChatColor.translateAlternateColorCodes('&', messages.playerK);
        //
        messages.clanPvp = customConfig.getString("clanPvp");
        messages.clanPvp = ChatColor.translateAlternateColorCodes('&', messages.clanPvp);
        //
        messages.playerLeave = customConfig.getString("playerLeaveC");
        messages.playerLeave = ChatColor.translateAlternateColorCodes('&', messages.playerLeave);
        //
        messages.tp = customConfig.getString("tp");
        messages.tp = ChatColor.translateAlternateColorCodes('&', messages.tp);
        //
        messages.homeNull = customConfig.getString("homeNull");
        messages.homeNull = ChatColor.translateAlternateColorCodes('&', messages.homeNull);
        //
        messages.done = customConfig.getString("done");
        messages.done = ChatColor.translateAlternateColorCodes('&', messages.done);
        //
        messages.Clist = customConfig.getString("clanList");
        messages.Clist = ChatColor.translateAlternateColorCodes('&', messages.Clist);
        //
        messages.dontHaveClan = customConfig.getString("dontHaveClan");
        messages.dontHaveClan = ChatColor.translateAlternateColorCodes('&', messages.dontHaveClan);
        //
        messages.dontOwnClan = customConfig.getString("dontOwnClan");
        messages.dontOwnClan = ChatColor.translateAlternateColorCodes('&', messages.dontOwnClan);

        //
        messages.info = customConfig.getString("info");
        messages.info = ChatColor.translateAlternateColorCodes('&', messages.info);
        //
        messages.info1 = customConfig.getString("info1");
        messages.info1 = ChatColor.translateAlternateColorCodes('&', messages.info1);
        //
        messages.info1_2 = customConfig.getString("info1_2");
        messages.info1_2 = ChatColor.translateAlternateColorCodes('&', messages.info1_2);
        //
        messages.info2 = customConfig.getString("info2");
        messages.info2 = ChatColor.translateAlternateColorCodes('&', messages.info2);
        //
        messages.info3 = customConfig.getString("info3");
        messages.info3 = ChatColor.translateAlternateColorCodes('&', messages.info3);
        //
        messages.info4 = customConfig.getString("info4");
        messages.info4 = ChatColor.translateAlternateColorCodes('&', messages.info4);
        //
        messages.info5 = customConfig.getString("info5");
        messages.info5 = ChatColor.translateAlternateColorCodes('&', messages.info5);
        //
        messages.info6 = customConfig.getString("info6");
        messages.info6 = ChatColor.translateAlternateColorCodes('&', messages.info6);
        //
        messages.info7 = customConfig.getString("info7");
        messages.info7 = ChatColor.translateAlternateColorCodes('&', messages.info7);
        //
        messages.info8 = customConfig.getString("info8");
        messages.info8 = ChatColor.translateAlternateColorCodes('&', messages.info8);

        //
        messages.needExitClan = customConfig.getString("needExitClan");
        messages.needExitClan = ChatColor.translateAlternateColorCodes('&', messages.needExitClan);
        //
        messages.clancreated = customConfig.getString("clancreated");
        messages.clancreated = ChatColor.translateAlternateColorCodes('&', messages.clancreated);
        //
        messages.needDeleteClan = customConfig.getString("needDeleteClan");
        messages.needDeleteClan = ChatColor.translateAlternateColorCodes('&', messages.needDeleteClan);
        //
        messages.alreadyInC = customConfig.getString("alreadyInC");
        messages.alreadyInC = ChatColor.translateAlternateColorCodes('&', messages.alreadyInC);
        //
        messages.inviteSSuc = customConfig.getString("inviteSSuc");
        messages.inviteSSuc = ChatColor.translateAlternateColorCodes('&', messages.inviteSSuc);

        //
        messages.delete = customConfig.getString("delete");
        messages.delete = ChatColor.translateAlternateColorCodes('&', messages.delete);
        //
        messages.deleteH = customConfig.getString("deleteH");
        messages.deleteH = ChatColor.translateAlternateColorCodes('&', messages.deleteH);
        //
        messages.DdeleteH = customConfig.getString("DdeleteH");
        messages.DdeleteH = ChatColor.translateAlternateColorCodes('&', messages.DdeleteH);
        //
        messages.Tdelete = customConfig.getString("Tdelete");
        messages.Tdelete = ChatColor.translateAlternateColorCodes('&', messages.Tdelete);
        //
        messages.TDdelete = customConfig.getString("TDdelete");
        messages.TDdelete = ChatColor.translateAlternateColorCodes('&', messages.TDdelete);
        //
        messages.deletS = customConfig.getString("deletS");
        messages.deletS = ChatColor.translateAlternateColorCodes('&', messages.deletS);
        //
        messages.delteD = customConfig.getString("delteD");
        messages.delteD = ChatColor.translateAlternateColorCodes('&', messages.delteD);

        //
        messages.PNEmoney = customConfig.getString("PNEmoney");
        messages.PNEmoney = ChatColor.translateAlternateColorCodes('&', messages.PNEmoney);
        //
        messages.deposit = customConfig.getString("deposit");
        messages.deposit = ChatColor.translateAlternateColorCodes('&', messages.deposit);
        //
        messages.CNEmoney = customConfig.getString("CNEmoney");
        messages.CNEmoney = ChatColor.translateAlternateColorCodes('&', messages.CNEmoney);
        //
        messages.withdraw = customConfig.getString("withdraw");
        messages.withdraw = ChatColor.translateAlternateColorCodes('&', messages.withdraw);
        //
        messages.NEmoney = customConfig.getString("NEmoney");
        messages.NEmoney = ChatColor.translateAlternateColorCodes('&', messages.NEmoney);

        //
        messages.Sregion = customConfig.getString("Sregion");
        messages.Sregion = ChatColor.translateAlternateColorCodes('&', messages.Sregion);
        //
        messages.NEregion = customConfig.getString("NEregion");
        messages.NEregion = ChatColor.translateAlternateColorCodes('&', messages.NEregion);
        //
        messages.regionEXS = customConfig.getString("regionEXS");
        messages.regionEXS = ChatColor.translateAlternateColorCodes('&', messages.regionEXS);
        //
        messages.regionEXSERR = customConfig.getString("regionEXSERR");
        messages.regionEXSERR = ChatColor.translateAlternateColorCodes('&', messages.regionEXSERR);
        //
        messages.SRegDel = customConfig.getString("SRegDel");
        messages.SRegDel = ChatColor.translateAlternateColorCodes('&', messages.SRegDel);

        //
        messages.clanNull = customConfig.getString("clanNull");
        messages.clanNull = ChatColor.translateAlternateColorCodes('&', messages.clanNull);
        //
        messages.Finvite = customConfig.getString("Finvite");
        messages.Finvite = ChatColor.translateAlternateColorCodes('&', messages.Finvite);
        //
        messages.Fsend = customConfig.getString("Fsend");
        messages.Fsend = ChatColor.translateAlternateColorCodes('&', messages.Fsend);
        //
        messages.Faccept = customConfig.getString("Faccept");
        messages.Faccept = ChatColor.translateAlternateColorCodes('&', messages.Faccept);
        //
        messages.Fdecline = customConfig.getString("Fdecline");
        messages.Fdecline = ChatColor.translateAlternateColorCodes('&', messages.Fdecline);
        //
        messages.FinvNull = customConfig.getString("SRegDel");
        messages.FinvNull = ChatColor.translateAlternateColorCodes('&', messages.FinvNull);
        //
        messages.FRemoved = customConfig.getString("FRemoved");
        messages.FRemoved = ChatColor.translateAlternateColorCodes('&', messages.FRemoved);
        //
        messages.FinvRemoved = customConfig.getString("FinvRemoved");
        messages.FinvRemoved = ChatColor.translateAlternateColorCodes('&', messages.FinvRemoved);

        //
        messages.Fsuc = customConfig.getString("Fsuc");
        messages.Fsuc = ChatColor.translateAlternateColorCodes('&', messages.Fsuc);
        //
        messages.Edec = customConfig.getString("Edec");
        messages.Edec = ChatColor.translateAlternateColorCodes('&', messages.Edec);
        //
        messages.Erem = customConfig.getString("Erem");
        messages.Erem = ChatColor.translateAlternateColorCodes('&', messages.Erem);
        //
        messages.SRegDel = customConfig.getString("SRegDel");
        messages.SRegDel = ChatColor.translateAlternateColorCodes('&', messages.SRegDel);
    }

    public void initSet() {
        // check if config and .schem exist and create it if not
        customConfigFile = new File(folder, "messages.yml");
        File schem = new File(folder, "castle\\castle.schem");

        if (!customConfigFile.exists()) {
            customConfigFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }
        if (!schem.exists()) {
            schem.getParentFile().mkdirs();
            saveResource("castle\\castle.schem", false);
        }
        // loads it
        customConfig = new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public Economy getEconomy() {
        return econ;
    }

    public Permission getPermissions() {
        return perms;
    }

}
