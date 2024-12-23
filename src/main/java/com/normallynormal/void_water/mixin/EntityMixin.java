package com.normallynormal.void_water.mixin;

import com.normallynormal.void_water.InterimCalculation;
import com.normallynormal.void_water.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.extensions.IEntityExtension;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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

        Set<FluidType> fluidsIn = new HashSet<>();

        if (entityTop < levelFloor) {
            for (int x = entityMinX; x < entityMaxX; x++) {
                for (int z = entityMinZ; z < entityMaxZ; z++) {
                    mutableBlockPos.set(x, levelFloor, z);
                    FluidState fluidstate = this.level().getFluidState(mutableBlockPos);
                    net.neoforged.neoforge.fluids.FluidType fluidType = fluidstate.getFluidType();
                    if (!fluidType.isAir() && fluidType.canPushEntity((Entity)(Object)this) && !fluidsIn.contains(fluidType)) {
                        double scale = ((IEntityExtension) this).getFluidMotionScale(fluidType);
                        modifiedFlowVector = modifiedFlowVector.add(0, -scale, 0);
                        fluidsIn.add(fluidType);
                    }
                }
            }
            Vec3 deltaMovement = this.getDeltaMovement();
            this.setDeltaMovement(deltaMovement.add(modifiedFlowVector));
        }
    }

//    @Overwrite
//    public void updateFluidHeightAndDoFluidPushing() {
//        if (((Entity)(Object)this).touchingUnloadedChunk()) {
//            return;
//        } else {
//            AABB aabb = ((Entity)(Object)this).getBoundingBox().deflate(0.001);
//            int i = Mth.floor(aabb.minX);
//            int j = Mth.ceil(aabb.maxX);
//            int k = Mth.floor(aabb.minY);
//            int l = Mth.ceil(aabb.maxY);
//            int i1 = Mth.floor(aabb.minZ);
//            int j1 = Mth.ceil(aabb.maxZ);
//            double d0 = 0.0;
//            boolean flag = ((Entity)(Object)this).isPushedByFluid();
//            boolean flag1 = false;
//            Vec3 vec3 = Vec3.ZERO;
//            int k1 = 0;
//            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
//            it.unimi.dsi.fastutil.objects.Object2ObjectMap<net.neoforged.neoforge.fluids.FluidType, InterimCalculation> interimCalcs = null;
//            int levelFloor = Util.getMinYForLevel(this.level());
//
//            for (int l1 = i; l1 < j; l1++) {
//                for (int i2 = k; i2 < l; i2++) {
//                    for (int j2 = i1; j2 < j1; j2++) {
//                        blockpos$mutableblockpos.set(l1, i2, j2);
//                        FluidState fluidstate = this.level().getFluidState(blockpos$mutableblockpos);
//                        net.neoforged.neoforge.fluids.FluidType fluidType = fluidstate.getFluidType();
//                        if (!fluidType.isAir()) {
//                            double d1 = (double)((float)i2 + fluidstate.getHeight(this.level(), blockpos$mutableblockpos));
//                            if (d1 >= aabb.minY) {
//                                flag1 = true;
//                                if (interimCalcs == null) {
//                                    interimCalcs = new it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap<>();
//                                }
//                                InterimCalculation interim = interimCalcs.computeIfAbsent(fluidType, t -> new InterimCalculation());
//                                interim.fluidHeight = Math.max(d1 - aabb.minY, interim.fluidHeight);
//                                if (((Entity)(Object)this).isPushedByFluid(fluidType)) {
//                                    Vec3 vec31 = fluidstate.getFlow(this.level(), blockpos$mutableblockpos);
//                                    if (interim.fluidHeight < 0.4D) {
//                                        vec31 = vec31.scale(interim.fluidHeight);
//                                    }
//
//                                    interim.flowVector = interim.flowVector.add(vec31);
//                                    interim.blockCount++;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//            if(interimCalcs != null) {
//                interimCalcs.forEach((fluidType, interim) -> {
//                    if (interim.flowVector.length() > 0.0D) {
//                        if (interim.blockCount > 0) {
//                            interim.flowVector = interim.flowVector.scale(1.0D / (double)interim.blockCount);
//                        }
//
//                        if (!(((Entity)(Object)this) instanceof Player)) {
//                            interim.flowVector = interim.flowVector.normalize();
//                        }
//
//                        Vec3 vec32 = this.getDeltaMovement();
//                        interim.flowVector = interim.flowVector.scale(((Entity)(Object)this).getFluidMotionScale(fluidType));
//                        double d2 = 0.003;
//                        if (Math.abs(vec32.x) < 0.003D && Math.abs(vec32.z) < 0.003D && interim.flowVector.length() < 0.0045000000000000005D) {
//                            interim.flowVector = interim.flowVector.normalize().scale(0.0045000000000000005D);
//                        }
//
//                        this.setDeltaMovement(this.getDeltaMovement().add(interim.flowVector));
//                    }
//
//                    ((EntityInvoker)(Object)this).invokeSetFluidTypeHeight(fluidType, interim.fluidHeight);
//                });
//            }
//        }
//    }

    @ModifyVariable(method = "updateFluidOnEyes()V", at = @At("STORE"), ordinal = 0)
    private double modifyD0(double d0) {
        int minY = Util.getMinYForLevel(this.level());
        if (d0 < minY) {
            return minY + 0.01;
        }
        return d0;
    }
}
