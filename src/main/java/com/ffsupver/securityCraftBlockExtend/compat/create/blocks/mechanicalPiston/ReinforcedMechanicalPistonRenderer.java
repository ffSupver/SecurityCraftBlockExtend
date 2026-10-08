package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston;

import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedMechanicalPistonRenderer extends KineticBlockEntityRenderer<ReinforcedMechanicalPistonBlockEntity> {
    public ReinforcedMechanicalPistonRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected BlockState getRenderedBlockState(ReinforcedMechanicalPistonBlockEntity be) {
        return shaft(getRotationAxisOf(be));
    }
}
