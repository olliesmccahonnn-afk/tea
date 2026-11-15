package dev.mythclient.mixin;

import dev.mythclient.addons.gui.NatureGuiScreen;
import dev.mythclient.gui.ClickGuiScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Swaps the base client's menu for the nature menu whenever it is about to open. */
@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {
    @ModifyVariable(method = "setScreen", at = @At("HEAD"), argsOnly = true, require = 0)
    private Screen myth$natureGui(Screen screen) {
        if (screen instanceof ClickGuiScreen) {
            return new NatureGuiScreen(MinecraftClient.getInstance().currentScreen);
        }
        return screen;
    }
}
