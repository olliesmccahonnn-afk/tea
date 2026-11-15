package dev.mythclient.module.hud;

import java.util.List;
import java.util.Locale;

/** In-game clock (0 ticks = 6:00). */
public class GameTimeModule extends HudModule {
    public GameTimeModule() { super("Game Time", "Shows the in-game time of day.", 180, 100); }

    @Override
    protected List<String> lines() {
        if (mc.world == null) return List.of();
        long t = mc.world.getTimeOfDay() % 24000L;
        long hours = (t / 1000L + 6L) % 24L;
        long minutes = (t % 1000L) * 60L / 1000L;
        return List.of(String.format(Locale.ROOT, "Time: %02d:%02d", hours, minutes));
    }
}
