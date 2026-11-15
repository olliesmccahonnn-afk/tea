package dev.mythclient.mixin;

import dev.mythclient.module.render.NoHurtCamModule;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true, require = 0)
    private void mythclient$noHurtCam(CallbackInfo ci) {
        if (NoHurtCamModule.active()) ci.cancel();
    }
}
