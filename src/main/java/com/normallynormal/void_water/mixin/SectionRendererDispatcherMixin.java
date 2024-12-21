package com.normallynormal.void_water.mixin;

import com.normallynormal.void_water.Config;
import com.normallynormal.void_water.Util;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.lang.reflect.Field;

@Mixin(SectionRenderDispatcher.RenderSection.class)
public class SectionRendererDispatcherMixin {

    private static Field bbField;

    static {
        try {
            bbField = SectionRenderDispatcher.RenderSection.class.getDeclaredField("bb");
            bbField.setAccessible(true);
        } catch (NoSuchFieldException ignored) {

        }
    }

    @Redirect(method = "setOrigin(III)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/chunk/SectionRenderDispatcher$RenderSection;bb:Lnet/minecraft/world/phys/AABB;"))
    private void modifyBB(SectionRenderDispatcher.RenderSection renderSection, AABB aabb) {
        int x = renderSection.getOrigin().getX();
        int y = renderSection.getOrigin().getY();
        int z = renderSection.getOrigin().getZ();
        int minY = Util.getMinYForLevel();
        AABB newAABB = new AABB(x, (y == minY ? y - Config.trailLength : y), z,
                x + 16,
                y + 16,
                z + 16);

        try {
            bbField.set(renderSection, newAABB);
        } catch (IllegalAccessException ignored) {

        }
    }
}
