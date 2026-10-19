package com.ffsupver.securityCraftBlockExtend.compat.quark;

import com.ffsupver.securityCraftBlockExtend.mixin.ButtonBlockAccessor;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.minecraft.world.level.block.ButtonBlock;

public class ReinforcedZetaButtonBlock extends ReinforcedButtonBlock {
    public ReinforcedZetaButtonBlock(Properties properties, ButtonBlock vb) {
        super(properties, vb, ((ButtonBlockAccessor)vb).getType(), ((ButtonBlockAccessor)vb).getTicksToStayPressed(), ((ButtonBlockAccessor)vb).getArrowsCanPress());
    }
}
