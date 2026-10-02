package com.ffsupver.securityCraftBlockExtend.dataGen;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public record SCBEBlockLootData(
        Supplier<? extends Block> block,
        BlockLootType type
) {
    public enum BlockLootType {
        /** 掉落自身 */
        DROP_SELF,
        /** 无掉落 */
        NO_DROP,
        /** 台阶掉落 根据BlockState */
        SLAB
    }
    public static SCBEBlockLootData dropSelf(Supplier<? extends Block> block){
        return new SCBEBlockLootData(block, BlockLootType.DROP_SELF);
    }
}