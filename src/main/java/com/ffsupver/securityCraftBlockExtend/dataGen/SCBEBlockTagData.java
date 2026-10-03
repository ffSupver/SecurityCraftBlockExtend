package com.ffsupver.securityCraftBlockExtend.dataGen;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.function.Supplier;

/**
 * 一个方块需要加入哪些方块标签。
 * 使用 Supplier 包裹方块，避免在数据生成之前加载可选模组的类。
 */
public record SCBEBlockTagData(
        Supplier<? extends Block> block,
        List<TagKey<Block>> tags
) {
    public static SCBEBlockTagData mineWithPickaxe(Supplier<Block> block){
        return new SCBEBlockTagData(block,List.of(BlockTags.MINEABLE_WITH_PICKAXE));
    }
}