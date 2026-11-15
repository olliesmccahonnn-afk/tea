package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.NumberSetting;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;

/** Stops you breaking blocks with a tool that is about to break. Cancelled client-side. */
public class ToolSaverModule extends ClientModule {
    private final NumberSetting minLeft = (NumberSetting) setting(new NumberSetting("Min durability left", 5, 1, 50, 1));

    public ToolSaverModule() {
        super("Tool Saver", "Blocks mining with nearly broken tools.", Category.MISC);
        AttackBlockCallback.EVENT.register((player, world, hand, pos, dir) -> {
            if (!isEnabled() || !world.isClient()) return ActionResult.PASS;
            ItemStack s = player.getStackInHand(hand);
            if (s.isDamageable() && s.getMaxDamage() - s.getDamage() <= minLeft.intValue()) {
                player.sendMessage(Text.literal("Tool Saver: " + s.getName().getString() + " is almost broken"), true);
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }
}
