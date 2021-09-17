package clans.clan.utils.tile;

import clans.clan.utils.Loc;
import clans.clan.utils.Loc2di;

public class Tile {
    // first and second point of rectangle
    Loc2di p1 = new Loc2di();
    Loc2di p2 = new Loc2di();
    String owner = "";


    public Loc2di getLocation() {
        // calculate midpoint
        Loc2di midPoint = new Loc2di();
        midPoint.world = p1.world;
        midPoint.x = (int) ((p1.x + p2.x) / 2);
        midPoint.y = (int) ((p1.y + p2.y) / 2);
        return midPoint;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public boolean contains(Loc point) {
        // check if point is in bound
        if (((point.x > p1.x) && (point.x < p2.x)) && ((point.y > p1.y) && (point.y < p2.y)))
            return true;
        return false;
    }

    public void capture(String owner) {
        this.owner = owner;
    }
}
