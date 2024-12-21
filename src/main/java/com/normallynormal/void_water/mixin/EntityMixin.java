package com.normallynormal.void_water.mixin;

import com.normallynormal.void_water.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow
    private BlockPos blockPosition;

    @Shadow
    public Level level() {
        return null;
    }

    @Shadow
    public Vec3 getDeltaMovement() {
        return null;
    }

    @Shadow
    public void setDeltaMovement(Vec3 deltaMovement) {

    }

    @Shadow
    private AABB bb;

    @Inject(
            method = "updateFluidHeightAndDoFluidPushing()V",
            at = @At("RETURN")
    )
    private void modifyFluidPushing(CallbackInfo ci) {
        int levelFloor = Util.getMinYForLevel(this.level());
        double entityTop = bb.maxY;
        int entityMinX = Mth.floor(bb.minX);
        int entityMaxX = Mth.ceil(bb.maxX);
        int entityMinZ = Mth.floor(bb.minZ);
        int entityMaxZ = Mth.ceil(bb.maxZ);

        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        Vec3 modifiedFlowVector = Vec3.ZERO;

        if (entityTop < levelFloor) {
            for (int x = entityMinX; x < entityMaxX; x++) {
                for (int z = entityMinZ; z < entityMaxZ; z++) {
                    mutableBlockPos.set(x, levelFloor, z);
                    FluidState fluidstate = this.level().getFluidState(mutableBlockPos);
                    net.neoforged.neoforge.fluids.FluidType fluidType = fluidstate.getFluidType();
                    if (!fluidType.isAir() && fluidType.canPushEntity((Entity)(Object)this)) {
                        double scale = ((IEntityExtension) this).getFluidMotionScale(fluidType);
                        modifiedFlowVector = modifiedFlowVector.add(0, -scale, 0);
                    }
                }
            }
            Vec3 deltaMovement = this.getDeltaMovement();
            this.setDeltaMovement(deltaMovement.add(modifiedFlowVector));
        }
    }

    @ModifyVariable(method = "updateFluidOnEyes()V", at = @At("STORE"), ordinal = 0)
    private double modifyD0(double d0) {
        int minY = Util.getMinYForLevel(this.level());
        if (d0 < minY) {
            return minY + 0.01;
        }
        return d0;
    }
}
