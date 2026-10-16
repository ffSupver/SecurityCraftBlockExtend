package com.ffsupver.securityCraftBlockExtend.compat.quark;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class QuarkBlocksCapture {
    public static final Map<ResourceLocation, Block> QUARK_BLOCKS = new HashMap<>();

    public static void capture(ResourceLocation id, Block block) {
        QUARK_BLOCKS.put(id, block);
    }
}
