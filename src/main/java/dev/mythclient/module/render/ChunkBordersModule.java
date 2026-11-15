package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;

/**
 * Shows vanilla's chunk border overlay (same as F3+G).
 * NOTE: the debug renderer was reworked around 1.21.9+. If the call below does not compile
 * on 1.21.11, check DebugRenderer in your mappings for the chunk-border toggle; this is the
 * only line that may need changing.
 */
public class ChunkBordersModule extends ClientModule {
    private boolean shown;

    public ChunkBordersModule() {
        super("Chunk Borders", "Shows chunk boundaries around you.", Category.RENDER);
    }

    private void set(boolean want) {
        if (shown != want) {
            mc.debugRenderer.toggleShowChunkBorder();
            shown = want;
        }
    }

    @Override protected void onEnable() { set(true); }
    @Override protected void onDisable() { set(false); }
}
