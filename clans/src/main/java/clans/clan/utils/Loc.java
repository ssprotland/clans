package clans.clan.utils;

public class Loc {
    public Loc() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.world = "";
    }

    public Loc(Double x, Double y, Double z, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public Loc(int x, int y, int z, String world) {
        this.x = (double) x;
        this.y = (double) y;
        this.z = (double) z;
        this.world = world;
    }

    public Double x;
    public Double y;
    public Double z;
    public String world;
}