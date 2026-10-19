package com.ffsupver.securityCraftBlockExtend.compat.quark;

import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedLadderBlock;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class ReinforcedVariantLadderBlock extends ReinforcedLadderBlock {
    private final Supplier<Block> vanillaBlock;
    public ReinforcedVariantLadderBlock(Properties properties,Supplier<Block> vanillaBlock) {
        super(properties);
        this.vanillaBlock = vanillaBlock;
    }

    @Override
    public Block getVanillaBlock() {
        return vanillaBlock.get();
    }
}
