package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.module.misc.OptionModule;

/** Forces particles to Minimal while enabled (helps FPS in big fights). Restores your setting on disable. */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ReducedParticlesModule extends OptionModule {
    public ReducedParticlesModule() {
        super("Reduced Particles", "Sets particles to Minimal.", Category.RENDER,
              () -> mc.options.getParticles(), null);
    }

    @Override protected Object target() { return enumConst(mc.options.getParticles(), "MINIMAL"); }
}
