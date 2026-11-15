package dev.mythclient.module.misc;

import dev.mythclient.core.Category;

/** Turns on vanilla auto jump while enabled. */
public class AutoJumpModule extends BoolOptionModule {
    public AutoJumpModule() {
        super("Auto Jump", "Automatically jumps up single blocks.", Category.MOVEMENT,
              () -> mc.options.getAutoJump(), true);
    }
}
