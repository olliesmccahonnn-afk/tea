package dev.mythclient.module.hud;

import java.util.List;

public class BiomeModule extends HudModule {
    public BiomeModule() { super("Biome", "Shows the biome you are in.", 180, 76); }

    @Override
    protected List<String> lines() {
        if (mc.player == null || mc.world == null) return List.of();
        String id = mc.world.getBiome(mc.player.getBlockPos()).getKey()
            .map(k -> k.getValue().getPath()).orElse("unknown");
        StringBuilder sb = new StringBuilder("Biome: ");
        for (String part : id.split("_")) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
        }
        return List.of(sb.toString().trim());
    }
}
