package dev.mythclient.mixin;

import dev.mythclient.module.render.HealthHeartsModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true, require = 0)
    private void mythclient$hearts(CallbackInfoReturnable<Text> cir) {
        if (HealthHeartsModule.active()) {
            cir.setReturnValue(HealthHeartsModule.INSTANCE.decorate(cir.getReturnValue(), (PlayerEntity) (Object) this));
        }
    }
}
