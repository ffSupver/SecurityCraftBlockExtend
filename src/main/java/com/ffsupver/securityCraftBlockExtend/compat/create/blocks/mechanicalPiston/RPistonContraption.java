package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston;

import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.ContraptionType;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.TranslatingContraption;
import com.simibubi.create.content.contraptions.piston.PistonExtensionPoleBlock;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.createmod.catnip.math.VecHelper;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import static com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston.ReinforcedMechanicalPistonBlock.*;
import static com.simibubi.create.AllBlocks.MECHANICAL_PISTON_HEAD;
import static com.simibubi.create.AllBlocks.PISTON_EXTENSION_POLE;
import static com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock.PistonState.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class RPistonContraption extends TranslatingContraption {
    private Owner pistonOwner;

    protected int extensionLength;
    protected int initialExtensionProgress;
    protected Direction orientation;

    private AABB pistonExtensionCollisionBox;
    private boolean retract;

    @Override
    public ContraptionType getType() {
        return ReinforcedContraptionTypes.REINFORCED_PISTON.value();
    }

    public RPistonContraption() {
    }

    public RPistonContraption(Direction direction, boolean retract,Owner pistonOwner) {
        orientation = direction;
        this.retract = retract;
        this.pistonOwner = pistonOwner;
    }

    @Override
    protected boolean movementAllowed(BlockState state, Level world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof IOwnable blockOwnable) {
            // 目标是可拥有方块（SecurityCraft 系）
            // 只有当活塞本身也有 owner 且两者匹配时才允许推动
            if (pistonOwner == null)
                return false;
            return pistonOwner.isTreatedTheSameAs(blockOwnable.getOwner());
        }
        // 非 IOwnable 方块：走 Create 原生逻辑
        return super.movementAllowed(state, world, pos);
    }

    @Override
    public boolean assemble(Level world, BlockPos pos) throws AssemblyException {
        if (!collectExtensions(world, pos, orientation))
            return false;
        int count = blocks.size();
        if (!searchMovedStructure(world, anchor, retract ? orientation.getOpposite() : orientation))
            return false;
        if (blocks.size() == count) { // no new blocks added
            bounds = pistonExtensionCollisionBox;
        } else {
            bounds = bounds.minmax(pistonExtensionCollisionBox);
        }
        startMoving(world);
        return true;
    }

    private boolean collectExtensions(Level world, BlockPos pos, Direction direction) throws AssemblyException {
        List<StructureTemplate.StructureBlockInfo> poles = new ArrayList<>();
        BlockPos actualStart = pos;
        BlockState nextBlock = world.getBlockState(actualStart.relative(direction));
        int extensionsInFront = 0;
        BlockState blockState = world.getBlockState(pos);
        boolean sticky = isStickyPiston(blockState);

        if (!isPiston(blockState))
            return false;

        if (blockState.getValue(ReinforcedMechanicalPistonBlock.STATE) == EXTENDED) {
            while (PistonExtensionPoleBlock.PlacementHelper.get().matchesAxis(nextBlock, direction.getAxis()) || isPistonHead(nextBlock) && nextBlock.getValue(FACING) == direction) {

                actualStart = actualStart.relative(direction);
                poles.add(new StructureTemplate.StructureBlockInfo(actualStart, nextBlock.setValue(FACING, direction), null));
                extensionsInFront++;

                if (isPistonHead(nextBlock))
                    break;

                nextBlock = world.getBlockState(actualStart.relative(direction));
                if (extensionsInFront > ReinforcedMechanicalPistonBlock.maxAllowedPistonPoles())
                    throw AssemblyException.tooManyPistonPoles();
            }
        }

        if (extensionsInFront == 0)
            poles.add(new StructureTemplate.StructureBlockInfo(pos, MECHANICAL_PISTON_HEAD.getDefaultState()
                    .setValue(FACING, direction)
                    .setValue(BlockStateProperties.PISTON_TYPE, sticky ? PistonType.STICKY : PistonType.DEFAULT), null));
        else
            poles.add(new StructureTemplate.StructureBlockInfo(pos, PISTON_EXTENSION_POLE.getDefaultState()
                    .setValue(FACING, direction), null));

        BlockPos end = pos;
        nextBlock = world.getBlockState(end.relative(direction.getOpposite()));
        int extensionsInBack = 0;

        while (PistonExtensionPoleBlock.PlacementHelper.get().matchesAxis(nextBlock, direction.getAxis())) {
            end = end.relative(direction.getOpposite());
            poles.add(new StructureTemplate.StructureBlockInfo(end, nextBlock.setValue(FACING, direction), null));
            extensionsInBack++;
            nextBlock = world.getBlockState(end.relative(direction.getOpposite()));

            if (extensionsInFront + extensionsInBack > ReinforcedMechanicalPistonBlock.maxAllowedPistonPoles())
                throw AssemblyException.tooManyPistonPoles();
        }

        anchor = pos.relative(direction, initialExtensionProgress + 1);
        extensionLength = extensionsInBack + extensionsInFront;
        initialExtensionProgress = extensionsInFront;
        pistonExtensionCollisionBox = new AABB(
                BlockPos.ZERO.relative(direction, -1),
                BlockPos.ZERO.relative(direction, -extensionLength - 1)).expandTowards(1,
                1, 1);

        if (extensionLength == 0)
            throw AssemblyException.noPistonPoles();

        bounds = new AABB(0, 0, 0, 0, 0, 0);

        for (StructureTemplate.StructureBlockInfo pole : poles) {
            BlockPos relPos = pole.pos().relative(direction, -extensionsInFront);
            BlockPos localPos = relPos.subtract(anchor);
            getBlocks().put(localPos, new StructureTemplate.StructureBlockInfo(localPos, pole.state(), null));
            //pistonExtensionCollisionBox = pistonExtensionCollisionBox.union(new AABB(localPos));
        }

        return true;
    }

    @Override
    protected boolean isAnchoringBlockAt(BlockPos pos) {
        return pistonExtensionCollisionBox.contains(VecHelper.getCenterOf(pos.subtract(anchor)));
    }

    @Override
    protected boolean addToInitialFrontier(Level world, BlockPos pos, Direction direction, Queue<BlockPos> frontier) throws AssemblyException {
        frontier.clear();
        boolean sticky = isStickyPiston(world.getBlockState(pos.relative(orientation, -1)));
        boolean retracting = direction != orientation;
        if (retracting && !sticky) {
            return true;
        }
        for (int offset = 0; offset <= AllConfigs.server().kinetics.maxChassisRange.get(); offset++) {
            if (offset == 1 && retracting) {
                return true;
            }
            BlockPos currentPos = pos.relative(orientation, offset + initialExtensionProgress);
            if (retracting && world.isOutsideBuildHeight(currentPos)){
                return true;
            }
            if (!world.isLoaded(currentPos))
                throw AssemblyException.unloadedChunk(currentPos);
            BlockState state = world.getBlockState(currentPos);
            if (!BlockMovementChecks.isMovementNecessary(state, world, currentPos)){
                return true;
            }
            if (BlockMovementChecks.isBrittle(state) && !(state.getBlock() instanceof WoolCarpetBlock)) {
                return true;
            }
            if (isPistonHead(state) && state.getValue(FACING) == direction.getOpposite()) {
                return true;
            }
            if (!movementAllowed(state, world, currentPos)) {
                if (retracting) return true;
                else throw AssemblyException.unmovableBlock(currentPos, state);
            }
            if (retracting && state.getPistonPushReaction() == PushReaction.PUSH_ONLY) {
                return true;
            }
            frontier.add(currentPos);
            if (BlockMovementChecks.isNotSupportive(state, orientation)) {
                return true;
            }
        }
        return true;
    }

    @Override
    public void addBlock(Level level, BlockPos pos, Pair<StructureTemplate.StructureBlockInfo, BlockEntity> capture) {
        super.addBlock(level, pos.relative(orientation, -initialExtensionProgress), capture);
    }

    @Override
    public BlockPos toLocalPos(BlockPos globalPos) {
        return globalPos.subtract(anchor)
                .relative(orientation, -initialExtensionProgress);
    }

    @Override
    protected boolean customBlockPlacement(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockPos pistonPos = anchor.relative(orientation, -1);
        BlockState pistonState = world.getBlockState(pistonPos);
        BlockEntity be = world.getBlockEntity(pistonPos);
        if (pos.equals(pistonPos)) {
            if (be == null || be.isRemoved())
                return true;
            if (!isExtensionPole(state) && isPiston(pistonState))
                world.setBlock(pistonPos, pistonState.setValue(ReinforcedMechanicalPistonBlock.STATE, RETRACTED),
                        3 | 16);
            return true;
        }
        return false;
    }

    @Override
    protected boolean customBlockRemoval(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockPos pistonPos = anchor.relative(orientation, -1);
        BlockState blockState = world.getBlockState(pos);
        if (pos.equals(pistonPos) && isPiston(blockState)) {
            world.setBlock(pos, blockState.setValue(ReinforcedMechanicalPistonBlock.STATE, MOVING), 66 | 16);
            return true;
        }
        return false;
    }

    @Override
    public void readNBT(Level world, CompoundTag nbt, boolean spawnData) {
        super.readNBT(world, nbt, spawnData);
        initialExtensionProgress = nbt.getInt("InitialLength");
        extensionLength = nbt.getInt("ExtensionLength");
        orientation = Direction.from3DDataValue(nbt.getInt("Orientation"));
    }

    @Override
    public CompoundTag writeNBT(boolean spawnPacket) {
        CompoundTag tag = super.writeNBT(spawnPacket);
        tag.putInt("InitialLength", initialExtensionProgress);
        tag.putInt("ExtensionLength", extensionLength);
        tag.putInt("Orientation", orientation.get3DDataValue());
        return tag;
    }

}
