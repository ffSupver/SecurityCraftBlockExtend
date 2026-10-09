package com.ffsupver.securityCraftBlockExtend.compat.simplyLight;

import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

import static com.flanks255.simplylight.blocks.LampBlock.ON;

public class ReinforcedLampBlock extends BaseReinforcedBlock {
    public final boolean Default;
    public final DyeColor color;
    public ReinforcedLampBlock(boolean Default, DyeColor color, Properties properties, Supplier<Block> vB) {
        super(properties,vB);
        this.Default = Default;
        this.color = color;

        this.registerDefaultState(this.defaultBlockState().setValue(ON, Default));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(ON));
    }

    @Nullable
    public BlockState getStateForPlacement(@Nonnull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ON, this.Default);
    }

    public void setPlacedBy(@Nonnull Level pLevel, @Nonnull BlockPos pPos, @Nonnull BlockState pState, @Nullable LivingEntity pPlacer, @Nonnull ItemStack pStack) {
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
        boolean powered = pLevel.hasNeighborSignal(pPos);
        if (powered) {
            pLevel.setBlockAndUpdate(pPos, this.defaultBlockState().setValue(ON, !this.Default));
        }

    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pNeighborBlock, BlockPos pNeighborPos, boolean pMovedByPiston) {
        super.neighborChanged(pState, pLevel, pPos, pNeighborBlock, pNeighborPos, pMovedByPiston);
        boolean on = this.Default != pLevel.hasNeighborSignal(pPos);
        pLevel.setBlockAndUpdate(pPos, this.defaultBlockState().setValue(ON, on));
    }

    @Override
    public boolean isSignalSource(BlockState pState) {
        return true;
    }
}
