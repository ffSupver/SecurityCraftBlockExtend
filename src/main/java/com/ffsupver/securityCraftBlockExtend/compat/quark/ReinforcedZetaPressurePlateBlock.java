package com.ffsupver.securityCraftBlockExtend.compat.quark;

import com.ffsupver.securityCraftBlockExtend.mixin.BasePressurePlateBlockAccessor;
import com.ffsupver.securityCraftBlockExtend.mixin.PressurePlateBlockAccessor;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPressurePlateBlock;
import net.minecraft.world.level.block.PressurePlateBlock;

public class ReinforcedZetaPressurePlateBlock extends ReinforcedPressurePlateBlock {
    public ReinforcedZetaPressurePlateBlock(Properties properties, PressurePlateBlock vanillaBlock) {
        super(((PressurePlateBlockAccessor)vanillaBlock).getSensitivity(), properties, vanillaBlock, ((BasePressurePlateBlockAccessor)vanillaBlock).getType());
    }
}
