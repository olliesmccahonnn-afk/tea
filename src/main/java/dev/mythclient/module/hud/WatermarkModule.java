package dev.mythclient.module.hud;

import dev.mythclient.MythClient;

import java.util.List;

public final class WatermarkModule extends HudModule {

    public WatermarkModule() {
        super("Watermark", "Shows the client name and version.", 4, 4);
    }

    @Override
    protected List<String> lines() {
        return List.of(MythClient.NAME + " " + MythClient.VERSION);
    }
}
