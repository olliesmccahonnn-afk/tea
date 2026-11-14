package dev.mythclient.module.world;

/** A saved position. {@code world} = server address or "singleplayer"; {@code dimension} = e.g. minecraft:overworld. */
public final class Waypoint {

    public String name;
    public int x;
    public int y;
    public int z;
    public String world;
    public String dimension;
    public boolean visible = true;

    public Waypoint(String name, int x, int y, int z, String world, String dimension) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.dimension = dimension;
    }
}
