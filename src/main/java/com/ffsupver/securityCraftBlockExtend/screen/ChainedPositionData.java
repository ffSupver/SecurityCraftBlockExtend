package com.ffsupver.securityCraftBlockExtend.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ChainedPositionData(int x, int y, int z, int posX, int posY, int posZ) {
    public static final String NBT_TAG = "position_data";
    public CompoundTag toNbt(){
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("z", z);
        nbt.putInt("posX", posX);
        nbt.putInt("posY", posY);
        nbt.putInt("posZ", posZ);
        return nbt;
    }

    public static ChainedPositionData fromNbt(CompoundTag nbt){
        return new ChainedPositionData(
                nbt.getInt("x"),
                nbt.getInt("y"),
                nbt.getInt("z"),
                nbt.getInt("posX"),
                nbt.getInt("posY"),
                nbt.getInt("posZ")
        );
    }
    public static class Packet{
        private final ChainedPositionData chainedPositionData;
        public Packet(ChainedPositionData chainedPositionData){
            this.chainedPositionData = chainedPositionData;
        }
        public Packet(FriendlyByteBuf byteBuf){
            this.chainedPositionData = new ChainedPositionData(
                    byteBuf.readInt(),
                    byteBuf.readInt(),
                    byteBuf.readInt(),
                    byteBuf.readInt(),
                    byteBuf.readInt(),
                    byteBuf.readInt()
            );
        }
        public void encode(FriendlyByteBuf buf) {
            buf.writeInt(chainedPositionData.x());
            buf.writeInt(chainedPositionData.y());
            buf.writeInt(chainedPositionData.z());
            buf.writeInt(chainedPositionData.posX());
            buf.writeInt(chainedPositionData.posY());
            buf.writeInt(chainedPositionData.posZ());
        }

        public static void handle(ChainedPositionData.Packet msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer player = ctx.get().getSender();
                if (player == null) return;

                if (!(player.containerMenu instanceof ChainedUniversalBlockReinforcerMenu menu)) return;

                menu.applyClientData(msg.chainedPositionData);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
