package dev.mythclient.module.hud;

import net.minecraft.entity.effect.StatusEffectInstance;

import java.util.ArrayList;
import java.util.List;

/** Lists active status effects with level and time left. */
public class PotionHudModule extends HudModule {
    private static final String[] ROMAN = {"I","II","III","IV","V","VI","VII","VIII","IX","X"};

    public PotionHudModule() {
        super("Potion Effects", "Shows active effects and time left.", 4, 100);
    }

    @Override
    protected List<String> lines() {
        List<String> out = new ArrayList<>();
        if (mc.player == null) return out;
        for (StatusEffectInstance e : mc.player.getStatusEffects()) {
            String name = e.getEffectType().value().getName().getString();
            int amp = e.getAmplifier();
            String lvl = amp < ROMAN.length ? ROMAN[amp] : String.valueOf(amp + 1);
            String time;
            if (e.isInfinite()) {
                time = "**:**";
            } else {
                int s = e.getDuration() / 20;
                time = String.format("%d:%02d", s / 60, s % 60);
            }
            out.add(name + " " + lvl + "  " + time);
        }
        return out;
    }
}
