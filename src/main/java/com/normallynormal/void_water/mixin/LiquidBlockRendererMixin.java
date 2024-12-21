package com.normallynormal.void_water.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.normallynormal.void_water.Config;
import com.normallynormal.void_water.Util;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.client.textures.FluidSpriteCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.client.renderer.block.LiquidBlockRenderer.shouldRenderFace;

@Mixin(LiquidBlockRenderer.class)
public abstract class LiquidBlockRendererMixin {

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void onShouldRenderFace(BlockAndTintGetter level, BlockPos pos, FluidState fluidState, BlockState blockState, Direction side, FluidState neighborFluid, CallbackInfoReturnable<Boolean> cir) {
        int minY = Util.getMinYForLevel();
        if (pos.getY() <= minY && side == Direction.DOWN) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tesselate", at = @At("HEAD"))
    private void onTesselate(BlockAndTintGetter level, BlockPos pos, VertexConsumer buffer, BlockState blockState, FluidState fluidState, CallbackInfo ci) {
        int minY = Util.getMinYForLevel();
        if (pos.getY() == minY) {
            TextureAtlasSprite[] atextureatlassprite = FluidSpriteCache.getFluidSprites(level, pos, fluidState);
            TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
            int tintColor = net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
            float alpha = (float)(tintColor >> 24 & 255) / 255.0F;
            float red = (float)(tintColor >> 16 & 0xFF) / 255.0F;
            float green = (float)(tintColor >> 8 & 0xFF) / 255.0F;
            float blue = (float)(tintColor & 0xFF) / 255.0F;
            float upShade = level.getShade(Direction.UP, true);
            float northShade = level.getShade(Direction.NORTH, true);
            float westShade = level.getShade(Direction.WEST, true);
            Fluid fluid = fluidState.getType();
            BlockState blockstate2 = level.getBlockState(pos.relative(Direction.NORTH));
            FluidState fluidstate2 = blockstate2.getFluidState();
            BlockState blockstate3 = level.getBlockState(pos.relative(Direction.SOUTH));
            FluidState fluidstate3 = blockstate3.getFluidState();
            BlockState blockstate4 = level.getBlockState(pos.relative(Direction.WEST));
            FluidState fluidstate4 = blockstate4.getFluidState();
            BlockState blockstate5 = level.getBlockState(pos.relative(Direction.EAST));
            FluidState fluidstate5 = blockstate5.getFluidState();
            boolean flag3 = shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, fluidstate2);
            boolean flag4 = shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, fluidstate3);
            boolean flag5 = shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, fluidstate4);
            boolean flag6 = shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, fluidstate5);
            float f36 = (float)(pos.getX() & 15);
            float f37 = (float)(pos.getY() & 15);
            float f38 = (float)(pos.getZ() & 15);
            int j = this.getLightColor(level, pos);

            for (Direction direction : Direction.Plane.HORIZONTAL) {
                float f47;
                float f49;
                float f51;
                float f52;
                boolean flag7;
                switch (direction) {
                    case NORTH:
                        f47 = f36;
                        f51 = f36 + 1.0F;
                        f49 = f38 + 0.001F;
                        f52 = f38 + 0.001F;
                        flag7 = flag3;
                        break;
                    case SOUTH:
                        f47 = f36 + 1.0F;
                        f51 = f36;
                        f49 = f38 + 1.0F - 0.001F;
                        f52 = f38 + 1.0F - 0.001F;
                        flag7 = flag4;
                        break;
                    case WEST:
                        f47 = f36 + 0.001F;
                        f51 = f36 + 0.001F;
                        f49 = f38 + 1.0F;
                        f52 = f38;
                        flag7 = flag5;
                        break;
                    default:
                        f47 = f36 + 1.0F - 0.001F;
                        f51 = f36 + 1.0F - 0.001F;
                        f49 = f38;
                        f52 = f38 + 1.0F;
                        flag7 = flag6;
                }
                if (flag7) {
                    BlockPos blockpos = pos.relative(direction);
                    if (atextureatlassprite[2] != null) {
                        if (level.getBlockState(blockpos).shouldDisplayFluidOverlay(level, blockpos, fluidState)) {
                            textureatlassprite2 = atextureatlassprite[2];
                        }
                    }
                    float renderalpha = alpha;
                    for (int yval = -1; yval >= -Config.trailLength; yval--) {
                        float nextrenderalpha = alpha * (1 - ((float) (yval - 1) / -Config.trailLength));
                        nextrenderalpha = (float) Math.pow(nextrenderalpha, Config.trailDecay);

                        if (yval == -Config.trailLength - 1) {
                            nextrenderalpha = 1;
                        }

                        float f56 = textureatlassprite2.getU(0.0F);
                        float f58 = textureatlassprite2.getU(0.5F);
                        float f59 = textureatlassprite2.getV(0.0F);
                        float f31 = textureatlassprite2.getV(0.5F);

                        float f32 = direction.getAxis() == Direction.Axis.Z ? northShade : westShade;
                        float f33 = upShade * f32 * red;
                        float f34 = upShade * f32 * green;
                        float f35 = upShade * f32 * blue;

                        this.vertex(buffer, f47, f37 + 1 + yval + 0.001f, f49, f33, f34, f35, renderalpha, f56, f59, j);
                        this.vertex(buffer, f51, f37 + 1 + yval + 0.001f, f52, f33, f34, f35, renderalpha, f58, f59, j);
                        this.vertex(buffer, f51, f37 + yval + 0.001f, f52, f33, f34, f35, nextrenderalpha, f58, f31, j);
                        this.vertex(buffer, f47, f37 + yval + 0.001f, f49, f33, f34, f35, nextrenderalpha, f56, f31, j);

                        if (textureatlassprite2 != atextureatlassprite[2]) {
                            this.vertex(buffer, f47, f37 + yval + 0.001f, f49, f33, f34, f35, nextrenderalpha, f56, f31, j);
                            this.vertex(buffer, f51, f37 + yval + 0.001f, f52, f33, f34, f35, nextrenderalpha, f58, f31, j);
                            this.vertex(buffer, f51, f37 + 1 + yval + 0.001f, f52, f33, f34, f35, renderalpha, f58, f59, j);
                            this.vertex(buffer, f47, f37 + 1 + yval + 0.001f, f49, f33, f34, f35, renderalpha, f56, f59, j);
                        }

                        renderalpha = nextrenderalpha;
                    }
                }
            }
        }
    }

    private void vertex(
            VertexConsumer vertexConsumer,
            float x,
            float y,
            float z,
            float red,
            float green,
            float blue,
            float alpha,
            float u,
            float v,
            int light
    ) {
        vertexConsumer.addVertex(x, y, z)
                .setColor(red, green, blue, alpha)
                .setUv(u, v)
                .setLight(light)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static boolean isNeighborSameFluid(FluidState firstState, FluidState secondState) {
        return secondState.getType().isSame(firstState.getType());
    }

    private static boolean isFaceOccludedByState(BlockGetter level, Direction face, float height, BlockPos pos, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0, 0.0, 0.0, 1.0, (double)height, 1.0);
            VoxelShape voxelshape1 = state.getOcclusionShape(level, pos);
            return Shapes.blockOccudes(voxelshape, voxelshape1, face);
        } else {
            return false;
        }
    }

    private static boolean isFaceOccludedByNeighbor(BlockGetter level, BlockPos pos, Direction side, float height, BlockState blockState) {
        return isFaceOccludedByState(level, side, height, pos.relative(side), blockState);
    }

    private static boolean isFaceOccludedBySelf(BlockGetter level, BlockPos pos, BlockState state, Direction face) {
        return isFaceOccludedByState(level, face.getOpposite(), 1.0F, pos, state);
    }

    private int getLightColor(BlockAndTintGetter level, BlockPos pos) {
        int i = LevelRenderer.getLightColor(level, pos);
        int j = LevelRenderer.getLightColor(level, pos.above());
        int k = i & 0xFF;
        int l = j & 0xFF;
        int i1 = i >> 16 & 0xFF;
        int j1 = j >> 16 & 0xFF;
        return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
    }

    private float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return this.getHeight(level, fluid, pos, blockstate, blockstate.getFluidState());
    }

    private float getHeight(BlockAndTintGetter level, Fluid fluid, BlockPos pos, BlockState blockState, FluidState fluidState) {
        if (fluid.isSame(fluidState.getType())) {
            BlockState blockstate = level.getBlockState(pos.above());
            return fluid.isSame(blockstate.getFluidState().getType()) ? 1.0F : fluidState.getOwnHeight();
        } else {
            return !blockState.isSolid() ? 0.0F : -1.0F;
        }
    }

    private float calculateAverageHeight(BlockAndTintGetter level, Fluid fluid, float currentHeight, float height1, float height2, BlockPos pos) {
        if (!(height2 >= 1.0F) && !(height1 >= 1.0F)) {
            float[] afloat = new float[2];
            if (height2 > 0.0F || height1 > 0.0F) {
                float f = this.getHeight(level, fluid, pos);
                if (f >= 1.0F) {
                    return 1.0F;
                }

                this.addWeightedHeight(afloat, f);
            }

            this.addWeightedHeight(afloat, currentHeight);
            this.addWeightedHeight(afloat, height2);
            this.addWeightedHeight(afloat, height1);
            return afloat[0] / afloat[1];
        } else {
            return 1.0F;
        }
    }

    private void addWeightedHeight(float[] output, float height) {
        if (height >= 0.8F) {
            output[0] += height * 10.0F;
            output[1] += 10.0F;
        } else if (height >= 0.0F) {
            output[0] += height;
            output[1]++;
        }
    }
}