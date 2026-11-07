package com.ffsupver.securityCraftBlockExtend.compat.quark;

import com.ffsupver.securityCraftBlockExtend.compat.Mods;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedChainBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.content.building.block.WoodPostBlock;

import static org.violetmoon.quark.content.building.block.WoodPostBlock.SIDES;

public class ReinforcedWoodPostBlock extends BaseReinforcedBlock implements SimpleWaterloggedBlock {

    private static final float START = 0F;
    private static final float END = 16F;
    private static final float LEFT_EDGE = 6F;
    private static final float RIGHT_EDGE = 10F;

    private static final VoxelShape CENTER_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);

    private static final VoxelShape DOWN_SHAPE = Block.box(LEFT_EDGE, START, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);
    private static final VoxelShape UP_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, END, RIGHT_EDGE);
    private static final VoxelShape NORTH_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, START, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);
    private static final VoxelShape SOUTH_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, END);
    private static final VoxelShape WEST_SHAPE = Block.box(START, LEFT_EDGE, LEFT_EDGE, RIGHT_EDGE, RIGHT_EDGE, RIGHT_EDGE);
    private static final VoxelShape EAST_SHAPE = Block.box(LEFT_EDGE, LEFT_EDGE, LEFT_EDGE, END, RIGHT_EDGE, RIGHT_EDGE);

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    private static final VoxelShape[] SIDE_BOXES = new VoxelShape[] {
            DOWN_SHAPE, UP_SHAPE, NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE
    };

    private static final VoxelShape[] SHAPE_CACHE = new VoxelShape[64 * 3];

    public ReinforcedWoodPostBlock(Properties properties, Block vB) {
        super(properties, vB);
        BlockState state = stateDefinition.any().setValue(WATERLOGGED, false).setValue(AXIS, Direction.Axis.Y);
        for(EnumProperty<WoodPostBlock.PostSideType> prop : SIDES)
            state = state.setValue(prop, WoodPostBlock.PostSideType.NONE);
        registerDefaultState(state);
    }

    @Override
    public VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        int index = 0;
        for(Direction dir : Direction.values()) {
            if(state.getValue(SIDES[dir.ordinal()]).isSolid())
                index += (1 << dir.ordinal());
        }
        index += (64 * state.getValue(AXIS).ordinal());

        VoxelShape cached = SHAPE_CACHE[index];
        if(cached == null) {
            VoxelShape currShape = CENTER_SHAPE;

            for(Direction dir : Direction.values()) {
                boolean connected = isConnected(state, dir);
                if(connected)
                    currShape = Shapes.or(currShape, SIDE_BOXES[dir.ordinal()]);
            }
            SHAPE_CACHE[index] = currShape;
            cached = currShape;
        }

        return cached;
    }

    private boolean isConnected(BlockState state, Direction dir) {
        if(state.getValue(AXIS) == dir.getAxis())
            return true;

        return state.getValue(SIDES[dir.ordinal()]).isSolid();
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter pLevel, BlockPos pPos) {
        return !state.getValue(WATERLOGGED);
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getClickedFace().getAxis();
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        BlockState state = defaultBlockState().setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER)
                .setValue(AXIS, axis);

        for(Direction d : Direction.values()) {
            if(axis != d.getAxis()) {
                state = state.setValue(SIDES[d.ordinal()], getSideType(level, pos, d));
            }
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos facingPos) {
        if(state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        state = state.setValue(SIDES[facing.ordinal()], getSideType(level, pos, facing));
        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, AXIS);
        for(EnumProperty<WoodPostBlock.PostSideType> prop : SIDES)
            builder.add(prop);
        super.createBlockStateDefinition(builder);
    }

    private static WoodPostBlock.PostSideType getSideType(LevelAccessor world, BlockPos pos, Direction d) {
        BlockState sideState = world.getBlockState(pos.relative(d));

        if((sideState.getBlock() instanceof ChainBlock && sideState.getValue(BlockStateProperties.AXIS) == d.getAxis())
                || (sideState.getBlock() instanceof ReinforcedChainBlock && sideState.getValue(BlockStateProperties.AXIS) == d.getAxis())
                || (d == Direction.DOWN && sideState.getBlock() instanceof LanternBlock && sideState.getValue(LanternBlock.HANGING))
                || (d == Direction.DOWN && sideState.getBlock() instanceof CeilingHangingSignBlock)) {

            return WoodPostBlock.PostSideType.CHAIN;
        }

        if((sideState.getBlock() instanceof ReinforcedWoodPostBlock || sideState.getBlock() instanceof WoodPostBlock) && sideState.getValue(AXIS) == d.getAxis()) {
            return  WoodPostBlock.PostSideType.OTHER_POST;
        }
        return WoodPostBlock.PostSideType.NONE;
    }

    public static boolean canBelowSurvive(boolean prev, BlockState state, LevelReader worldIn, BlockPos pos) {
        return prev ||
                Mods.isModLoad(Mods.ModIds.QUARK.ModId) &&(!state.hasProperty(LanternBlock.HANGING) || state.getValue(LanternBlock.HANGING)) && worldIn.getBlockState(pos.above()).getBlock() instanceof ReinforcedWoodPostBlock;
    }
}
