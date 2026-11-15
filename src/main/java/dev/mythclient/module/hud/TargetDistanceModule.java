package dev.mythclient.module.hud;

import net.minecraft.util.hit.HitResult;

import java.util.List;
import java.util.Locale;

/** Distance from your eyes to whatever your crosshair is on. Empty when you are looking at nothing in range. */
public class TargetDistanceModule extends HudModule {
    public TargetDistanceModule() { super("Target Distance", "Shows the distance to what you are looking at.", 270, 60); }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        HitResult h = mc.crosshairTarget;
        if (h == null || h.getType() == HitResult.Type.MISS) return List.of();
        double d = h.getPos().distanceTo(mc.player.getEyePos());
        String what = h.getType() == HitResult.Type.ENTITY ? "Entity" : "Block";
        return List.of(String.format(Locale.ROOT, "%s: %.2f", what, d));
    }
}
