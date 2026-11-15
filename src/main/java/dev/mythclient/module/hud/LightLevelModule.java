package dev.mythclient.module.hud;

import java.util.List;

/** Light level at your feet (mobs can spawn at 0 on current versions). */
public class LightLevelModule extends HudModule {
    public LightLevelModule() { super("Light Level", "Shows the light level where you stand.", 180, 88); }

    @Override
    protected List<String> lines() {
        if (mc.player == null || mc.world == null) return List.of();
        return List.of("Light: " + mc.world.getLightLevel(mc.player.getBlockPos()));
    }
}
