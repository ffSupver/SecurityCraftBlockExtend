package com.ffsupver.securityCraftBlockExtend.compat.create.blocks.reinforcedTrack;

import com.simibubi.create.content.trains.track.TrackBlockEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class ReinforcedTrackEntity extends TrackBlockEntity implements IOwnable {
    private final Owner owner = new Owner();
    public ReinforcedTrackEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        owner.load(tag);
        super.read(tag, clientPacket);
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        owner.save(tag, needsValidation());
        super.write(tag, clientPacket);
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
