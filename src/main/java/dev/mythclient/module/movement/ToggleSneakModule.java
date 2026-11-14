package dev.mythclient.module.movement;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;

/**
 * Press your sneak key once to start sneaking, press again to stop (no holding).
 * Uses Minecraft's own "Toggle Sneak" accessibility option, switched on while this module is enabled
 * and put back to what it was when you disable it.
 */
public final class ToggleSneakModule extends ClientModule {

    private boolean applied;
    private boolean previous;

    public ToggleSneakModule() {
        super("Toggle Sneak", "Tap sneak to keep sneaking, tap again to stop.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        // applied from the first in-world tick (game options aren't ready yet when mods initialise)
        if (!applied && mc.options != null) {
            var option = mc.options.getSneakToggled();
            previous = option.getValue();
            option.setValue(true);
            applied = true;
        }
    }

    @Override
    protected void onDisable() {
        if (applied && mc.options != null) {
            mc.options.getSneakToggled().setValue(previous);
        }
        applied = false;
    }
}
