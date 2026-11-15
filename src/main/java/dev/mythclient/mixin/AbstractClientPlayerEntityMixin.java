package dev.mythclient.mixin;

import dev.mythclient.module.render.CosmeticsModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayerEntity.class)
public class AbstractClientPlayerEntityMixin {
    /** Swaps the local player's skin/cape textures. Other players are untouched. */
    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true, require = 0)
    private void mythclient$cosmetics(CallbackInfoReturnable<SkinTextures> cir) {
        if (CosmeticsModule.active() && (Object) this == MinecraftClient.getInstance().player) {
            cir.setReturnValue(CosmeticsModule.INSTANCE.apply(cir.getReturnValue()));
        }
    }
}
