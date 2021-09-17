package clans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import clans.clan.Clan;
import clans.clan.utils.Loc;
import clans.clan.utils.Loc2di;
import de.leonhard.storage.Json;

public class Storage {
    // create refrence to itself
    // public static Storage stor = new Storage();
    //
    public static File file;

    static String readFile(File file, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return new String(encoded, encoding);
    }

    public static void load() {
        Json json = new Json(file);

        Map<String, Object> jsonClans = json.getData();

        // clear all clans
        clans.clanList = new HashMap<>();

        for (String name : jsonClans.keySet()) {
            json.setPathPrefix(name); // !!!!!!!!!!!!!!!!!!!!!
            Clan clan = Clan.createClanD(name);
            clan.setPvp(json.getBoolean("pvpEnabled"));
            clan.setOwner(json.getString("owner"));

            List<String> members = json.getStringList("members");
            for (String member : members) {
                clans.log(member);
                clan.addMemberL(member);
            }

            List<String> coowners = json.getStringList("coOwners");
            for (String coowner : coowners) {
                clans.log(coowner);
                clan.addcoownerL(coowner);
            }

            json.setPathPrefix(name + ".home.location");// !!!!!!!!!!!!!!!!!!!!!
            Loc location = new Loc();
            location.x = json.getDouble("x");
            location.y = json.getDouble("y");
            location.z = json.getDouble("z");
            location.world = json.getString("world");
            clan.Home().setHome(location);

            json.setPathPrefix(name + ".eco");// !!!!!!!!!!!!!!!!!!!!!
            clan.Economy().setBalance(json.getDouble("balance"));

            json.setPathPrefix(name + ".claim");// !!!!!!!!!!!!!!!!!!!!!
            clan.Claim().setName(json.getString("name"));
            clan.Claim().setWorld(json.getString("world"));

            json.setPathPrefix(name + ".relations");// !!!!!!!!!!!!!!!!!!!!!
            clan.Relations().setFriends(json.getStringList("friends"));
            clan.Relations().setEnemysOther(json.getStringList("angryToOther"));
            clan.Relations().setEnemysThis(json.getStringList("angryToThis"));

            json.setPathPrefix(name + ".territories");// !!!!!!!!!!!!!!!!!!!!!
            ArrayList<Loc2di> tilesLoc = new ArrayList<>();
            // get json list that contains locations of each territory that is under
            // controll of this clan
            List<?> cords = json.getList("tilesCord");
            if (!cords.isEmpty()) {
                clans.log(cords.get(0).getClass().getName());
                for (Object crd : cords) {
                    HashMap<String, Object> cord = (HashMap<String, Object>) crd;
                    Loc2di loc = new Loc2di();
                    loc.x = (int) cord.get("x");
                    loc.y = (int) cord.get("y");
                    loc.world = (String) cord.get("world");
                    tilesLoc.add(loc);
                }
                clan.Territories().setTilesCords(tilesLoc);
            }
            // init territories and register tiles in storage (TileFactory)
            clan.Territories().init();

            clans.clanList.put(name, clan);
        }
    }

    public static void save() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = gson.toJson(clans.clanList);
        // clans.log(json);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(json.getBytes("UTF-8"));
            outputStream.close();
        } catch (IOException e) {

            e.printStackTrace();
        }

    }
}
