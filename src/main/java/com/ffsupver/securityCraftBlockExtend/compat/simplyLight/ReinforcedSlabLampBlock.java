package com.ffsupver.securityCraftBlockExtend.compat.simplyLight;

import com.flanks255.simplylight.blocks.ThinLamp;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ReinforcedSlabLampBlock extends RotatableReinforcedBlock{
    private final double thickness;


    public ReinforcedSlabLampBlock(double thickness,Properties properties, Supplier<Block> vB) {
        super(properties, vB);
        this.thickness = thickness;
        this.UP = Block.box(0.0F, 0.0F, 0.0F, 16.0F, thickness, 16.0F);
        this.DOWN = Block.box(0.0F, (double)16.0F - thickness, 0.0F, 16.0F, 16.0F, 16.0F);
        this.EAST = Block.box(0.0F, 0.0F, 0.0F, thickness, 16.0F, 16.0F);
        this.WEST = Block.box((double)16.0F - thickness, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F);
        this.NORTH = Block.box(0.0F, 0.0F, (double)16.0F - thickness, 16.0F, 16.0F, 16.0F);
        this.SOUTH = Block.box(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, thickness);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext useContext) {
        BlockPos pos = useContext.getClickedPos().relative(useContext.getClickedFace().getOpposite());
        boolean waterlogged = useContext.getLevel().getFluidState(useContext.getClickedPos()).getType() == Fluids.WATER;
        return useContext.getPlayer() != null && useContext.getLevel().getBlockState(pos).getBlock() instanceof ThinLamp && !useContext.getPlayer().isCrouching() ?
                this.defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, waterlogged).setValue(BlockStateProperties.FACING, useContext.getLevel().getBlockState(pos).getValue(BlockStateProperties.FACING)) : this.defaultBlockState().setValue(BlockStateProperties.FACING, useContext.getClickedFace()).setValue(BlockStateProperties.WATERLOGGED, waterlogged);
    }
}
