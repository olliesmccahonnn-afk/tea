package dev.mythclient.module.hud;

import net.minecraft.client.network.PlayerListEntry;

import java.util.List;

public class PingModule extends HudModule {
    public PingModule() { super("Ping", "Shows your latency to the server.", 90, 4); }

    @Override
    protected List<String> lines() {
        if (mc.player == null || mc.getNetworkHandler() == null) return List.of();
        PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return List.of("Ping: " + (e == null ? "--" : e.getLatency() + " ms"));
    }
}
