package com.ffsupver.securityCraftBlockExtend.compat.quark;

import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.violetmoon.quark.content.building.block.VerticalSlabBlock;

import java.util.function.Supplier;

import static org.violetmoon.quark.content.building.block.VerticalSlabBlock.TYPE;
import static org.violetmoon.quark.content.building.block.VerticalSlabBlock.WATERLOGGED;

public class ReinforcedVerticalSlab extends BaseReinforcedBlock implements SimpleWaterloggedBlock {
    public ReinforcedVerticalSlab(Properties properties, Block vB) {
        this(properties, ()->vB);
    }

    public ReinforcedVerticalSlab(Properties properties, Supplier<Block> vB) {
        super(properties, vB);
        registerDefaultState(defaultBlockState().setValue(TYPE, VerticalSlabBlock.VerticalSlabType.NORTH).setValue(WATERLOGGED,false));
    }

    @NotNull
    @Override
    public BlockState rotate(BlockState state, @NotNull Rotation rot) {
        return state.getValue(TYPE) == VerticalSlabBlock.VerticalSlabType.DOUBLE ? state : state.setValue(TYPE, VerticalSlabBlock.VerticalSlabType.fromDirection(rot.rotate(state.getValue(TYPE).direction)));
    }

    @NotNull
    @Override
    public BlockState mirror(BlockState state, @NotNull Mirror mirrorIn) {
        VerticalSlabBlock.VerticalSlabType type = state.getValue(TYPE);
        if(type == VerticalSlabBlock.VerticalSlabType.DOUBLE || mirrorIn == Mirror.NONE)
            return state;

        if((mirrorIn == Mirror.LEFT_RIGHT && type.direction.getAxis() == Direction.Axis.Z)
                || (mirrorIn == Mirror.FRONT_BACK && type.direction.getAxis() == Direction.Axis.X))
            return state.setValue(TYPE, VerticalSlabBlock.VerticalSlabType.fromDirection(state.getValue(TYPE).direction.getOpposite()));

        return state;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return state.getValue(TYPE) != VerticalSlabBlock.VerticalSlabType.DOUBLE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder.add(TYPE,WATERLOGGED));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TYPE).shape;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = context.getLevel().getBlockState(blockpos);
        if(blockstate.getBlock() == this)
            return blockstate.setValue(TYPE, VerticalSlabBlock.VerticalSlabType.DOUBLE).setValue(WATERLOGGED, false);

        FluidState fluid = context.getLevel().getFluidState(blockpos);
        BlockState retState = defaultBlockState().setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
        Direction direction = getDirectionForPlacement(context);
        VerticalSlabBlock.VerticalSlabType type = VerticalSlabBlock.VerticalSlabType.fromDirection(direction);

        return retState.setValue(TYPE, type);
    }

    private Direction getDirectionForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        if(direction.getAxis() != Direction.Axis.Y)
            return direction;

        BlockPos pos = context.getClickedPos();
        Vec3 vec = context.getClickLocation().subtract(new Vec3(pos.getX(), pos.getY(), pos.getZ())).subtract(0.5, 0, 0.5);
        double angle = Math.atan2(vec.x, vec.z) * -180.0 / Math.PI;
        return Direction.fromYRot(angle).getOpposite();
    }

    @Override
    public boolean canBeReplaced(BlockState state, @NotNull BlockPlaceContext useContext) {
        ItemStack itemstack = useContext.getItemInHand();
        VerticalSlabBlock.VerticalSlabType slabtype = state.getValue(TYPE);
        return slabtype != VerticalSlabBlock.VerticalSlabType.DOUBLE && itemstack.getItem() == this.asItem() &&
                (useContext.replacingClickedOnBlock() && (useContext.getClickedFace() == slabtype.direction && getDirectionForPlacement(useContext) == slabtype.direction) ||
                        (!useContext.replacingClickedOnBlock() && useContext.getClickedFace() != slabtype.direction));
    }

    @NotNull
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor worldIn, @NotNull BlockPos pos, BlockState state, @NotNull FluidState fluidStateIn) {
        return state.getValue(TYPE) != VerticalSlabBlock.VerticalSlabType.DOUBLE && SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public boolean canPlaceLiquid(@NotNull BlockGetter worldIn, @NotNull BlockPos pos, BlockState state, @NotNull Fluid fluidIn) {
        return state.getValue(TYPE) != VerticalSlabBlock.VerticalSlabType.DOUBLE && SimpleWaterloggedBlock.super.canPlaceLiquid(worldIn, pos, state, fluidIn);
    }

    @NotNull
    @Override
    public BlockState updateShape(@NotNull BlockState stateIn, @NotNull Direction facing, @NotNull BlockState facingState, @NotNull LevelAccessor worldIn, @NotNull BlockPos currentPos, @NotNull BlockPos facingPos) {
        if(stateIn.getValue(WATERLOGGED))
            worldIn.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(worldIn));

        return super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos, @NotNull PathComputationType type) {
        return type == PathComputationType.WATER && worldIn.getFluidState(pos).is(FluidTags.WATER);
    }
}
