package dev.mythclient.module.hud;

import java.util.List;

public final class FpsModule extends HudModule {

    public FpsModule() {
        super("FPS", "Shows your frames per second.", 4, 16);
    }

    @Override
    protected List<String> lines() {
        return List.of("FPS: " + mc.getCurrentFps());
    }
}
