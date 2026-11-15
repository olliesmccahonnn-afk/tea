package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.core.ClientModule;

/** Hides rain and thunder visuals by zeroing the client's weather strength every tick. */
public class NoWeatherModule extends ClientModule {
    public NoWeatherModule() {
        super("No Weather", "Hides rain, snow and thunder visuals.", Category.RENDER);
    }

    @Override
    public void onTick() {
        if (!isEnabled() || mc.world == null) return;
        mc.world.setRainGradient(0.0F);
        mc.world.setThunderGradient(0.0F);
    }
}
