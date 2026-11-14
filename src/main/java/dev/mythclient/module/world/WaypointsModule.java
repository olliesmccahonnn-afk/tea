package dev.mythclient.module.world;

import dev.mythclient.MythClient;
import dev.mythclient.module.hud.HudModule;
import dev.mythclient.setting.BoolSetting;
import dev.mythclient.setting.NumberSetting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lists your nearest waypoints on the HUD with distance and a direction arrow
 * (arrow is relative to where you are looking). Add / delete them in click GUI > Waypoints.
 */
public final class WaypointsModule extends HudModule {

    // clockwise from "straight ahead"
    private static final String[] ARROWS = {
            "\u2191", "\u2197", "\u2192", "\u2198", "\u2193", "\u2199", "\u2190", "\u2196"};

    private final NumberSetting maxShown = setting(new NumberSetting("Max shown", 5, 1, 12, 1));
    private final BoolSetting showDistance = setting(new BoolSetting("Distance", true));
    private final BoolSetting showArrow = setting(new BoolSetting("Arrow", true));

    public WaypointsModule() {
        super("Waypoints", "Lists nearby waypoints with distance and direction.", 4, 44);
    }

    /** Arrow pointing at (dx, dz) from a player facing {@code yaw} (Minecraft yaw: 0 = south, clockwise). */
    public static String arrowFor(double dx, double dz, float yaw) {
        double target = Math.toDegrees(Math.atan2(-dx, dz));
        double rel = ((target - yaw) % 360 + 540) % 360 - 180; // -180..180, + = to the right
        int idx = (int) Math.round(rel / 45.0);
        return ARROWS[((idx % 8) + 8) % 8];
    }

    @Override
    protected List<String> lines() {
        var p = mc.player;
        if (p == null || mc.world == null) return List.of();
        double px = p.getX();
        double py = p.getY();
        double pz = p.getZ();
        float yaw = p.getYaw();

        List<Waypoint> here = new ArrayList<>(MythClient.waypoints().visibleHere());
        here.sort(Comparator.comparingDouble(w -> sq(w.x + 0.5 - px) + sq(w.y - py) + sq(w.z + 0.5 - pz)));

        List<String> out = new ArrayList<>();
        for (int i = 0; i < here.size() && i < maxShown.intValue(); i++) {
            Waypoint w = here.get(i);
            double dx = w.x + 0.5 - px;
            double dy = w.y - py;
            double dz = w.z + 0.5 - pz;
            StringBuilder line = new StringBuilder(w.name);
            if (showDistance.get()) line.append("  ").append(Math.round(Math.sqrt(dx * dx + dy * dy + dz * dz))).append("m");
            if (showArrow.get()) line.append("  ").append(arrowFor(dx, dz, yaw));
            out.add(line.toString());
        }
        return out;
    }

    private static double sq(double v) {
        return v * v;
    }
}
