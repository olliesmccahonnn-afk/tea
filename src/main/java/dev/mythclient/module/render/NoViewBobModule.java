package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.module.misc.BoolOptionModule;

/** Turns off view bobbing while enabled. */
public class NoViewBobModule extends BoolOptionModule {
    public NoViewBobModule() {
        super("No View Bob", "Disables camera bobbing while walking.", Category.RENDER,
              () -> mc.options.getBobView(), false);
    }
}
