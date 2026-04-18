package com.normallynormal.void_water;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ClientTrailData {
    private static final Map<Long, Integer> TRAIL_LENGTHS = new HashMap<>();

    public static void applyUpdate(Map<Long, Integer> updates) {
        Minecraft mc = Minecraft.getInstance();
        for (Map.Entry<Long, Integer> entry : updates.entrySet()) {
            long packed = entry.getKey();
            if (entry.getValue() <= 0) {
                TRAIL_LENGTHS.remove(packed);
            } else {
                TRAIL_LENGTHS.put(packed, entry.getValue());
            }
            if (mc.levelRenderer != null) {
                int x = VoidTrailData.unpackX(packed);
                int z = VoidTrailData.unpackZ(packed);
                int minY = Util.getMinYForLevel();
                mc.levelRenderer.setSectionDirty(x >> 4, minY >> 4, z >> 4);
            }
        }
    }

    public static int getTrailLength(BlockPos pos) {
        return TRAIL_LENGTHS.getOrDefault(VoidTrailData.pack(pos.getX(), pos.getZ()), 0);
    }

    public static void clear() {
        TRAIL_LENGTHS.clear();
    }
}
