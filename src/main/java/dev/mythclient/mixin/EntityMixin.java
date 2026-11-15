package dev.mythclient.mixin;

import dev.mythclient.module.render.FreecamModule;
import dev.mythclient.module.render.FreelookModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    /** While freecam or freelook is on, mouse movement rotates the camera instead of the player. */
    @Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true, require = 0)
    private void mythclient$cameraMouse(double dx, double dy, CallbackInfo ci) {
        if ((Object) this != MinecraftClient.getInstance().player) return;
        float ddx = (float) dx * 0.15F, ddy = (float) dy * 0.15F;
        if (FreecamModule.active()) {
            FreecamModule.yaw += ddx;
            FreecamModule.pitch = Math.max(-90F, Math.min(90F, FreecamModule.pitch + ddy));
            ci.cancel();
        } else if (FreelookModule.active()) {
            FreelookModule.cameraYaw += ddx;
            FreelookModule.cameraPitch = Math.max(-90F, Math.min(90F, FreelookModule.cameraPitch + ddy));
            ci.cancel();
        }
    }
}
