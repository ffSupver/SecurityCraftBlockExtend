package com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer;

import com.ffsupver.securityCraftBlockExtend.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.ffsupver.securityCraftBlockExtend.item.ChainedUniversalBlockReinforcer.ChainedUniversalBlockReinforcerItem.tryReinforcedBlock;

public class ChainedUniversalReinforcePacketHandler {

    public static void handle(ChainedUniversalReinforcePacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();

        ServerPlayer player = ctx.getSender();
        if (player == null) return;

        ServerLevel level = player.serverLevel();
        BlockPos pos = packet.getPos();

        if (!level.isLoaded(pos)) return;

        BlockPos playerPos = player.blockPosition();
        if (!playerPos.closerThan(pos, Config.chainedReinforcerInteractRange))
            return;

        tryReinforcedBlock(player, level, pos);
    }
}
