package com.ffsupver.securityCraftBlockExtend.compat.create.blocks;

import net.createmod.catnip.data.Iterate;
import net.geforcemods.securitycraft.blocks.reinforced.BaseReinforcedBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.AxisDirection;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.ticks.LevelTickAccess;

import java.util.function.Supplier;

/**
 * 强化柱体：保留 SecurityCraft 的强化行为，同时具备 Create ConnectedPillarBlock 的列连接逻辑，
 * 使 RotatedPillarCTBehaviour 能在轴上正常旋转纹理。
 */
public class ReinforcedConnectedPillarBlock extends BaseReinforcedBlock {

    public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty EAST  = BooleanProperty.create("east");
    public static final BooleanProperty WEST  = BooleanProperty.create("west");

    public ReinforcedConnectedPillarBlock(Properties properties, Block vanillaBlock) {
        super(properties, vanillaBlock);
        registerDefaultState(defaultBlockState()
                .setValue(AXIS,  Axis.Y)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST,  false)
                .setValue(WEST,  false));
    }

    public ReinforcedConnectedPillarBlock(Properties properties, Supplier<Block> vanillaBlock) {
        super(properties, vanillaBlock);
        registerDefaultState(defaultBlockState()
                .setValue(AXIS,  Axis.Y)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(EAST,  false)
                .setValue(WEST,  false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(AXIS, NORTH, SOUTH, EAST, WEST));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = defaultBlockState()
                .setValue(AXIS, ctx.getClickedFace().getAxis());
        return updateColumn(ctx.getLevel(), ctx.getClickedPos(), state, true);
    }

    private BlockState updateColumn(Level level, BlockPos pos, BlockState state, boolean present) {
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        Axis axis = state.getValue(AXIS);

        for (Direction connection : Iterate.directions) {
            if (connection.getAxis() == axis)
                continue;

            boolean connect = true;
            Move: for (Direction movement : Iterate.directionsInAxis(axis)) {
                currentPos.set(pos);
                for (int i = 0; i < 1000; i++) {
                    if (!level.isLoaded(currentPos))
                        break;

                    BlockState other1 = currentPos.equals(pos) ? state : level.getBlockState(currentPos);
                    BlockState other2 = level.getBlockState(currentPos.relative(connection));
                    boolean col1 = canConnect(state, other1);
                    boolean col2 = canConnect(state, other2);
                    currentPos.move(movement);

                    if (!col1 && !col2)
                        break;
                    if (col1 && col2)
                        continue;

                    connect = false;
                    break Move;
                }
            }
            state = setConnection(state, connection, connect);
        }
        return state;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() == this)
            return;
        LevelTickAccess<Block> ticks = level.getBlockTicks();
        if (!ticks.hasScheduledTick(pos, this))
            level.scheduleTick(pos, this, 1);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (state.getBlock() != this)
            return;
        BlockPos belowPos = pos.relative(
                Direction.fromAxisAndDirection(state.getValue(AXIS), AxisDirection.NEGATIVE));
        BlockState belowState = level.getBlockState(belowPos);
        if (!canConnect(state, belowState))
            level.setBlock(pos, updateColumn(level, pos, state, true), 3);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos currentPos, BlockPos neighborPos) {
        if (!canConnect(state, neighborState))
            return setConnection(state, direction, false);
        if (direction.getAxis() == state.getValue(AXIS))
            return withPropertiesOf(neighborState);
        return setConnection(state, direction, getConnection(neighborState, direction.getOpposite()));
    }

    protected boolean canConnect(BlockState state, BlockState other) {
        return other.getBlock() == this && state.getValue(AXIS) == other.getValue(AXIS);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (isMoving || newState.getBlock() == this)
            return;
        for (Direction d : Iterate.directionsInAxis(state.getValue(AXIS))) {
            BlockPos relative = pos.relative(d);
            BlockState adjacent = level.getBlockState(relative);
            if (canConnect(state, adjacent))
                level.setBlock(relative, updateColumn(level, relative, adjacent, false), 3);
        }
    }

    public static boolean getConnection(BlockState state, Direction side) {
        BooleanProperty property = connection(state.getValue(AXIS), side);
        return property != null && state.getValue(property);
    }

    public static BlockState setConnection(BlockState state, Direction side, boolean connect) {
        BooleanProperty property = connection(state.getValue(AXIS), side);
        if (property != null)
            state = state.setValue(property, connect);
        return state;
    }

    public static BooleanProperty connection(Axis axis, Direction side) {
        if (side.getAxis() == axis)
            return null;

        if (axis == Axis.X) {
            switch (side) {
                case UP:    return EAST;
                case NORTH: return NORTH;
                case SOUTH: return SOUTH;
                case DOWN:  return WEST;
                default:    return null;
            }
        }
        if (axis == Axis.Y) {
            switch (side) {
                case EAST:  return EAST;
                case NORTH: return NORTH;
                case SOUTH: return SOUTH;
                case WEST:  return WEST;
                default:    return null;
            }
        }
        if (axis == Axis.Z) {
            switch (side) {
                case UP:    return WEST;
                case WEST:  return SOUTH;
                case EAST:  return NORTH;
                case DOWN:  return EAST;
                default:    return null;
            }
        }
        return null;
    }
}