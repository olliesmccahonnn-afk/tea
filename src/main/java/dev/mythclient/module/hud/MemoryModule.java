package dev.mythclient.module.hud;

import java.util.List;

/** Java heap usage. */
public class MemoryModule extends HudModule {
    public MemoryModule() { super("Memory", "Shows memory used by the game.", 270, 84); }

    @Override
    protected List<String> lines() {
        Runtime rt = Runtime.getRuntime();
        long usedMb = (rt.totalMemory() - rt.freeMemory()) / 1_048_576L;
        long maxMb = rt.maxMemory() / 1_048_576L;
        return List.of("Mem: " + usedMb + "/" + maxMb + " MB (" + (usedMb * 100L / Math.max(1L, maxMb)) + "%)");
    }
}
