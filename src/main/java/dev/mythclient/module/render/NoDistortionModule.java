package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.module.misc.OptionModule;

/** Removes the screen warping from nausea and nether portals. */
public class NoDistortionModule extends OptionModule<Double> {
    public NoDistortionModule() {
        super("No Distortion", "Disables nausea and portal screen warping.", Category.RENDER,
              () -> mc.options.getDistortionEffectScale(), 0.0);
    }
}
