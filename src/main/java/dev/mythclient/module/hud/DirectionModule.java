package dev.mythclient.module.hud;

import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Locale;

/** Compass direction plus yaw. */
public class DirectionModule extends HudModule {
    private static final String[] DIRS = {"S","SW","W","NW","N","NE","E","SE"};

    public DirectionModule() { super("Direction", "Shows which way you are facing.", 90, 40); }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        float yaw = MathHelper.wrapDegrees(mc.player.getYaw());
        // yaw 0 = south, 180/-180 = north
        String d = DIRS[Math.floorMod(Math.round(MathHelper.wrapDegrees(mc.player.getYaw()) / 45f), 8)];
        return List.of(String.format(Locale.ROOT, "%s (%.0f)", d, yaw));
    }
}
