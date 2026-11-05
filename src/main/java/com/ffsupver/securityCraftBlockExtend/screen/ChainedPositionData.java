package com.ffsupver.securityCraftBlockExtend.screen;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ChainedPositionData(int x, int y, int z, int posX, int posY, int posZ) {
    public static final String NBT_TAG = "position_data";
    public static final ChainedPositionData ZERO = new ChainedPositionData(0, 0, 0, 0, 0, 0);
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

    /**
     * 是否为空，大小为0
     */
    public boolean isEmpty() {
        return x == 0 || y == 0 || z == 0;
    }

    public boolean isValid(int max) {
        return x >= 0 && y >= 0 && z >= 0
                && posX >= 0 && posY >= 0 && posZ >= 0
                && x <= max && y <= max && z <= max
                && posX <= max && posY <= max && posZ <= max
                && x >= posX && y >= posY && z >= posZ;
    }

    /**
     * 修正到合法区间。
     * 规则：先 clamp 到 [0, max]；再保证 x>=posX / y>=posY / z>=posZ
     * （出现冲突时压低 pos 而不是抬高 min，与"输入值过大变成最大值"的习惯一致）。
     */
    public ChainedPositionData clamp(int max) {
        int cx  = Mth.clamp(x,    0, max);
        int cy  = Mth.clamp(y,    0, max);
        int cz  = Mth.clamp(z,    0, max);
        int cpx = Mth.clamp(posX, 0, max);
        int cpy = Mth.clamp(posY, 0, max);
        int cpz = Mth.clamp(posZ, 0, max);
        if (cpx > cx) cpx = cx;
        if (cpy > cy) cpy = cy;
        if (cpz > cz) cpz = cz;
        return new ChainedPositionData(cx, cy, cz, cpx, cpy, cpz);
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
