package com.ffsupver.securityCraftBlockExtend.compat.quark;

import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public class ReinforcedVariantBookshelfBlock extends BaseReinforcedBlock {
    public ReinforcedVariantBookshelfBlock(Properties properties, Block vB) {
        super(properties, vB);
    }

    public ReinforcedVariantBookshelfBlock(Properties properties, Supplier<Block> vB) {
        super(properties, vB);
    }

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return 1;
    }
}
