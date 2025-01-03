package com.normallynormal.void_water.mixin;

import com.normallynormal.void_water.Config;
import com.normallynormal.void_water.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Level.class)
public class LevelMixin {
    @ModifyVariable(method = "getFluidState", at = @At("HEAD"))
    private BlockPos injected(BlockPos y) {
        int minY = Util.getMinYForLevel((Level) (Object) this);
        if (y.getY() < minY && y.getY() > minY - Config.trailLength) {
            return y.atY(minY);
        }
        return y;
    }
}
