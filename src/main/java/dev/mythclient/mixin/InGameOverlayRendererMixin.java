package dev.mythclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mythclient.module.render.LowFireModule;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    // If this target is not static in your version, remove `static` from the handler.
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), require = 0)
    private static void mythclient$lowFire(CallbackInfo ci, @Local(argsOnly = true) MatrixStack matrices) {
        if (LowFireModule.active()) matrices.translate(0.0F, -LowFireModule.INSTANCE.offset(), 0.0F);
    }
}
