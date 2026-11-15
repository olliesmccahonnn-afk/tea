package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.BoolSetting;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.item.SignChangingItem;
import net.minecraft.util.ActionResult;

/**
 * Right-clicking a sign no longer opens the edit screen.
 * Cancelled on the client, so no packet is sent to the server.
 */
public class NoSignEditModule extends ClientModule {
    private final BoolSetting sneakToEdit = (BoolSetting) setting(new BoolSetting("Sneak to edit", true));
    private final BoolSetting allowDyes = (BoolSetting) setting(new BoolSetting("Allow dye / wax / ink", true));

    public NoSignEditModule() {
        super("No Sign Edit", "Clicking a sign does not open its edit menu.", Category.MISC);
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (!isEnabled() || !world.isClient()) return ActionResult.PASS;
            if (!(world.getBlockState(hit.getBlockPos()).getBlock() instanceof AbstractSignBlock)) return ActionResult.PASS;
            if (sneakToEdit.get() && player.isSneaking()) return ActionResult.PASS;
            if (allowDyes.get() && player.getStackInHand(hand).getItem() instanceof SignChangingItem) return ActionResult.PASS;
            return ActionResult.FAIL;
        });
    }
}
