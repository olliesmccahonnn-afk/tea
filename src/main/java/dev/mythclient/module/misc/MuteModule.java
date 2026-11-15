package dev.mythclient.module.misc;

import dev.mythclient.core.Category;
import net.minecraft.sound.SoundCategory;

/** Sets master volume to 0 while enabled. Restores your volume on disable. */
public class MuteModule extends OptionModule<Double> {
    public MuteModule() {
        super("Mute", "Mutes all game sound.", Category.MISC,
              () -> mc.options.getSoundVolumeOption(SoundCategory.MASTER), 0.0);
    }
}
