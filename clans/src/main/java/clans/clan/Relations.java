package clans.clan;

import java.util.ArrayList;
import java.util.List;

public class Relations {
    List<String> friends;
    List<String> angryToOther; // this clan is angry to other clan
    List<String> angryToThis; // other clan is angry to this clan

    transient String friendInvite;

    public Relations() {
        friends = new ArrayList<String>();
        angryToOther = new ArrayList<String>();
        angryToThis = new ArrayList<String>();
        friendInvite = "";
    }

    // =============================================================
    public void addFriend(String name) {
        friends.add(name);
    }

    public void removeFriend(String name) {
        friends.remove(name);
    }

    public boolean hasFriend(String name) {
        return friends.contains(name);
    }

    // =============================================================
    public void addEnemyOther(String name) {
        angryToOther.add(name);
    }

    public void removeEnemyOther(String name) {
        angryToOther.remove(name);
    }

    public boolean hasEnemyOther(String name) {
        return angryToOther.contains(name);
    }

    // ==========
    public void addEnemyThis(String name) {
        angryToThis.add(name);
    }

    public void removeEnemyThis(String name) {
        angryToThis.remove(name);
    }

    public boolean hasEnemyThis(String name) {
        return angryToThis.contains(name);
    }

    // =============================================================
    public List<String> getFriends() {
        return friends;
    }

    public List<String> getEnemysOther() {
        return angryToOther;
    }

    public List<String> getEnemysThis() {
        return angryToThis;
    }

    // ==========
    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public void setEnemysOther(List<String> enemys) {
        this.angryToOther = enemys;
    }

    public void setEnemysThis(List<String> enemys) {
        this.angryToThis = enemys;
    }

    // =============================================================
    public void friendshipRequest(String name) {
        friendInvite = name;
    }

    public String getFriendshipRequest() {
        return friendInvite;
    }
}
