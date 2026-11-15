package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;
import dev.mythclient.setting.BoolSetting;
import dev.mythclient.setting.ModeSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Locale;

/**
 * Adds a row of hearts to other players' nametags showing their current health.
 * Uses the health value the server already sends to your client; nothing is hidden or revealed
 * that the game doesn't already know. The nametag text is edited in PlayerEntityMixin.
 */
public class HealthHeartsModule extends ClientModule {
    public static HealthHeartsModule INSTANCE;

    private final ModeSetting style = (ModeSetting) setting(new ModeSetting("Style", "Hearts", "Hearts", "Number", "Both"));
    private final BoolSetting showEmpty = (BoolSetting) setting(new BoolSetting("Show empty hearts", false));
    private final BoolSetting absorption = (BoolSetting) setting(new BoolSetting("Show absorption", true));

    public HealthHeartsModule() {
        super("Health Hearts", "Shows hearts on other players' nametags.", Category.RENDER);
        INSTANCE = this;
    }

    public static boolean active() { return INSTANCE != null && INSTANCE.isEnabled(); }

    /** Returns the player's display name with the health indicator appended. */
    public Text decorate(Text name, PlayerEntity p) {
        if (p == mc.player || p.isDead() || p.isSpectator()) return name;

        float health = Math.max(0f, p.getHealth());
        float max = Math.max(1f, p.getMaxHealth());
        MutableText out = Text.empty().append(name).append(Text.literal(" "));

        if (!style.is("Number")) {
            int halves = Math.round(Math.min(1f, health / max) * 20f);      // 0..20 half-hearts
            int full = halves / 2;
            boolean half = halves % 2 == 1;
            int empty = 10 - full - (half ? 1 : 0);
            out.append(Text.literal("\u2764".repeat(full)).formatted(Formatting.RED));
            if (half) out.append(Text.literal("\u2764").formatted(Formatting.DARK_RED));
            if (showEmpty.get() && empty > 0) {
                out.append(Text.literal("\u2764".repeat(empty)).formatted(Formatting.DARK_GRAY));
            }
        }
        if (!style.is("Hearts")) {
            if (!style.is("Number")) out.append(Text.literal(" "));
            out.append(Text.literal(String.format(Locale.ROOT, "%.1f", health / 2f)).formatted(Formatting.RED));
        }
        float abs = p.getAbsorptionAmount();
        if (absorption.get() && abs > 0f) {
            out.append(Text.literal(String.format(Locale.ROOT, " +%.1f", abs / 2f)).formatted(Formatting.GOLD));
        }
        return out;
    }
}
