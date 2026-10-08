package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.mechanicalPiston;

import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.contraptions.AssemblyException;
import com.simibubi.create.content.contraptions.ContraptionCollider;
import com.simibubi.create.content.contraptions.ControlledContraptionEntity;
import com.simibubi.create.content.contraptions.piston.MechanicalPistonBlockEntity;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.utility.CreateLang;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;

public class ReinforcedMechanicalPistonBlockEntity
        extends MechanicalPistonBlockEntity implements IOwnable {

    private final Owner owner = new Owner();

    public ReinforcedMechanicalPistonBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);

        behaviours.remove(movementMode);

        movementMode = new ScrollOptionBehaviour<>(MovementMode.class,
                CreateLang.translateDirect("contraptions.movement_mode"),
                this, getMovementModeSlot()) {

            @Override
            public void setValueSettings(Player player, ValueSettings valueSetting, boolean ctrlDown) {
                Owner owner = ReinforcedMechanicalPistonBlockEntity.this.getOwner();
                if (owner == null || !owner.isTreatedTheSameAs(new Owner(player))) {
                    player.sendSystemMessage(
                            Component.translatable("messages.securitycraft:notOwned", owner != null ? owner.getName() : "???")
                                    .setStyle(Style.EMPTY.withColor(ChatFormatting.RED))
                    );
                    return;
                }
                super.setValueSettings(player, valueSetting, ctrlDown);
            }
        };
        movementMode.withCallback(t -> waitingForSpeedChange = false);
        behaviours.add(movementMode);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        owner.load(compound);
        super.read(compound, clientPacket);      // 处理 extensionLength
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        owner.save(tag, needsValidation());
        super.write(tag, clientPacket);
    }

    @Override
    public void assemble() throws AssemblyException {
        if (!(level.getBlockState(worldPosition).getBlock()
                instanceof ReinforcedMechanicalPistonBlock))
            return;

        Direction direction = getBlockState().getValue(BlockStateProperties.FACING);
        RPistonContraption contraption =
                new RPistonContraption(direction, getMovementSpeed() < 0, owner);
        if (!contraption.assemble(level, worldPosition))
            return;

        Direction positive = Direction.get(Direction.AxisDirection.POSITIVE, direction.getAxis());
        Direction movementDirection =
                getSpeed() > 0 ^ direction.getAxis() != Direction.Axis.Z
                        ? positive : positive.getOpposite();

        BlockPos anchor = contraption.anchor.relative(direction, contraption.initialExtensionProgress);
        if (ContraptionCollider.isCollidingWithWorld(level, contraption,
                anchor.relative(movementDirection), movementDirection))
            return;

        extensionLength = contraption.extensionLength;   // 继承自父类
        float resultingOffset = contraption.initialExtensionProgress
                + Math.signum(getMovementSpeed()) * .5f;
        if (resultingOffset <= 0 || resultingOffset >= extensionLength)
            return;

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
    protected int getInitialOffset() {
        if (movedContraption == null)
            return 0;
        var contraption = movedContraption.getContraption();
        return contraption instanceof RPistonContraption rp ? rp.initialExtensionProgress : 0;
    }

    @Override public Owner getOwner() { return owner; }
    @Override public void setOwner(String uuid, String name) { owner.set(uuid, name); setChanged(); }
}

