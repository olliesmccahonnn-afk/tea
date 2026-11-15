package dev.mythclient.module.hud;

import java.util.List;

/** Shows a warning when every inventory slot is occupied. Empty otherwise. */
public class InventoryFullModule extends HudModule {
    public InventoryFullModule() { super("Inventory Full", "Warns when your inventory has no free slot.", 270, 96); }

    @Override
    protected List<String> lines() {
        if (mc.player == null) return List.of();
        return mc.player.getInventory().getEmptySlot() == -1 ? List.of("Inventory full!") : List.of();
    }
}
