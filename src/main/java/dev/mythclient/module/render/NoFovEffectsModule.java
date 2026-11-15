package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.module.misc.OptionModule;

/** Stops sprinting / speed effects from changing your FOV. */
public class NoFovEffectsModule extends OptionModule<Double> {
    public NoFovEffectsModule() {
        super("No FOV Effects", "Sprinting and speed effects no longer change your FOV.", Category.RENDER,
              () -> mc.options.getFovEffectScale(), 0.0);
    }
}
