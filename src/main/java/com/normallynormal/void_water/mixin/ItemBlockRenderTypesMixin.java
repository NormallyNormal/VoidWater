package com.normallynormal.void_water.mixin;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockRenderTypes.class)
public class ItemBlockRenderTypesMixin {
    @Inject(method = "getRenderLayer", at = @At("HEAD"), cancellable = true)
    private static void getRenderLayer(FluidState fluidState, CallbackInfoReturnable<RenderType> cir) {
        cir.setReturnValue(RenderType.translucent());
    }
}
