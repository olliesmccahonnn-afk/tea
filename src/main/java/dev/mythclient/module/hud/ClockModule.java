package dev.mythclient.module.hud;

import dev.mythclient.setting.BoolSetting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClockModule extends HudModule {
    private final BoolSetting twelveHour = (BoolSetting) setting(new BoolSetting("12-hour", false));
    private static final DateTimeFormatter H24 = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter H12 = DateTimeFormatter.ofPattern("h:mm a");

    public ClockModule() { super("Clock", "Shows your real-world time.", 90, 16); }

    @Override
    protected List<String> lines() {
        return List.of(LocalTime.now().format(twelveHour.get() ? H12 : H24));
    }
}
