package dev.mythclient.module.hud;

import dev.mythclient.setting.NumberSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/** Warns when held gear or armor drops below a durability threshold. Empty when everything is fine. */
public class DurabilityWarningModule extends HudModule {
    private final NumberSetting threshold = (NumberSetting) setting(new NumberSetting("Warn below %", 10, 1, 50, 1));
    private static final EquipmentSlot[] SLOTS = {
        EquipmentSlot.MAINHAND, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public DurabilityWarningModule() { super("Durability Warning", "Warns about low durability.", 180, 52); }

    @Override
    protected List<String> lines() {
        List<String> out = new ArrayList<>();
        if (mc.player == null) return out;
        for (EquipmentSlot slot : SLOTS) {
            ItemStack s = mc.player.getEquippedStack(slot);
            if (s.isEmpty() || !s.isDamageable()) continue;
            int pct = Math.round(100f * (s.getMaxDamage() - s.getDamage()) / s.getMaxDamage());
            if (pct < threshold.intValue()) out.add("! " + s.getName().getString() + " " + pct + "%");
        }
        return out;
    }
}
