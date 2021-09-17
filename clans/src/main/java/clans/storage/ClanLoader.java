package clans.storage;

import playerstoragev2.sql.Input;
import playerstoragev2.storage.Loader;
import playerstoragev2.storage.StorageCell;

public class ClanLoader implements Loader {

    @Override
    public String getPluginName() {
        return "clans";
    }

    @Override
    public boolean onLoad(StorageCell cell, Input input) {
        // primary check
        if (!input.hasData()) {
            return false;
        }

        ClanCell Ccell = (ClanCell) cell;

        // TODO: init variables
        Ccell.clanDelete = false;
        Ccell.invite = "";
        // secondary check
        if (input.getLong("id") == null) {
            // load default value
            Ccell.member = "";
            Ccell.owner = false;
            Ccell.coOwner = false;
            return true;
        }
        Ccell.member = input.getString("member");
        Ccell.owner = input.getBool("owner");
        Ccell.coOwner = input.getBool("coOwner");
        return true;
    }

    @Override
    public String onSave(StorageCell cell) {
        ClanCell Ccell = (ClanCell) cell;
        return "member='" + Ccell.member + "', owner=" + Ccell.owner + ", coOwner=" + Ccell.coOwner;
    }

    @Override
    public String onInit() {
        return "member VARCHAR(64), owner BOOLEAN, coOwner BOOLEAN";
    }

    @Override
    public String data() {
        return "member, owner, coOwner";
    }

    @Override
    public String value(StorageCell cell) {
        ClanCell Ccell = (ClanCell) cell;
        return "'" + Ccell.member + "', " + Ccell.owner + ", " + Ccell.coOwner;
    }

}
