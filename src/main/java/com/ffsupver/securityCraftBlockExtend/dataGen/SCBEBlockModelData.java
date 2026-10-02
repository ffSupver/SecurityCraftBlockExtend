package com.ffsupver.securityCraftBlockExtend.dataGen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Map;
import java.util.function.Supplier;

/**
 * 用于数据生成的方块模型数据。
 * 用 Supplier 包裹，避免在数据生成阶段直接引用可选模组的类。
 */
public record SCBEBlockModelData(
        String name,
        Supplier<? extends Block> block,
        String parentModel,                      // 例如 "block/reinforced_cube_all"，securitycraft 命名空间
        Map<String, ResourceLocation> textures   // 占位符 -> 纹理
) {}