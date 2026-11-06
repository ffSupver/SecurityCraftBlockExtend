package com.ffsupver.securityCraftBlockExtend.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SetChainedFilterItemGhostItemPacket {
    private final ItemStack itemStack;
    private final int slotIndex;
    public SetChainedFilterItemGhostItemPacket(ItemStack itemStack, int slotIndex){
        this.itemStack = itemStack;
        this.slotIndex = slotIndex;
    }
    public SetChainedFilterItemGhostItemPacket(FriendlyByteBuf byteBuf){
        this.itemStack = byteBuf.readItem();
        this.slotIndex = byteBuf.readInt();
    }

    public void encode(FriendlyByteBuf byteBuf){
        byteBuf.writeItem(this.itemStack);
        byteBuf.writeInt(this.slotIndex);
    }

    public static void handle(SetChainedFilterItemGhostItemPacket msg,Supplier<NetworkEvent.Context> contextSupplier) {
        contextSupplier.get().enqueueWork(()->{
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;

            if (!(player.containerMenu instanceof ChainedUniversalBlockReinforcerMenu menu)) return;
            menu.setGhostItem(msg.slotIndex, msg.itemStack);
        });
    }
}
