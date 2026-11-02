package com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ChainedUniversalReinforcePacket {
    private final BlockPos pos;

    public ChainedUniversalReinforcePacket(BlockPos pos) {
        this.pos = pos;
    }

    public ChainedUniversalReinforcePacket(FriendlyByteBuf buf) {
        this(buf.readBlockPos());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
