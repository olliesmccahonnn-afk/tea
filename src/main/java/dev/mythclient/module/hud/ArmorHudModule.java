package dev.mythclient.module.hud;

import dev.mythclient.MythClient;
import dev.mythclient.setting.BoolSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.List;

/** Shows worn armor with durability. Movable in the HUD Editor like the other HUD modules. */
public class ArmorHudModule extends HudModule {
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };
    private static final int ROW = 18;

    private final BoolSetting percent = (BoolSetting) setting(new BoolSetting("Durability %", true));
    private final BoolSetting hideEmpty = (BoolSetting) setting(new BoolSetting("Hide empty slots", false));

    public ArmorHudModule() {
        super("Armor", "Shows your armor and its durability.", 4, 40);
    }

    @Override protected List<String> lines() { return List.of(); }

    @Override
    public List<String> editorLines() {
        return List.of("      100%", "      100%", "      100%", "      100%");
    }

    @Override
    public void onHudRender(DrawContext ctx) {
        if (mc.player == null || !MythClient.hudVisible() || mc.options.hudHidden) return;
        List<String> box = editorLines();
        int x = px(widthOf(box));
        int y = py(ROW * SLOTS.length);
        int row = 0;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack s = mc.player.getEquippedStack(slot);
            if (s.isEmpty() && hideEmpty.get()) continue;
            int ry = y + row * ROW;
            if (!s.isEmpty()) {
                ctx.drawItem(s, x, ry);
                ctx.drawStackOverlay(mc.textRenderer, s, x, ry);
                if (percent.get() && s.isDamageable()) {
                    int left = s.getMaxDamage() - s.getDamage();
                    int pct = Math.round(100f * left / s.getMaxDamage());
                    ctx.drawText(mc.textRenderer, pct + "%", x + 20, ry + 4,
                        pct > 50 ? 0xFF55FF55 : pct > 20 ? 0xFFFFAA00 : 0xFFFF5555, true);
                }
            }
            row++;
        }
    }
}
