package dev.mythclient.module.hud;

import java.util.List;
import java.util.Locale;

/** Horizontal speed in blocks per second. */
public class SpeedModule extends HudModule {
    private double lastX, lastZ, speed;
    private boolean have;

    public SpeedModule() { super("Speed", "Shows your horizontal speed in blocks/s.", 90, 28); }

    @Override
    public void onTick() {
        if (mc.player == null) { have = false; return; }
        double x = mc.player.getX(), z = mc.player.getZ();
        if (have) speed = Math.hypot(x - lastX, z - lastZ) * 20.0;
        lastX = x; lastZ = z; have = true;
    }

    @Override
    protected List<String> lines() {
        return List.of(String.format(Locale.ROOT, "Speed: %.1f b/s", speed));
    }
}
