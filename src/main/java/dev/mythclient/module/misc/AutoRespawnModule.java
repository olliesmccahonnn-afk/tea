package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.NumberSetting;
import net.minecraft.client.gui.screen.DeathScreen;

/** Clicks Respawn for you after a short delay, so you still get a moment to read the death screen. */
public class AutoRespawnModule extends ClientModule {
    private final NumberSetting delay = (NumberSetting) setting(new NumberSetting("Delay (ticks)", 10, 0, 60, 5));
    private int waited;

    public AutoRespawnModule() {
        super("Auto Respawn", "Respawns automatically when you die.", Category.MISC);
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.player == null || !(mc.currentScreen instanceof DeathScreen)) { waited = 0; return; }
        if (++waited > delay.intValue()) {
            mc.player.requestRespawn();
            waited = 0;
        }
    }
}
