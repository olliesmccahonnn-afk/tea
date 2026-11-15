package dev.mythclient.mixin;

import dev.mythclient.module.render.FreecamModule;
import dev.mythclient.module.render.FreelookModule;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public class CameraMixin {
    private static final String SET_ROTATION = "Lnet/minecraft/client/render/Camera;setRotation(FF)V";
    private static final String SET_POS = "Lnet/minecraft/client/render/Camera;setPos(DDD)V";

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = SET_ROTATION), index = 0, require = 0)
    private float mythclient$yaw(float yaw) {
        if (FreecamModule.active()) return FreecamModule.yaw;
        return FreelookModule.active() ? FreelookModule.cameraYaw : yaw;
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = SET_ROTATION), index = 1, require = 0)
    private float mythclient$pitch(float pitch) {
        if (FreecamModule.active()) return FreecamModule.pitch;
        return FreelookModule.active() ? FreelookModule.cameraPitch : pitch;
    }

    // x is the first argument, so this is also where the camera advances once per frame.
    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = SET_POS), index = 0, require = 0)
    private double mythclient$x(double x) {
        if (!FreecamModule.active()) return x;
        FreecamModule.frame();
        return FreecamModule.x;
    }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = SET_POS), index = 1, require = 0)
    private double mythclient$y(double y) { return FreecamModule.active() ? FreecamModule.y : y; }

    @ModifyArg(method = "update", at = @At(value = "INVOKE", target = SET_POS), index = 2, require = 0)
    private double mythclient$z(double z) { return FreecamModule.active() ? FreecamModule.z : z; }

    /** Makes your own character render while the camera is away from it. */
    @Inject(method = "isThirdPerson", at = @At("HEAD"), cancellable = true, require = 0)
    private void mythclient$showBody(CallbackInfoReturnable<Boolean> cir) {
        if (FreecamModule.active()) cir.setReturnValue(true);
    }
}
