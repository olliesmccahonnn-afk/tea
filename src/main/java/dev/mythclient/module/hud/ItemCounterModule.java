package dev.mythclient.module.hud;

import net.minecraft.item.ItemStack;

import java.util.List;

/** Counts how many of the item in your hand you carry in total. */
public class ItemCounterModule extends HudModule {
    public ItemCounterModule() { super("Item Counter", "Total count of the item you are holding.", 180, 64); }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        ItemStack held = mc.player.getMainHandStack();
        if (held.isEmpty()) return List.of();
        return List.of(held.getName().getString() + ": " + mc.player.getInventory().count(held.getItem()));
    }
}
