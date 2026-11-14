package dev.mythclient.module.movement;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;

/** Keeps you sprinting while you hold forward. */
public final class ToggleSprintModule extends ClientModule {

    public ToggleSprintModule() {
        super("Toggle Sprint", "Sprint automatically while moving forward.", Category.MOVEMENT);
    }

    @Override
    public void onTick() {
        var player = mc.player;
        if (player == null) return;
        if (mc.options.forwardKey.isPressed()
                && !player.isSneaking()
                && !player.isUsingItem()
                && player.getHungerManager().getFoodLevel() > 6) {
            player.setSprinting(true);
        }
    }
}
