package dev.mythclient.mixin;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets modules write option values past the vanilla slider range (gamma > 1, FOV < 30). */
@Mixin(SimpleOption.class)
public interface SimpleOptionAccessor<T> {
    @Accessor("value")
    void mythclient$setRaw(T value);
}
