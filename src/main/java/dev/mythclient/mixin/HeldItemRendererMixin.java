package dev.mythclient.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.mythclient.module.render.ItemPositionModule;
import dev.mythclient.module.render.LowShieldModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {
    @Inject(method = "renderFirstPersonItem", at = @At("HEAD"), require = 0)
    private void mythclient$adjust(CallbackInfo ci,
                                   @Local(argsOnly = true) ItemStack stack,
                                   @Local(argsOnly = true) MatrixStack matrices) {
        if (ItemPositionModule.active()) {
            ItemPositionModule m = ItemPositionModule.INSTANCE;
            matrices.translate(m.dx(), m.dy(), m.dz());
        }
        if (LowShieldModule.active() && stack.isOf(Items.SHIELD)) {
            var p = MinecraftClient.getInstance().player;
            if (!LowShieldModule.INSTANCE.onlyBlocking() || (p != null && p.isBlocking())) {
                matrices.translate(0.0F, -LowShieldModule.INSTANCE.offset(), 0.0F);
            }
        }
    }
}
