package com.normallynormal.void_water;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = VoidWaterMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.IntValue TRAIL_LENGTH = BUILDER
            .comment("Length that liquids extend into the void. Must be an integer greater than or equal to 0.")
            .defineInRange("liquidTrailLength", 64, 0, Integer.MAX_VALUE);

    private static final ModConfigSpec.DoubleValue TRAIL_DECAY = BUILDER
            .comment("How fast the trails fade. Must be greater than 0.")
            .defineInRange("liquidTrailDecay", 4, Double.MIN_VALUE, Double.MAX_VALUE);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static int trailLength;
    public static double trailDecay;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        trailLength = TRAIL_LENGTH.get();
        trailDecay = TRAIL_DECAY.get();
    }
}
