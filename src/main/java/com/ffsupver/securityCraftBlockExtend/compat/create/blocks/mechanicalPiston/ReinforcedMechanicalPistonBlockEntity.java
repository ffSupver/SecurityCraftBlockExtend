package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.DirectionalExtenderScrollOptionSlot;
import com.simibubi.create.content.contraptions.piston.LinearActuatorBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.ValueBoxTransform;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;

import static com.simibubi.create.content.contraptions.piston.MechanicalPistonBlock.PistonState.EXTENDED;

public class ReinforcedMechanicalPistonBlockEntity  extends LinearActuatorBlockEntity implements IOwnable {
    protected int extensionLength;
    private Owner owner = new Owner();

    public ReinforcedMechanicalPistonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        extensionLength = compound.getInt("ExtensionLength");
        owner.load(compound);
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        tag.putInt("ExtensionLength", extensionLength);
        if (owner != null){
            owner.save(tag,needsValidation());
        }
        super.write(tag, clientPacket);
    }

    @Override
    public void assemble() throws AssemblyException {
        if (!(level.getBlockState(worldPosition)
                .getBlock() instanceof ReinforcedMechanicalPistonBlock))
            return;

        Direction direction = getBlockState().getValue(BlockStateProperties.FACING);

        // Collect Construct
        RPistonContraption contraption = new RPistonContraption(direction, getMovementSpeed() < 0,owner);
        if (!contraption.assemble(level, worldPosition)) {
            return;
        }
        Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, direction.getAxis());
        Direction movementDirection =
                getSpeed() > 0 ^ direction.getAxis() != Direction.Axis.Z ? positive : positive.getOpposite();

        BlockPos anchor = contraption.anchor.relative(direction, contraption.initialExtensionProgress);
        if (ContraptionCollider.isCollidingWithWorld(level, contraption, anchor.relative(movementDirection),
                movementDirection)) {
            return;
        }

        // Check if not at limit already
        extensionLength = contraption.extensionLength;
        float resultingOffset = contraption.initialExtensionProgress + Math.signum(getMovementSpeed()) * .5f;
        if (resultingOffset <= 0 || resultingOffset >= extensionLength) {
            return;
        }

        // Run
        running = true;
        offset = contraption.initialExtensionProgress;
        sendData();
        clientOffsetDiff = 0;

        BlockPos startPos = BlockPos.ZERO.relative(direction, contraption.initialExtensionProgress);
        contraption.removeBlocksFromWorld(level, startPos);
        movedContraption = ControlledContraptionEntity.create(getLevel(), this, contraption);
        resetContraptionToOffset();
        forceMove = true;
        level.addFreshEntity(movedContraption);

        AllSoundEvents.CONTRAPTION_ASSEMBLE.playOnServer(level, worldPosition);

        if (contraption.containsBlockBreakers())
            award(AllAdvancements.CONTRAPTION_ACTORS);
    }

    @Override
    public void disassemble() {
        if (!running && movedContraption == null)
            return;
        if (!remove)
            getLevel().setBlock(worldPosition, getBlockState().setValue(ReinforcedMechanicalPistonBlock.STATE, EXTENDED),
                    3 | 16);
        if (movedContraption != null) {
            resetContraptionToOffset();
            movedContraption.disassemble();
            AllSoundEvents.CONTRAPTION_DISASSEMBLE.playOnServer(level, worldPosition);
        }
        running = false;
        movedContraption = null;
        sendData();

        if (remove)
            AllBlocks.MECHANICAL_PISTON.get()
                    .playerWillDestroy(level, worldPosition, getBlockState(), null);
    }

    @Override
    protected void collided() {
        super.collided();
        if (!running && getMovementSpeed() > 0)
            assembleNextTick = true;
    }

    @Override
    public float getMovementSpeed() {
        float movementSpeed = Mth.clamp(convertToLinear(getSpeed()), -.49f, .49f);
        if (level.isClientSide)
            movementSpeed *= ServerSpeedProvider.get();
        Direction pistonDirection = getBlockState().getValue(BlockStateProperties.FACING);
        int movementModifier = pistonDirection.getAxisDirection()
                .getStep() * (pistonDirection.getAxis() == Direction.Axis.Z ? -1 : 1);
        movementSpeed = movementSpeed * -movementModifier + clientOffsetDiff / 2f;

        int extensionRange = getExtensionRange();
        movementSpeed = Mth.clamp(movementSpeed, 0 - offset, extensionRange - offset);
        if (sequencedOffsetLimit >= 0)
            movementSpeed = (float) Mth.clamp(movementSpeed, -sequencedOffsetLimit, sequencedOffsetLimit);
        return movementSpeed;
    }

    @Override
    protected int getExtensionRange() {
        return extensionLength;
    }

    @Override
    protected void visitNewPosition() {
    }

    @Override
    protected Vec3 toMotionVector(float speed) {
        Direction pistonDirection = getBlockState().getValue(BlockStateProperties.FACING);
        return Vec3.atLowerCornerOf(pistonDirection.getNormal())
                .scale(speed);
    }

    @Override
    protected Vec3 toPosition(float offset) {
        Vec3 position = Vec3.atLowerCornerOf(getBlockState().getValue(BlockStateProperties.FACING)
                        .getNormal())
                .scale(offset);
        return position.add(Vec3.atLowerCornerOf(movedContraption.getContraption().anchor));
    }

    @Override
    protected ValueBoxTransform getMovementModeSlot() {
        return new DirectionalExtenderScrollOptionSlot((state, d) -> {
            Direction.Axis axis = d.getAxis();
            Direction.Axis extensionAxis = state.getValue(ReinforcedMechanicalPistonBlock.FACING)
                    .getAxis();
            Direction.Axis shaftAxis = ((IRotate) state.getBlock()).getRotationAxis(state);
            return extensionAxis != axis && shaftAxis != axis;
        });
    }

    @Override
    protected int getInitialOffset() {
        return movedContraption == null ? 0
                : (movedContraption.getContraption() instanceof RPistonContraption rPistonContraption ? rPistonContraption.initialExtensionProgress : 0);
    }

    @Override
    public Owner getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String uuid, String name) {
        owner.set(uuid, name);
        setChanged();
    }
}
