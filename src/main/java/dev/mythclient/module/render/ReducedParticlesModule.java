package dev.mythclient.module.render;

import dev.mythclient.core.Category;
import dev.mythclient.module.misc.OptionModule;
import net.minecraft.client.option.ParticlesMode;

/** Forces particles to Minimal while enabled (helps FPS in big fights). Restores your setting on disable. */
public class ReducedParticlesModule extends OptionModule<ParticlesMode> {
    public ReducedParticlesModule() {
        super("Reduced Particles", "Sets particles to Minimal.", Category.RENDER,
              () -> mc.options.getParticles(), ParticlesMode.MINIMAL);
    }
}
