package clans.configuration;

public class Config {
    public static int maxTileDistance = 10000;
    public static int minTileDistance = -10000;
    public static int distanceToSpawnPoint = 1000;
    public static int maxHeightDifference = 3;

    public static int castleSizeX = 11;
    public static int castleSizeY = 13;
    public static int firstStageMaxTime = 20 * 60;// 20min in seconds
    public static int secondStageMaxTime = 10 * 60;// 10 min

    public static boolean addZombie = true;
    public static int zombieSpawnRadius = 10;
    public static boolean rememberLastEnterance = true; // "sticky" capturing

}
