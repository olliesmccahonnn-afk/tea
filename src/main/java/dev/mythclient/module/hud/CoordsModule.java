package dev.mythclient.module.hud;

import java.util.List;

/** Your block position. Drag it anywhere in the HUD Editor. */
public final class CoordsModule extends HudModule {

    public CoordsModule() {
        super("Coordinates", "Shows your block position. Move it in the HUD Editor.", 4, 28);
    }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        return List.of("XYZ: " + mc.player.getBlockX() + " " + mc.player.getBlockY() + " " + mc.player.getBlockZ());
    }
}
