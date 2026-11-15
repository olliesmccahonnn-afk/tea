package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.module.misc.OptionModule;

/** Removes the screen pulsing from the Darkness effect. */
public class NoDarknessModule extends OptionModule<Double> {
    public NoDarknessModule() {
        super("No Darkness", "Disables the Darkness effect pulsing.", Category.RENDER,
              () -> mc.options.getDarknessEffectScale(), 0.0);
    }
}
